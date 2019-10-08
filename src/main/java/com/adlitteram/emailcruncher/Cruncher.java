package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.log.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultListModel;

public class Cruncher implements Runnable {

    private static final String URL_FILTER = "url_filter";
    private static final String PAGE_FILTER = "page_filter";
    private static final String EMAIL_FILTER = "email_filter";
    private static final String USE_ROBOTS = "use_robots";
    private static final String SEARCH_LIMIT = "search_limit";
    private static final String IN_LINK_DEPTH = "in_link_depth";
    private static final String OUT_LINK_DEPTH = "out_link_depth";
    private static final String TIME_OUT = "time_out";
    private static final String THREAD_MAX = "thread_max";
    private static final String USE_PROXY = "use_proxy";
    private static final String PROXY_HOST = "proxy_host";
    private static final String PROXY_PORT = "proxy_port";
    public static final int SITE = 0;
    public static final int ALL = 1;
    public static final int STOP = 0;
    public static final int RUN = 1;

    private Preferences prefs;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final DefaultListModel emailListModel = new DefaultListModel();
    private final Map<String, ExtURL> urlToSearch = Collections.synchronizedMap(new HashMap<>(10000));    // URLs to be searched
    private final Set<String> urlFound = Collections.synchronizedSet(new HashSet<>(100000));

    private final ArrayList<ScanningThread> threadList = new ArrayList<>();
    private Thread stopThread;
    private int threadCount;
    private int stoppedCount;
    private int emailCount;
    private long startTime;
    private int searchLimit = 0;
    private String urlFilter = "";
    private String pageFilter = "";
    private String emailFilter = "";
    private Pattern urlFilterPattern;
    private Pattern emailFilterPattern;
    private boolean useRobots = false;
    private int inLinkDepth = 0;
    private int outLinkDepth = 3;
    private int timeOut = 120;
    private int threadMax = 10;
    private boolean useProxy = false;
    private String proxyHost = "";
    private int proxyPort = 80;
    private Proxy proxy;
    private int status = STOP;

    public Cruncher() {
        init();
    }

    private void init() {
        prefs = Preferences.userNodeForPackage(Cruncher.class);

        setEmailFilter(prefs.get(EMAIL_FILTER, ""));
        setPageFilter(prefs.get(PAGE_FILTER, ""));
        setUrlFilter(prefs.get(URL_FILTER, ""));
        setUseRobots(prefs.getBoolean(USE_ROBOTS, false));

        setSearchLimit(prefs.getInt(SEARCH_LIMIT, 0));
        setInLinkDepth(prefs.getInt(IN_LINK_DEPTH, 3));
        setOutLinkDepth(prefs.getInt(OUT_LINK_DEPTH, 3));

        setTimeOut(prefs.getInt(TIME_OUT, 120));
        setThreadMax(prefs.getInt(THREAD_MAX, 10));

        setUseProxy(prefs.getBoolean(USE_PROXY, false));
        setProxyHost(prefs.get(PROXY_HOST, "proxy"));
        setProxyPort(prefs.getInt(PROXY_PORT, 80));
    }

    public void updatePreferences() {
        prefs.put(EMAIL_FILTER, getEmailFilter());
        prefs.put(PAGE_FILTER, getPageFilter());
        prefs.put(URL_FILTER, getUrlFilter());
        prefs.putBoolean(USE_ROBOTS, isUseRobots());

        prefs.putInt(SEARCH_LIMIT, getSearchLimit());
        prefs.putInt(IN_LINK_DEPTH, getInLinkDepth());
        prefs.putInt(OUT_LINK_DEPTH, getOutLinkDepth());

        prefs.putInt(TIME_OUT, getTimeOut());
        prefs.putInt(THREAD_MAX, getThreadMax());

        prefs.putBoolean(USE_PROXY, isUseProxy());
        prefs.put(PROXY_HOST, getProxyHost());
        prefs.putInt(PROXY_PORT, getProxyPort());
    }

    public void addUrlToSearch(ExtURL url) {
        if ( !urlFound.contains(url.toString()) && urlToSearch.size() < 100000) {
            urlToSearch.put(url.toString(), url);
        }
    }

    private ScanningThread getScanningThread() {
        for (int i = threadList.size() - 1; i >= 0; i--) {
            ScanningThread th = threadList.get(i);
            if (th.getStatus() == ScanningThread.FINISH) {
                return th;
            }
        }
        if (threadList.size() >= threadMax) {
            return null;
        }

        ScanningThread th = new ScanningThread(this);
        threadList.add(th);
        return th;
    }

    private boolean isThreadAvailable() {
        for (int i = threadList.size() - 1; i >= 0; i--) {
            ScanningThread th = threadList.get(i);
            if (th.getStatus() == ScanningThread.FINISH) {
                return true;
            }
            else if ((System.currentTimeMillis() - th.getTime()) > (timeOut * 1000L)) {
                stoppedCount++;
                threadList.remove(i);
                Log.info(th.getUrl() + " aborted - status: " + th.getStatus() + " - restarted: " + th.isAlreadyStarted());
                return true;
            }
        }
        return (threadList.size() < threadMax);
    }

    private int getRunningThread() {
        int r = 0;
        for (int i = threadList.size() - 1; i >= 0; i--) {
            ScanningThread th = threadList.get(i);
            if (th.getStatus() != ScanningThread.FINISH) {
                r++;
            }
        }
        return r;
    }

