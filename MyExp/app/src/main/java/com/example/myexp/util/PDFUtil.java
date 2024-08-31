package com.example.myexp.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PDFUtil {

    public static String createPDF(String clinetName, List<String> headers, List<List<String>> data, List<Integer> numberColumns, float[] columnWidths) {
        Document document = new Document();
        String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        CSVReadAndWrite.createClientFolder(clinetName + "/share/" + seldate);
        String pdf = CSVReadAndWrite.getBaseFileWithFolder() + "/" + clinetName + "/share/" + seldate + "/" + (new SimpleDateFormat("HH-mm-ss").format(new Date())) + ".pdf";
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            int y = 750;
            addTable(document, data, numberColumns, columnWidths, 750 - y,headers);
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdf;
    }

    public static String shareAllPDF(String clinetName, Map<String,Object> dataMap,List<String> modules) {
        Document document = new Document();
        String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        CSVReadAndWrite.createClientFolder(clinetName + "/share/" + seldate);
        String pdf = CSVReadAndWrite.getBaseFileWithFolder() + "/" + clinetName + "/share/" + seldate + "/" + (new SimpleDateFormat("HH-mm-ss").format(new Date())) + ".pdf";
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            int y = 750;
            int ind=0;
            for(String s:modules) {
                System.out.println("S  "+s);
                List<List<String>> data= (List<List<String>>) dataMap.get(s+"_data");
                List<Integer> numberColumns= (List<Integer>) dataMap.get(s+"_numberColumns");
                float[] columnWidths= (float[]) dataMap.get(s+"_columnWidths");
                List<String> headers= (List<String>) dataMap.get(s+"_headers");
                addTable(document, data, numberColumns, columnWidths, 750 - y, headers,s,ind);
                ind++;
            }
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdf;
    }

    private static void createContent(PdfContentByte cb, float x, float y, String text, int align, int size) {
        cb.beginText();
        cb.setFontAndSize(getBaseFont(), size);
        cb.showTextAligned(align, text.trim(), x, y, 0);
        cb.endText();
    }

    private static void createContentheader(PdfContentByte cb, float x, float y, String text, int align, int size) {
        cb.beginText();
        cb.setFontAndSize(getHeaderBaseFont(), size);
        cb.showTextAligned(align, text.trim(), x, y, 0);
        cb.endText();
    }

    private static BaseFont getHeaderBaseFont() {
        try {
            return BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (Exception ex) {
            return null;
        }
    }

    private static BaseFont getBaseFont() {
        try {
            return BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void addbarcode(Document document, PdfContentByte cb, String text, int x, int y) {
        try {


            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(text);
            barcode128.setCodeType(Barcode.CODE128);
            Image code128Image = barcode128.createImageWithBarcode(cb, null, null);
            code128Image.setAbsolutePosition(x, y);
            code128Image.scaleToFit(190, 190);
            document.add(code128Image);

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }


    public static void addTable(Document document, List<List<String>> data, List<Integer> numberColumns, float[] columnWidths, float space,List<String> headers) {
        try {
            PdfPTable table = new PdfPTable(columnWidths.length); // 3 columns.
            BaseFont bf = BaseFont.createFont(
                    BaseFont.TIMES_ROMAN,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);
            Font fontH1 = new Font(bf, 12, Font.NORMAL);
            Font fontH2 = new Font(bf, 13, Font.BOLD);
            table.setHorizontalAlignment(100);
            table.setWidths(columnWidths);
            table.setWidthPercentage(100); //Width 100%
            System.out.println("Sapce : " + space);
            table.setSpacingBefore(space); //Space before table

            System.out.println(data);

            for (String h : headers) {
                table.addCell(getPCell(h, Element.ALIGN_RIGHT,fontH2,columnWidths.length));
                           }
            if(headers!=null && !headers.isEmpty())
                table.addCell(getPCell("Details", Element.ALIGN_CENTER,fontH2,columnWidths.length));
            int index = 1;
            for (int i = 0; i < data.size(); i++) {
                index = 1;
                for (String h : data.get(i)) {
                    table.addCell(getPCell(h, numberColumns.contains(index) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT,fontH1));
                    index++;
                }
            }

            document.add(table);
        } catch (Exception ex) {
            ex.printStackTrace();

        }



    }

    public static void addTable(Document document, List<List<String>> data, List<Integer> numberColumns, float[] columnWidths, float space,List<String> headers,String s,int tableIndex) {
        try {
            PdfPTable table = new PdfPTable(columnWidths.length); // 3 columns.
            BaseFont bf = BaseFont.createFont(
                    BaseFont.TIMES_ROMAN,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);
            Font fontH1 = new Font(bf, 12, Font.NORMAL);
            Font fontH2 = new Font(bf, 13, Font.BOLD);
            table.setHorizontalAlignment(100);
            table.setWidths(columnWidths);
            table.setWidthPercentage(100); //Width 100%
            System.out.println("Sapce : " + space);
            table.setSpacingBefore(space); //Space before table

            System.out.println(data);
            if (tableIndex > 0){
                table.addCell(getPCell(" ", Element.ALIGN_CENTER, fontH2, columnWidths.length));
            table.addCell(getPCell(" ", Element.ALIGN_CENTER, fontH2, columnWidths.length));
        }
            table.addCell(getPCell(s, Element.ALIGN_CENTER,fontH2,columnWidths.length));
            for (String h : headers) {
                table.addCell(getPCell(h, Element.ALIGN_RIGHT,fontH2,columnWidths.length));
            }
            if(headers!=null && !headers.isEmpty())
                table.addCell(getPCell("Details", Element.ALIGN_CENTER,fontH2,columnWidths.length));
            int index = 1;
            for (int i = 0; i < data.size(); i++) {
                index = 1;
                for (String h : data.get(i)) {
                    table.addCell(getPCell(h, numberColumns.contains(index) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT,fontH1));
                    index++;
                }
            }

            document.add(table);
        } catch (Exception ex) {
            ex.printStackTrace();

        }



    }

    public static PdfPCell getPCell(String text, int align, int barder) {
        PdfPCell cell3 = new PdfPCell(new Paragraph(text));
        cell3.setPaddingLeft(10);
        cell3.setHorizontalAlignment(align);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setBorder(barder);
        return cell3;
    }

    public static PdfPCell getPCell(String text, int align,Font font) {
        text = Element.ALIGN_RIGHT == align ? GeneralUtil.doubleFarmate(text) : text;
        PdfPCell cell3 = new PdfPCell(new Paragraph(text,font));
        cell3.setPaddingLeft(3);
        cell3.setHorizontalAlignment(align);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell3;
    }

    public static PdfPCell getPCell(String text, int align,Font font,int span) {
        text = Element.ALIGN_RIGHT == align ? GeneralUtil.doubleFarmate(text) : text;
        PdfPCell cell3 = new PdfPCell(new Paragraph(text,font));
        cell3.setColspan(span);
        cell3.setPaddingLeft(3);
        cell3.setHorizontalAlignment(align);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell3;
    }

    public static void main(String[] args) {
        try {

        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }

}

