package com.adlitteram.emailcruncher.log;

import java.util.logging.LogRecord;

public interface LogWriter {

    void write(LogRecord record);

    void close();

    void flush();
}
