package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;

public class ScanningThread implements Runnable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-z0-9._-]+@[a-z0-9][a-z0-9.-]{0,61}[a-z0-9]\\.[a-z.]{2,6})");
    public static final int START = 0;
    public static final int RUN = 1;
    public static final int FINISH = 2;
    public static final int INIT = 3;

    private final Cruncher cruncher;
    private ExtURL url;
    private int status = INIT;
    private long time = System.currentTimeMillis();
    private boolean isStarted = false;
    private Thread thread;

    public ScanningThread(Cruncher cruncher) {
        this.cruncher = cruncher;
    }

    @Override
    public void run() {
        isStarted = true;

        while (true) {
            status = RUN;

            if (url != null && isRobotAllowed(url)) {

                InputStream urlStream = null;

                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) url.getUrl().openConnection(cruncher.getProxy());
                    urlConnection.setAllowUserInteraction(false);
                    urlConnection.setConnectTimeout(30000);
                    urlConnection.setReadTimeout(30000);
                    urlConnection.setInstanceFollowRedirects(true);

                    String type = urlConnection.getContentType();
                    if (type == null || type.contains("text/")) {
                        urlStream = urlConnection.getInputStream();
                        byte b[] = new byte[1024];
                        StringBuilder buffer = new StringBuilder(1024);
                        int numRead;

                        while ((status == RUN) && (numRead = urlStream.read(b)) != -1) {
                            buffer.append(new String(b, 0, numRead));
                            if (buffer.length() > 100000) {
                                break;
                            }
                            if (cruncher.getStatus() == Cruncher.STOP) {
                                status = FINISH;
                            }
                        }

                        if (status == RUN) {
                            String content = buffer.toString().toLowerCase();
                            int u = searchURL(content, url);
                            int e = searchEmail(content);
                            //XLog.getLogger().info(url + " - emails: " + e + " - url: " + u);
                        }
                    }
                }
                catch (IOException e) {
                    System.err.println(e.toString());
                }
                finally {
                    Utils.closeQuietly(urlStream);
                }
            }
            waiting();
        }
    }

    public synchronized void restart() {
        if (thread != null) {
            time = System.currentTimeMillis();
            notify();
        }
        else {
            start();
        }
    }

    public void start() {
        thread = new Thread(this);
        time = System.currentTimeMillis();
        thread.start();
    }

    public synchronized void waiting() {
        try {
            status = FINISH;
            cruncher.syncNotify();
            wait();
        }
        catch (InterruptedException ex) {
        }
    }

    public boolean isAlreadyStarted() {
        return isStarted;
    }

    private int searchEmail(String content) {
        int count = 0;
        Matcher m = EMAIL_PATTERN.matcher(content);
        while (m.find()) {
            final String address = m.group().trim();
            if (cruncher.getEmailFilterPattern() != null) {
                if (cruncher.getEmailFilterPattern().matcher(address).matches()) {
                    continue;
                }
            }
            count++;
            SwingUtilities.invokeLater(() -> {
                if (!cruncher.getEmailListModel().contains(address)) {
                    cruncher.incEmailCount();
                    cruncher.getEmailListModel().addElement(address);
                }
            });
        }
        return count;
    }

    private int searchURL(String content, ExtURL url) {
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

                cruncher.addUrlToSearch(urlLink);
                count++;
            }
        }

        return count;
    }

    private boolean isRobotAllowed(ExtURL url) {

        if (cruncher.isUseRobots()) {

            String strHost = url.getUrl().getHost();
            String strRobot = "http://" + strHost + "/robots.txt";
            BufferedReader in = null;

            try {
                URL urlRobot = new URL(strRobot);

                HttpURLConnection urlConnection = (HttpURLConnection) urlRobot.openConnection(cruncher.getProxy());
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setConnectTimeout(30000);
                urlConnection.setReadTimeout(30000);
                urlConnection.setInstanceFollowRedirects(true);

                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String urlPath = url.getUrl().getFile();
                String line;
                while ((line = in.readLine()) != null) {
                    int index = line.toLowerCase().indexOf("disallow:");
                    if (index > 0) {
                        if (urlPath.startsWith(line.substring(index))) {
                            return false;
                        }
                    }
                }
                in.close();
            }
            catch (IOException e) {
            }
            finally {
                Utils.closeQuietly(in);
            }
        }

        return true;
    }

    private static URL concatURL(URL url, String link) {
        try {
            URL linkUrl = new URL(link);
            if (linkUrl.getProtocol() != null) {
                return linkUrl;
            }
        }
        catch (MalformedURLException e) {
        }

        try {
            return new URL(url, link);
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public ExtURL getUrl() {
        return url;
    }

    public void setUrl(ExtURL url) {
        this.url = url;
    }
}
