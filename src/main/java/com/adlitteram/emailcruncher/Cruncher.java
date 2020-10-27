package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.utils.LimitedList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cruncher {

    private static final Logger LOGGER = LoggerFactory.getLogger("Cruncher");

    private static final String URLS = "urls";
    private static final String URL_FILTER = "url_filter";
    private static final String PAGE_FILTER = "page_filter";
    private static final String EMAIL_FILTER = "email_filter";
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

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Set<String> urlFound;
    private final Set<String> emailFound;
    private ThreadPoolExecutor executor;
    private int status = STOP;
    private final LimitedList<String> urls;

    private String urlFilter;
    private String pageFilter;
    private String emailFilter;
    private Pattern urlFilterPattern;
    private Pattern emailFilterPattern;
    private int searchLimit;
    private int inLinkDepth;
    private int outLinkDepth;
    private int timeOut;
    private int threadMax;
    private boolean useProxy;
    private String proxyHost;
    private int proxyPort;

    private final CrunchService cruncherService;

    private Cruncher(Preferences prefs) {
        urls = new LimitedList(Arrays.asList(prefs.get(URLS, "").split(";")));
        emailFilter = prefs.get(EMAIL_FILTER, "");
        pageFilter = prefs.get(PAGE_FILTER, "");
        urlFilter = prefs.get(URL_FILTER, "");
        searchLimit = prefs.getInt(SEARCH_LIMIT, 0);
        inLinkDepth = prefs.getInt(IN_LINK_DEPTH, 3);
        outLinkDepth = prefs.getInt(OUT_LINK_DEPTH, 3);
        timeOut = prefs.getInt(TIME_OUT, 120);
        threadMax = prefs.getInt(THREAD_MAX, 10);
        useProxy = prefs.getBoolean(USE_PROXY, false);
        proxyHost = prefs.get(PROXY_HOST, "proxy");
        proxyPort = prefs.getInt(PROXY_PORT, 8);

        cruncherService = new CrunchService(this);
        emailFound = Collections.synchronizedSet(new HashSet<>(1000));
    }

    public static Cruncher create(Preferences prefs) {
        return new Cruncher(prefs);
    }

    public Preferences update(Preferences prefs) {      
        prefs.put(URLS, String.join(";", urls));
        prefs.put(EMAIL_FILTER, emailFilter);
        prefs.put(PAGE_FILTER, pageFilter);
        prefs.put(URL_FILTER, urlFilter);
        prefs.putInt(SEARCH_LIMIT, searchLimit);
        prefs.putInt(IN_LINK_DEPTH, inLinkDepth);
        prefs.putInt(OUT_LINK_DEPTH, outLinkDepth);
        prefs.putInt(TIME_OUT, timeOut);
        prefs.putInt(THREAD_MAX, threadMax);
        prefs.putBoolean(USE_PROXY, useProxy);
        prefs.put(PROXY_HOST, proxyHost);
        prefs.putInt(PROXY_PORT, proxyPort);
        return prefs;
    }

    public void processUrl(ExtURL extUrl) {
        if (urlFound.add(extUrl.toString())) {
            LOGGER.info("Url: " + extUrl.toString());
            executor.execute(() -> cruncherService.scan(extUrl));
        }
    }

    public void start(URL url) {
        LOGGER.info("Start: " + url.toString());
        setStatus(RUN);
        urls.addFirst(url.toString());
        urlFound = Collections.synchronizedSet(new HashSet<>(100000));
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadMax);
        processUrl(new ExtURL(url));
    }

    public void stop() {
        doStop();
    }

    private void doStop() {
        setStatus(STOP);
        try {
            // Wait to let threadpool finish
            executor.shutdownNow();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
        }

        LOGGER.info("stop");
        LOGGER.info("Exe Queue Size: " + executor.getQueue().size());
        LOGGER.info("Exe LargestPoolSize: " + executor.getLargestPoolSize());
        LOGGER.info("Exe CompletedTaskCount: " + executor.getCompletedTaskCount());
        LOGGER.info("Exe TaskCount: " + executor.getTaskCount());
        LOGGER.info("URL Found List: " + urlFound.size());
        LOGGER.info("Extracted emails: " + emailFound.size());

        urlFound.clear();
        executor.purge();
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

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        var oldStatus = this.status;
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

    public ProxySelector getProxySelector() {
        return useProxy ? ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)) : ProxySelector.getDefault();
    }

    public ArrayList<String> getEmails() {
        return new ArrayList<>(emailFound);
    }

    public void clearEmails() {
        emailFound.clear();
        propertyChangeSupport.firePropertyChange("clearEmails", "", null);
    }

    public boolean addEmail(String email) {
        if (emailFound.add(email)) {
            propertyChangeSupport.firePropertyChange("addEmail", "", email);
            return true;
        }
        return false;
    }

    public String[] getUrls() {
        return urls.toArray(new String[0]) ;
    }
}
