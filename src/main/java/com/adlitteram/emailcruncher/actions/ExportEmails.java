package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.Main;
import com.adlitteram.emailcruncher.Message;
import com.adlitteram.emailcruncher.gui.widgets.FileChooser;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import com.adlitteram.emailcruncher.utils.XFileFilter;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.prefs.Preferences;
import javax.swing.DefaultListModel;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportEmails extends XAction {

    private static final String EXPORT_DIR = "export_dir";
    private final Cruncher cruncher;
    private final Preferences prefs;

    public ExportEmails(Cruncher cruncher) {
        super("ExportEmails");
        this.cruncher = cruncher;
        prefs = Preferences.userNodeForPackage(ExportEmails.class);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultListModel model = cruncher.getEmailListModel();

        FileChooser fc = new FileChooser(Main.getMainframe(), Message.get("ExportTitle"));
        fc.setDirectory(prefs.get(EXPORT_DIR, System.getProperty("user.home")));
        fc.setFile("");
        fc.addFileFilter(XFileFilter.XLS);
        fc.addFileFilter(XFileFilter.TXT);

        if (fc.showSaveDialog() == FileChooser.APPROVE_OPTION) {
            prefs.put(EXPORT_DIR, fc.getSelectedFile().getParent());
            String filename = fc.getSelectedFile().getPath();
            try {
                if ("xls".equalsIgnoreCase(getSuffix(filename))) {
                    exportToTxt(filename);
                }
                else {
                    exportToTxt(filename);
                }
            }
            catch (IOException ex) {
                GuiUtils.showError(Message.get("ExportError") + ex.getMessage());
            }
        }
    }

    // Return String after dot
    public static String getSuffix(String str) {
        int i = str.lastIndexOf('.');
        if ((i > 0) && (i < (str.length() - 1))) {
            return str.substring(i + 1);
        }
        return null;
    }

    private void exportToXls(String filename) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("emails");

        // Create a row and put some cells in it. Rows are 0 based.
        HSSFRow row = sheet.createRow((short) 0);

        // Create a cell and put a value in it.
        DefaultListModel model = cruncher.getEmailListModel();
        Object[] emails = model.toArray();

        for (int i = 0; i < emails.length; i++) {
            row.createCell((short) i).setCellValue(new HSSFRichTextString((String) emails[i]));
        }

        try (FileOutputStream os = new FileOutputStream(filename)) {
            wb.write(os);
        }
    }

    private void exportToTxt(String filename) throws IOException {

        BufferedWriter writer = null;

        try {
            FileOutputStream out = new FileOutputStream(filename);
            writer = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));

            DefaultListModel model = cruncher.getEmailListModel();
            Object[] emails = model.toArray();

            for (Object email : emails) {
                writer.write((String) email);
                writer.write(13);
            }
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }

    }
}
