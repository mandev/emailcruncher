package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.utils.LimitedList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CruncherModel {

    private LimitedList<String> urls = new LimitedList<>();
    private String urlFilter = "";
    private String pageFilter = "";
    private String emailFilter = "";
    private int searchLimit = 0;
    private int inLinkDepth = 3;
    private int outLinkDepth = 3;
    private int timeOut = 20;
    private int threadMax = 8;
    private boolean useProxy = false;
    private String proxyHost = "proxy";
    private int proxyPort = 90;
    private String exportDir = System.getProperty("user.home");

}
