package com.adlitteram.emailcruncher;

import java.net.MalformedURLException;
import java.net.URL;

public class ExtURL {

    private final URL url;
    private final URL initUrl;
    private final int inLinkCount;
    private final int outLinkCount;

    public ExtURL(URL url) {
        this(url, url, 0, 0);
    }

    public ExtURL(URL url, URL initUrl, int ic, int oc) {
        this.url = url;
        this.initUrl = initUrl;
        this.inLinkCount = ic;
        this.outLinkCount = oc;
    }

    public URL getUrl() {
        return url;
    }

    public int getInLinkCount() {
        return inLinkCount;
    }

    public int getOutLinkCount() {
        return outLinkCount;
    }

    public ExtURL concatURL(String l) {
        try {
            URL linkUrl = new URL(l);
            if ("http".equalsIgnoreCase(linkUrl.getProtocol())) {
                if (initUrl.getHost().equalsIgnoreCase(linkUrl.getHost())) {
                    return new ExtURL(linkUrl, initUrl, inLinkCount + 1, outLinkCount);
                }
                else {
                    return new ExtURL(linkUrl, initUrl, inLinkCount, outLinkCount + 1);
                }
            }
        }
        catch (MalformedURLException ignored) {
        }

        try {
            URL linkUrl = new URL(url, l);
            if (initUrl.getHost().equalsIgnoreCase(linkUrl.getHost())) {
                return new ExtURL(linkUrl, initUrl, inLinkCount + 1, outLinkCount);
            }
            else {
                return new ExtURL(linkUrl, initUrl, inLinkCount, outLinkCount + 1);
            }

        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExtURL)) {
            return false;
        }

        ExtURL eu = (ExtURL) obj;
        return (this.getUrl().equals(eu.getUrl()));
    }

    public boolean isLocal() {
        return initUrl.getHost().equalsIgnoreCase(url.getHost());
    }

    @Override
    public String toString() {
        return url.toString();
    }

    @Override
    public int hashCode() {
        return url.toString().hashCode();
    }
}
