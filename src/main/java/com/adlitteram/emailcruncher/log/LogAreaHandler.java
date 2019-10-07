package com.adlitteram.emailcruncher.log;

import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class LogAreaHandler extends Handler {

    protected SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss.SSS");

    private final JTextArea logArea;
    private final Document document;

    public LogAreaHandler(JTextArea logArea) {
        this.logArea = logArea;
        this.document = new PlainDocument();
        logArea.setDocument(document);
    }

    public synchronized void reset() {
        SwingUtilities.invokeLater(() -> {
            try {
                document.remove(0, document.getLength());
            }
            catch (BadLocationException e) {
            }
        });
    }

    @Override
    public void publish(final LogRecord record) {
        if (isLoggable(record)) {
            logArea.append(fmt.format(record.getMillis()) + " - " + record.getMessage() + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }
}
