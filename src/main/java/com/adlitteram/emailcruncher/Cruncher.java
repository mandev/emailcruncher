package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.log.Log;
import com.adlitteram.emailcruncher.utils.Utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Cruncher {

    public static final int SITE = 0;
    public static final int ALL = 1;
    public static final int STOP = 0;
    public static final int RUN = 1;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Set<String> urlFound;
    private Set<String> emailFound;
    private ThreadPoolExecutor executor;

    private int searchLimit = 0;
    private String urlFilter = "";
    private String pageFilter = "";
    private String emailFilter = "";
    private Pattern urlFilterPattern;
    private Pattern emailFilterPattern;
    private int inLinkDepth = 0;
    private int outLinkDepth = 3;
    private int timeOut = 30;
    private int threadMax = 4;
    private boolean useProxy = false;
    private String proxyHost = "";
    private int proxyPort = 80;
    private int status = STOP;

    private final CrunchService cruncherService;

    public Cruncher() {
        cruncherService = new CrunchService(this);
        emailFound = Collections.synchronizedSet(new HashSet<>(1000));
    }

    public void processUrl(ExtURL extUrl) {
        if (urlFound.add(extUrl.toString())) {
            Log.info("processUrl: " + extUrl.toString());
            executor.execute(() -> cruncherService.scan(extUrl));
        }
    }

    public void start(URL url) {
        Log.info("start: " + url.toString());
        setStatus(RUN);
        urlFound = Collections.synchronizedSet(new HashSet<>(100000));
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadMax);
        processUrl(new ExtURL(url));
    }

    public void stop() {
        doStop();
    }

    private void doStop() {
        executor.shutdownNow();
        setStatus(STOP);

        // Wait to let threadpool finish
        Utils.sleep(1000);

        Log.info("stop");
        Log.info("Exe Queue Size: " + executor.getQueue().size());
        Log.info("Exe LargestPoolSize: " + executor.getLargestPoolSize());
        Log.info("Exe CompletedTaskCount: " + executor.getCompletedTaskCount());
        Log.info("Exe TaskCount: " + executor.getTaskCount());
        Log.info("URL Found List: " + urlFound.size());
        Log.info("Extracted emails: " + emailFound.size());

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

}
