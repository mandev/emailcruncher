package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.utils.EmailExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Slf4j
public class Cruncher {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Set<String> foundEmails;
    private final OkHttpClient httpClient;
    private final CruncherModel cruncherModel;

    private Set<String> foundUrls;
    private ThreadPoolExecutor executor;
    private Pattern urlFilterPattern;
    private Pattern emailFilterPattern;
    private long startTime ;

    private Status status = Status.STOP;

    private Cruncher(CruncherModel cruncherModel) {
        this.cruncherModel = cruncherModel;

        this.foundEmails = Collections.synchronizedSet(new HashSet<>(10000));

        // TODO Configure proxy   
        int poolSize = Math.max(64, cruncherModel.getThreadMax()/5) ;
        this.httpClient = new OkHttpClient.Builder()
                .readTimeout(cruncherModel.getTimeOut()*1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .connectTimeout(cruncherModel.getTimeOut()*1000, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(poolSize, 1L, TimeUnit.MINUTES))
                .build();
    }

    public static Cruncher create() {
        CruncherModel cruncherModel;

        try {
            Preferences prefs = Preferences.userNodeForPackage(Cruncher.class);
            String str = prefs.get("Preferences", null);
            cruncherModel = str == null ? new CruncherModel() : new ObjectMapper().readValue(str, CruncherModel.class);
        }
        catch (JsonProcessingException ex) {
            log.info("Unable to find or read Cruncher preferences", ex);
            cruncherModel = new CruncherModel();
        }

        return new Cruncher(cruncherModel);
    }

    public void save() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(Cruncher.class);
            String str = new ObjectMapper().writeValueAsString(cruncherModel);
            prefs.put("Preferences", str);
            prefs.flush();
        }
        catch (BackingStoreException | JsonProcessingException ex) {
            log.warn("Failed to save Cruncher preferences", ex);
        }
    }

    public CruncherModel getCruncherModel() {
        return cruncherModel;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        var oldStatus = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("status", oldStatus, status);
    }

    public void start(URL url) {
        log.info("Start: " + url.toString());
        startTime = System.currentTimeMillis() ;
        setStatus(Status.RUN);
        cruncherModel.getUrls().addFirst(url.toString());

        String urlFilter = cruncherModel.getUrlFilter();
        urlFilterPattern = (urlFilter.length() > 0) ? Pattern.compile(urlFilter) : null;

        String emailFilter = cruncherModel.getEmailFilter();
        emailFilterPattern = (emailFilter.length() > 0) ? Pattern.compile(emailFilter) : null;

        foundUrls = Collections.synchronizedSet(new HashSet<>(100000));
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cruncherModel.getThreadMax());
        processUrl(new ExtURL(url));
    }

    public void stop() {
        setStatus(Status.STOP);
        try {
            // Wait to let threads to finish
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
        }

        long stopTime = System.currentTimeMillis() ;
                
        log.info("Stop");
        log.info("Largest pool size: " + executor.getLargestPoolSize());
        log.info("Completed tasks: " + executor.getCompletedTaskCount());
        log.info("Total tasks: " + executor.getTaskCount());
        log.info("URLs found: " + foundUrls.size());
        log.info("Emails found: " + foundEmails.size());
        log.info("Wall Time: " + Math.round((stopTime-startTime)/1000) + " s");
        log.info("Completed Tasks/Sec: " + Math.round(executor.getCompletedTaskCount()*1000/(stopTime-startTime)));
        
        foundUrls.clear();
        executor.purge();
    }

    public ArrayList<String> getEmails() {
        return new ArrayList<>(foundEmails);
    }

    public void clearEmails() {
        foundEmails.clear();
        propertyChangeSupport.firePropertyChange("clearEmails", "", null);
    }

    private void processEmail(String email) {
        if (foundEmails.add(email)) {
            propertyChangeSupport.firePropertyChange("addEmail", "", email);
        }
    }

    private void processUrl(ExtURL extUrl) {
        if (foundUrls.add(extUrl.toString())) { 
            log.info("Queue Size: " + executor.getQueue().size() + 
                    " - Core Threads: " + executor.getCorePoolSize() +
                    " - Active Taks: " + executor.getActiveCount() + 
                    " - Completed Tasks: " + executor.getCompletedTaskCount() +
                    " - Total Tasks: " + executor.getTaskCount());
            
//            log.info(extUrl.toString());
            executor.execute(() -> {
                var content = getContent(extUrl);
                if (content != null) {
                    searchURL(content, extUrl);
                    searchEmail(content);
                }
            });
        }
    }

    private String getContent(ExtURL url) {

        var request = new Request.Builder().url(url.getUrl()).build();

        try (var response = httpClient.newCall(request).execute()) {
            var contentType = response.header("content-type");
            if (contentType != null && contentType.contains("text/")) {
                var body = response.body();
                if (body != null) {
                    return body.string();
                }
            }
        }
        catch (IOException ex) {
            if (!executor.isShutdown()) {
                log.warn("Failed to call " + url.toString(), ex);
            }
        }
        return null;
    }

    private int searchURL(String content, ExtURL url) {
        var count = 0;

        if (cruncherModel.getPageFilter().length() > 0) {
            if (content.contains(cruncherModel.getPageFilter())) {
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

                if (urlFilterPattern != null && urlFilterPattern.matcher(urlLink.getUrl().toString()).matches()) {
                    continue;
                }

                if (cruncherModel.getSearchLimit() == 0 && !urlLink.isLocal()) {
                    continue;
                }

                if ((cruncherModel.getInLinkDepth() > 0) && (urlLink.getInLinkCount() > cruncherModel.getInLinkDepth())) {
                    continue;
                }
                
                if ((cruncherModel.getOutLinkDepth() > 0) && (urlLink.getOutLinkCount() > cruncherModel.getOutLinkDepth())) {
                    continue;
                }

                processUrl(urlLink);
                count++;
            }
        }
        return count;
    }

    private int searchEmail(String content) {
        var count = 0;

        ArrayList<String> emails = EmailExtractor.extracts(content);
        for (String email : emails) {
            if (emailFilterPattern == null || !emailFilterPattern.matcher(email).matches()) {
                count++;
                processEmail(email);
            }
        }
        return count;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

}
