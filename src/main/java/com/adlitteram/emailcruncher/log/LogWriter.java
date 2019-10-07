package com.adlitteram.emailcruncher.log;

import java.util.logging.LogRecord;

public interface LogWriter {

    public void write(LogRecord record);

    public void close();

    public void flush();
}
