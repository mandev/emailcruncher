package com.adlitteram.emailcruncher;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CrunchService {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-z0-9._-]+@[a-z0-9.-]+\\.[a-z]{2,8})");

    private final Cruncher cruncher;

    public CrunchService(Cruncher cruncher) {
        this.cruncher = cruncher;
    }

    public void scan(ExtURL url) {
        var content = getContent(url);
        if (content != null) {
            searchURL(content, url);
            searchEmail(content);
        }
    }

    protected String getContent(ExtURL url) {

        try (var httpclient = HttpClients.createDefault()) {
            var httpGet = new HttpGet(url.getUri());
            try (var response = httpclient.execute(httpGet)) {
                var contentType = response.getFirstHeader("content-type");
                if (contentType != null && contentType.getValue().contains("text/")) {
                    var entity = response.getEntity();
                    return entity == null ? null : EntityUtils.toString(entity);
                }
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
        return null;
    }

    protected int searchEmail(String content) {
        var count = 0;

        if (content.contains("@")) {
            var m = EMAIL_PATTERN.matcher(content.toLowerCase());
            while (m.find()) {
                final var email = m.group().trim();
                if (EMAIL_VALIDATOR.isValid(email)) {
                    var filter = cruncher.getEmailFilterPattern();
                    if (filter == null || !filter.matcher(email).matches()) {
                        count++;
                        cruncher.addEmail(email);
                    }
                }
            }
        }
        return count;
    }

    protected int searchURL(String content, ExtURL url) {
        var count = 0;

        if (cruncher.getPageFilter().length() > 0) {
            if (content.contains(cruncher.getPageFilter())) {
                return count;
            }
        }

        var index = 0;
        while ((index = content.indexOf("<a", index)) != -1) {

            index += 2;

            var hrefIndex = content.indexOf("href", index);
            if (hrefIndex == -1) {
                continue;
            }

            var equalIndex = content.indexOf("=", hrefIndex);
            if (equalIndex == -1) {
                continue;
            }

            var quoteIndex = content.indexOf("\"", equalIndex);
            if (quoteIndex == -1) {
                continue;
            }

            var index2 = quoteIndex + 1;
            if ((index2 = content.indexOf("\"", index2)) == -1) {
                continue;
            }
            var link = content.substring(quoteIndex + 1, index2).trim();

            if (link.startsWith("mailto:")) {
                continue;
            }
            if (link.startsWith("'")) {
                continue;
            }
            if (link.startsWith("&")) {
                continue;
            }
            if ((index2 = link.indexOf("#")) != -1) {
                link = link.substring(0, index2);
            }
            if ((index2 = link.indexOf("<")) != -1) {
                link = link.substring(0, index2);
            }

            if (link.length() > 0) {

                link = link.replace('\\', '/').replace(" ", "");

                ExtURL urlLink = url.concatURL(link);

                if (urlLink == null) {
                    continue;
                }

                if (cruncher.getUrlFilterPattern() != null) {
                    if (cruncher.getUrlFilterPattern().matcher(urlLink.getUrl().toString()).matches()) {
                        continue;
                    }
                }

                if (cruncher.getSearchLimit() == Cruncher.SITE) {
                    if (!urlLink.isLocal()) {
                        continue;
                    }
                }

                if ((cruncher.getInLinkDepth() > 0) && (urlLink.getInLinkCount() > cruncher.getInLinkDepth())) {
                    continue;
                }
                if ((cruncher.getOutLinkDepth() > 0) && (urlLink.getOutLinkCount() > cruncher.getOutLinkDepth())) {
                    continue;
                }

                cruncher.processUrl(urlLink);
                count++;
            }
        }
        return count;
    }
}
