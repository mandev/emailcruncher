package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.CruncherModel;
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
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportEmails extends XAction {

    private final Cruncher cruncher;

    public ExportEmails(Cruncher cruncher) {
        super("ExportEmails");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CruncherModel cruncherModel = cruncher.getCruncherModel();

        var fc = new FileChooser(Main.getMainframe(), Message.get("ExportTitle"));
        fc.setDirectory(cruncherModel.getExportDir());
        fc.setFile("");
        fc.addFileFilter(XFileFilter.XLS);
        fc.addFileFilter(XFileFilter.TXT);

        if (fc.showSaveDialog() == FileChooser.APPROVE_OPTION) {
            cruncherModel.setExportDir(fc.getSelectedFile().getParent());
            var filename = fc.getSelectedFile().getPath();
            try {
                if ("xls".equalsIgnoreCase(getSuffix(filename))) {
                    exportToXls(filename);
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
        var i = str.lastIndexOf('.');
        if ((i > 0) && (i < (str.length() - 1))) {
            return str.substring(i + 1);
        }
        return null;
    }

    private void exportToXls(String filename) throws IOException {
        var wb = new HSSFWorkbook();
        var sheet = wb.createSheet("emails");

        // Create a cell and put a value in it.
        List<String> emails = cruncher.getEmails();
        for (var i = 0; i < emails.size(); i++) {
            // Create a row and put some cells in it. Rows are 0 based.
            var row = sheet.createRow(i);
            row.createCell(0).setCellValue(new HSSFRichTextString(emails.get(i)));
        }

        wb.write(new File(filename));
    }

    private void exportToTxt(String filename) throws IOException {

        try (var out = new FileOutputStream(filename);
                var osw = new OutputStreamWriter(out, "UTF-8");
                var writer = new BufferedWriter(osw)) {

            for (var email : cruncher.getEmails()) {
                writer.write(email);
                writer.write(13);
            }
        }
    }

}
