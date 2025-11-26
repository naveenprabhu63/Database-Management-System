package com.crime;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.JOptionPane;   // ADD THIS LINE
import java.io.FileOutputStream;
import java.util.List;

public class ExportManager {
    public static void export(List<Crime> crimes, String filename) {
        String[] options = {"Excel (.xlsx)", "PDF"};
        int choice = JOptionPane.showOptionDialog(null, "Export Format:", "Export",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) exportExcel(crimes, filename + ".xlsx");
        else if (choice == 1) exportPDF(crimes, filename + ".pdf");
    }

    private static void exportExcel(List<Crime> crimes, String file) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Crimes");
        String[] headers = {"ID", "Type", "Location", "Date", "Suspect", "Status", "Description"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (Crime c : crimes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getCrimeType());
            row.createCell(2).setCellValue(c.getLocation());
            row.createCell(3).setCellValue(c.getDateReported());
            row.createCell(4).setCellValue(c.getSuspectName());
            row.createCell(5).setCellValue(c.getStatus());
            row.createCell(6).setCellValue(c.getDescription());
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
            JOptionPane.showMessageDialog(null, "Exported: " + file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void exportPDF(List<Crime> crimes, String file) {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();
            doc.add(new Paragraph("Crime Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            String[] headers = {"ID", "Type", "Location", "Date", "Suspect", "Status", "Desc"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }
            for (Crime c : crimes) {
                table.addCell(String.valueOf(c.getId()));
                table.addCell(c.getCrimeType());
                table.addCell(c.getLocation());
                table.addCell(c.getDateReported());
                table.addCell(c.getSuspectName());
                table.addCell(c.getStatus());
                String desc = c.getDescription();
                table.addCell(desc.length() > 50 ? desc.substring(0, 47) + "..." : desc);
            }
            doc.add(table);
            doc.close();
            JOptionPane.showMessageDialog(null, "Exported: " + file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
