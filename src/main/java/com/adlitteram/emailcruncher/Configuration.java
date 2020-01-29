package com.adlitteram.emailcruncher;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Configuration {

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

    private Preferences prefs;

    private final Cruncher cruncher;

    public Configuration(Cruncher cruncher) {
        this.cruncher = cruncher;
    }

    public Configuration load() {
        prefs = Preferences.userNodeForPackage(Cruncher.class);

        cruncher.setEmailFilter(prefs.get(EMAIL_FILTER, ""));
        cruncher.setPageFilter(prefs.get(PAGE_FILTER, ""));
        cruncher.setUrlFilter(prefs.get(URL_FILTER, ""));
        cruncher.setUseRobots(prefs.getBoolean(USE_ROBOTS, false));

        cruncher.setSearchLimit(prefs.getInt(SEARCH_LIMIT, 0));
        cruncher.setInLinkDepth(prefs.getInt(IN_LINK_DEPTH, 3));
        cruncher.setOutLinkDepth(prefs.getInt(OUT_LINK_DEPTH, 3));

        cruncher.setTimeOut(prefs.getInt(TIME_OUT, 120));
        cruncher.setThreadMax(prefs.getInt(THREAD_MAX, 10));

        cruncher.setUseProxy(prefs.getBoolean(USE_PROXY, false));
        cruncher.setProxyHost(prefs.get(PROXY_HOST, "proxy"));
        cruncher.setProxyPort(prefs.getInt(PROXY_PORT, 80));

        return this;
    }

    public Configuration save() {
        prefs.put(EMAIL_FILTER, cruncher.getEmailFilter());
        prefs.put(PAGE_FILTER, cruncher.getPageFilter());
        prefs.put(URL_FILTER, cruncher.getUrlFilter());
        prefs.putBoolean(USE_ROBOTS, cruncher.isUseRobots());

        prefs.putInt(SEARCH_LIMIT, cruncher.getSearchLimit());
        prefs.putInt(IN_LINK_DEPTH, cruncher.getInLinkDepth());
        prefs.putInt(OUT_LINK_DEPTH, cruncher.getOutLinkDepth());

        prefs.putInt(TIME_OUT, cruncher.getTimeOut());
        prefs.putInt(THREAD_MAX, cruncher.getThreadMax());

        prefs.putBoolean(USE_PROXY, cruncher.isUseProxy());
        prefs.put(PROXY_HOST, cruncher.getProxyHost());
        prefs.putInt(PROXY_PORT, cruncher.getProxyPort());

        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
        }

        return this;
    }

}
