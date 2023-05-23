package com.example.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.tika.exception.TikaException;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//получение простой таблицы из pdf и запись в файл
@RestController
public class TableRecognitionControllerWithTabulaAndTika {

    @PostMapping("/recognizeTable")
    public void recognizeTable(@RequestPart("pdfFile") MultipartFile pdfFile) throws IOException, TikaException, SAXException {
        File tempFile = File.createTempFile("pdf", ".pdf");
        try (InputStream inputStream = pdfFile.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            // Копируем содержимое загруженного файла во временный файл
            StreamUtils.copy(inputStream, outputStream);
        }
        PDDocument pd = PDDocument.load(tempFile);

        int totalPages = pd.getNumberOfPages();
        System.out.println("Total Pages in Document: " + totalPages);

        ObjectExtractor oe = new ObjectExtractor(pd);
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

        for (int i = 1; i <= totalPages; i++) {
            Page page = oe.extract(i);


            // Extract text from the table after detecting
            List<Table> tables = sea.extract(page);

            // Create a new DOCX document
            try (XWPFDocument doc = new XWPFDocument()) {
                XWPFTable table = doc.createTable();

                // Iterate over the extracted tables
                for (Table tabulaTable : tables) {
                    List<List<RectangularTextContainer>> rows = tabulaTable.getRows();

                    for (List<RectangularTextContainer> rectangularTextContainers : rows) {
                        XWPFTableRow row = table.createRow();

                        for (int j = 0; j < rectangularTextContainers.size(); j++) {
                            var textContainer = rectangularTextContainers.get(j);
                            if (textContainer.getText() != null && !textContainer.getText().isEmpty()) {

                                XWPFTableCell cell = row.getCell(j);
                                if (cell == null) {
                                    cell = row.createCell();
                                }else {
                                    // Проверяем наличие пустого абзаца и удаляем его, если он существует
                                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                                    if (!paragraphs.isEmpty() && paragraphs.get(0).getRuns().isEmpty()) {
                                        cell.removeParagraph(0);
                                    }
                                }
                                System.out.print(rectangularTextContainers.get(j).getText() + "|");
                                cell.setText(rectangularTextContainers.get(j).getText().trim());
                            }
                        }
                        System.out.println();
                    }
                }

                // Сохраняем документ DOCX
                File outputFile = new File("output" + i + ".docx");
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    doc.write(fileOutputStream);
                }
            }

            // Close the PDF document
            pd.close();

        }
    }

}



