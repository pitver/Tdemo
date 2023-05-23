package com.example.controller;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.File;
import java.io.IOException;


//получение шрифта
public class PDFTableStyleExtractorWithPdfbox {

    public static PDFont extractStylesFromPDF(String filePath) throws IOException {
        File file = new File(filePath);
        PDDocument document = PDDocument.load(file);
        PDFont font = null;

        for (int i = 0; i < document.getNumberOfPages(); ++i) {
            PDPage page = document.getPage(i);
            PDResources res = page.getResources();
           var b= res.getExtGStateNames();
           b.forEach(System.out::println);

            for (COSName fontName : res.getFontNames()) {
                font = res.getFont(fontName);
                System.out.println(font.getName());

            }
        }return font;
    }

    public static void main(String[] args) {
        String pdfFilePath = "D:\\test1.pdf";
        try {
            var b = extractStylesFromPDF(pdfFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

