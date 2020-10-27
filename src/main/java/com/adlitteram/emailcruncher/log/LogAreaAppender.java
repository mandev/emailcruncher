package com.adlitteram.emailcruncher.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.apache.commons.lang3.time.FastDateFormat;

public class LogAreaAppender extends AppenderBase<ILoggingEvent> {

    private final FastDateFormat FMT = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.MEDIUM);
    private final JTextArea logArea;
    private final Document document;

    public LogAreaAppender(JTextArea logArea) {
        this.logArea = logArea;
        this.document = new LogDocument();
        logArea.setDocument(document);
    }

    public synchronized void reset() {
        try {
            document.remove(0, document.getLength());
        }
        catch (BadLocationException ex) {
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        logArea.append(FMT.format(event.getTimeStamp()) + " - " + event.getMessage() + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private class LogDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            var tooMany = str.length() - 16000;
            if (tooMany > 0) {
                str = str.substring(tooMany);
                remove(0, getLength());
            }
            else {
                tooMany += getLength() ;
                if (tooMany > 0) {
                    remove(0, tooMany);
                }
            }
            super.insertString(getLength(), str, a);
        }
    }
}
