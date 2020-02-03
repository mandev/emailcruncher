package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.Main;
import com.adlitteram.emailcruncher.Message;
import com.adlitteram.emailcruncher.gui.widgets.FileChooser;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import com.adlitteram.emailcruncher.utils.XFileFilter;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.prefs.Preferences;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportEmails extends XAction {

    private static final String EXPORT_DIR = "export_dir";
    private final Cruncher cruncher;

    public ExportEmails(Cruncher cruncher) {
        super("ExportEmails");
        this.cruncher = cruncher;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Preferences prefs = Preferences.userNodeForPackage(ExportEmails.class);

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
                    exportToXls(filename);
                } else {
                    exportToTxt(filename);
                }
            } catch (IOException ex) {
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

        // Create a cell and put a value in it.
        List<String> emails = cruncher.getEmails();
        for (int i = 0; i < emails.size(); i++) {
            // Create a row and put some cells in it. Rows are 0 based.
            HSSFRow row = sheet.createRow(i);
            row.createCell(0).setCellValue(new HSSFRichTextString(emails.get(i)));
        }

        wb.write(new File(filename));
    }

    private void exportToTxt(String filename) throws IOException {

        try (FileOutputStream out = new FileOutputStream(filename);
                OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                BufferedWriter writer = new BufferedWriter(osw)) {

            for (String email : cruncher.getEmails()) {
                writer.write(email);
                writer.write(13);
            }
        }
    }
}
