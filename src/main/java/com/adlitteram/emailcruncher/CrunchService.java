package com.adlitteram.emailcruncher;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CrunchService {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-z0-9._-]+@[a-z0-9][a-z0-9.-]{0,61}[a-z0-9]\\.[a-z]{2,6})");

    private final Cruncher cruncher;

    public CrunchService(Cruncher cruncher) {
        this.cruncher = cruncher;
    }

    public void scan(ExtURL url) {
        String content = getContent(url);
        if (content != null) {
            searchURL(content, url);
            searchEmail(content);
        }
    }

    protected String getContent(ExtURL url) {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url.getUri());
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                Header contentType = response.getFirstHeader("content-type");
                if (contentType != null && contentType.getValue().contains("text/")) {
                    HttpEntity entity = response.getEntity();
                    return entity == null ? null : EntityUtils.toString(entity);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return null;
    }

    protected int searchEmail(String content) {
        int count = 0;
        Matcher m = EMAIL_PATTERN.matcher(content.toLowerCase());
        while (m.find()) {
            final String email = m.group().trim();
            if (EMAIL_VALIDATOR.isValid(email)) {
                if (cruncher.getEmailFilterPattern() != null) {
                    if (cruncher.getEmailFilterPattern().matcher(email).matches()) {
                        continue;
                    }
                }
                count++;
                SwingUtilities.invokeLater(() -> {
                    if (!cruncher.getEmailListModel().contains(email)) {
                        cruncher.incEmailCount();
                        cruncher.getEmailListModel().addElement(email);
                    }
                });
            }
        }
        return count;
    }

    protected int searchURL(String content, ExtURL url) {
        int count = 0;

        if (cruncher.getPageFilter().length() > 0) {
            if (content.contains(cruncher.getPageFilter())) {
                return count;
            }
        }

        int index = 0;
        while ((index = content.indexOf("<a", index)) != -1) {

            index += 2;

            int hrefIndex = content.indexOf("href", index);
            if (hrefIndex == -1) {
                continue;
            }

            int equalIndex = content.indexOf("=", hrefIndex);
            if (equalIndex == -1) {
                continue;
            }

            int quoteIndex = content.indexOf("\"", equalIndex);
            if (quoteIndex == -1) {
                continue;
            }

            int index2 = quoteIndex + 1;
            if ((index2 = content.indexOf("\"", index2)) == -1) {
                continue;
            }
            String link = content.substring(quoteIndex + 1, index2).trim();

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

//    protected boolean isRobotAllowed(ExtURL url) {
//
//        if (cruncher.isUseRobots()) {
//
//            String strHost = url.getUrl().getHost();
//            String strRobot = "http://" + strHost + "/robots.txt";
//            BufferedReader in = null;
//
//            try {
//                URL urlRobot = new URL(strRobot);
//
//                HttpURLConnection urlConnection = (HttpURLConnection) urlRobot.openConnection(cruncher.getProxy());
//                urlConnection.setAllowUserInteraction(false);
//                urlConnection.setConnectTimeout(30000);
//                urlConnection.setReadTimeout(30000);
//                urlConnection.setInstanceFollowRedirects(true);
//
//                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//
//                String urlPath = url.getUrl().getFile();
//                String line;
//                while ((line = in.readLine()) != null) {
//                    int index = line.toLowerCase().indexOf("disallow:");
//                    if (index > 0) {
//                        if (urlPath.startsWith(line.substring(index))) {
//                            return false;
//                        }
//                    }
//                }
//                in.close();
//            } catch (IOException ignored) {
//            } finally {
//                Utils.closeQuietly(in);
//            }
//        }
//
//        return true;
//    }
}
