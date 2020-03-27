package com.adlitteram.emailcruncher.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.apache.commons.lang3.time.FastDateFormat;

public class LogAreaHandler extends Handler {

    private final FastDateFormat FMT = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.MEDIUM);

    private final JTextArea logArea;
    private final Document document;

    public LogAreaHandler(JTextArea logArea) {
        this.logArea = logArea;
        this.document = new LogDocument();
        logArea.setDocument(document);
    }

    public synchronized void reset() {
        SwingUtilities.invokeLater(() -> {
            try {
                document.remove(0, document.getLength());
            } catch (BadLocationException ignored) {
            }
        });
    }

    @Override
    public void publish(final LogRecord record) {
        if (isLoggable(record)) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(FMT.format(record.getMillis()) + " - " + record.getMessage() + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    private class LogDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offs, str, a);
            var tooMany = getLength() - 32000;
            if (tooMany > 0) {
                remove(0, tooMany);
            }
        }
    }

}