    private synchronized ExtURL getUrlToSearch() {

        while (!isThreadAvailable() || urlToSearch.isEmpty()) {
            try {
                wait(1000L);

                if (urlToSearch.isEmpty() && getRunningThread() == 0) {
                    return null;
                }
                if (status == STOP) {
                    return null;
                }
            }
            catch (InterruptedException e) {
                Log.info(e.toString());
            }
        }

        Collection<ExtURL> col = urlToSearch.values();
        synchronized (urlToSearch) {
            Iterator<ExtURL> it = col.iterator();
            ExtURL u = it.next();
            it.remove();
            return u;
        }
    }

    public synchronized void syncNotify() {
        notify();
    }

    public void run() {

        while (status == RUN) {
            Log.info("urlFoundSize : " + urlFound.size() + " - urlToSearchSize: " + urlToSearch.size() + " - Running Thread: " + getRunningThread() + " - Total Request: " + threadCount + " - Aborted request: " + stoppedCount);
            ExtURL url = getUrlToSearch();
            if (url == null) {
                Log.info("url = null");
                break;
            }
            if (!urlFound.add(url.toString())) {
                continue;
            }

            ScanningThread th = getScanningThread();
            if (th != null) {
                threadCount++;
                th.setUrl(url);
                th.setStatus(ScanningThread.START);
                th.restart();
                Log.info("Scanned  url : " + url ) ;
            }
            else {
                addUrlToSearch(url);
            }
        }

        Log.info("Set List: " + urlFound.size());
        Log.info("Url List: " + urlToSearch.size());
        Log.info("Running Thread: " + getRunningThread());
        Log.info("Aborted request: " + stoppedCount);
        Log.info("Total request: " + threadCount);
        Log.info("Extracted emails: " + emailListModel.getSize());

        long time = (System.currentTimeMillis() - startTime) / 1000;
        int min = (int) (time / 60);
        int sec = (int) (time - min * 60);
        Log.info("Done in " + min + " min " + sec + " sec");

        setStatus(STOP);
    }

    public void start(URL url) {

        if (useProxy) {
            SocketAddress addr = new InetSocketAddress(proxyHost, proxyPort);
            proxy = new Proxy(Proxy.Type.HTTP, addr);
        }
        else {
            proxy = Proxy.NO_PROXY;
        }

        urlFound.clear();
        urlToSearch.clear();
        threadList.clear();

        startTime = System.currentTimeMillis();
        threadCount = 0;
        stoppedCount = 0;

        if ("http".equals(url.getProtocol())) {
            setStatus(RUN);

            urlToSearch.put(url.toString(), new ExtURL(url));
            new Thread(this).start();
        }
    }

    public void stop() {
        setStatus(STOP);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public int getThreadMax() {
        return threadMax;
    }

    public void setThreadMax(int threadMax) {
        this.threadMax = threadMax;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public int getEmailCount() {
        return emailCount;
    }

    public Thread getStopThread() {
        return stopThread;
    }

    public void setStopThread(Thread stopThread) {
        this.stopThread = stopThread;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getStoppedThreadCount() {
        return stoppedCount;
    }

    public DefaultListModel getEmailListModel() {
        return emailListModel;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        int oldStatus = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("status", Integer.valueOf(oldStatus), Integer.valueOf(status));
    }

    public String getUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(String urlFilter) throws PatternSyntaxException {
        this.urlFilter = urlFilter;
        urlFilterPattern = (urlFilter.length() > 0) ? Pattern.compile(urlFilter) : null;
    }

    public String getPageFilter() {
        return pageFilter;
    }

    public void setPageFilter(String pageFilter) {
        this.pageFilter = pageFilter;
    }

    public String getEmailFilter() {
        return emailFilter;
    }

    public void setEmailFilter(String emailFilter) throws PatternSyntaxException {
        this.emailFilter = emailFilter;
        emailFilterPattern = (emailFilter.length() > 0) ? Pattern.compile(emailFilter) : null;
    }

    public int getSearchLimit() {
        return searchLimit;
    }

    public void setSearchLimit(int searchLimit) {
        this.searchLimit = searchLimit;
    }

    public boolean isUseRobots() {
        return useRobots;
    }

    public void setUseRobots(boolean useRobots) {
        this.useRobots = useRobots;
    }

    public int getInLinkDepth() {
        return inLinkDepth;
    }

    public void setInLinkDepth(int inLinkDepth) {
        this.inLinkDepth = inLinkDepth;
    }

    public int getOutLinkDepth() {
        return outLinkDepth;
    }

    public void setOutLinkDepth(int outLinkDepth) {
        this.outLinkDepth = outLinkDepth;
    }

    public Pattern getUrlFilterPattern() {
        return urlFilterPattern;
    }

    public Pattern getEmailFilterPattern() {
        return emailFilterPattern;
    }

    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Setter for property emailCount.
     *
     * @param emailCount New value of property emailCount.
     */
    public void setEmailCount(int emailCount) {
        int oldEmailCount = this.emailCount;
        this.emailCount = emailCount;
        propertyChangeSupport.firePropertyChange("emailCount", Integer.valueOf(oldEmailCount), Integer.valueOf(emailCount));
    }

    public void incEmailCount() {
        setEmailCount(emailCount + 1);
    }

    public void decEmailCount() {
        setEmailCount(emailCount - 1);
    }
}
