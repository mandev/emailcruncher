/*
 * ExtUrl.java
 *
 * Created on 7 mai 2007, 12:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.adlitteram.emailcruncher;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author manu
 */
public class ExtURL {
    //~--- fields --------------------------------------------------------------

    private URL url;
    private URL initUrl;
    private int inLinkCount;
    private int outLinkCount;

    //~--- constructors --------------------------------------------------------
    /**
     * Creates a new instance of ExtUrl
     */
    public ExtURL(URL url) {
        this(url, url, 0, 0);
    }

    public ExtURL(URL url, URL initUrl, int ic, int oc) {
        this.url = url;
        this.initUrl = initUrl;
        this.inLinkCount = ic;
        this.outLinkCount = oc;
    }

    //~--- methods -------------------------------------------------------------
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
        String link = new String(l); // Don't keep the big original String
        try {
            URL linkUrl = new URL(link);
            if ("http".equalsIgnoreCase(linkUrl.getProtocol())) {
                if (initUrl.getHost().equalsIgnoreCase(linkUrl.getHost())) {
                    return new ExtURL(linkUrl, initUrl, inLinkCount + 1, outLinkCount);
                }
                else {
                    return new ExtURL(linkUrl, initUrl, inLinkCount, outLinkCount + 1);
                }
            }
        }
        catch (MalformedURLException e) {
        }

        try {
            URL linkUrl = new URL(url, link);
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
        int h = url.toString().hashCode();
        return h;
    }
}
