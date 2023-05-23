package com.example.controller;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//получение простой таблицы из pdf и запись в файл
@RestController
public class TableRecognitionControllerWithPdfBoxAndTika {

    @PostMapping("/recognizeTable")
    public void recognizeTable(@RequestPart("pdfFile") MultipartFile pdfFile) throws IOException, TikaException, SAXException {
// Создаем временный файл для сохранения PDF
        File tempFile = File.createTempFile("pdf", ".pdf");
        try (InputStream inputStream = pdfFile.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            // Копируем содержимое загруженного файла во временный файл
            StreamUtils.copy(inputStream, outputStream);
        }

        // Открываем PDF-документ с помощью Apache Tika
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext pcontext = new ParseContext();

        // Используем PDFParser для обработки PDF-файла
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(new FileInputStream(tempFile), handler, metadata, pcontext);

        // Получаем текст из документа
        String pageText = handler.toString();

        // Распознаем таблицы на странице и получаем их данные
        List<List<String>> tableData = recognizeTables(pageText);

        // Создаем новый документ DOCX
        try (XWPFDocument doc = new XWPFDocument()) {

            // Создаем таблицу с нужным количеством строк и столбцов
            int rowCount = tableData.size();
            int columnCount = tableData.get(0).size();
            XWPFTable table = doc.createTable(rowCount, columnCount);


// Применяем стили к таблице
            // applyTableStyles(table);
            // Заполняем таблицу данными
            for (int i = 0; i < rowCount; i++) {
                List<String> rowData = tableData.get(i);
                XWPFTableRow row = table.getRow(i);

                for (int j = 0; j < columnCount; j++) {
                    String cellData = rowData.get(j);
                    XWPFTableCell cell = row.getCell(j);
                    cell.setText(cellData);
                }
            }

            // Сохраняем документ DOCX
            File outputFile = new File("output.docx");
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                doc.write(fileOutputStream);
            }

            // Закрываем документы
            doc.close();
        }
    }

    private List<List<String>> recognizeTables(String pageText) {
        List<List<String>> tableData = new ArrayList<>();
        String[] lines = pageText.split("\n");

        for (String line : lines) {
            line = line.trim(); // Удаление пробелов в начале и конце строки

            if (!line.isEmpty()) { // Исключение пустых строк
                String[] cells = line.split("\\s+"); // Разделитель - пробел
                List<String> rowData = Arrays.asList(cells);
                tableData.add(rowData);
            }
        }

        return tableData;
    }

    //задание стилей
    /*private void applyCellStyles(XWPFTableCell cell) {
        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        CTTcBorders borders = tcPr.addNewTcBorders();
        borders.addNewLeft().setVal(STBorder.COMPASS);
        borders.addNewRight().setVal(STBorder.COMPASS);
        borders.addNewTop().setVal(STBorder.COMPASS);
        borders.addNewBottom().setVal(STBorder.COMPASS);
        borders.addNewInsideH().setVal(STBorder.COMPASS);
        borders.addNewInsideV().setVal(STBorder.COMPASS);
    }

    private void applyTableStyles(XWPFTable table) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        // Создаем стиль для таблицы
        CTString styleId = CTString.Factory.newInstance();
        styleId.setVal("Table Grid"); // Устанавливаем имя стиля таблицы
        tblPr.setTblStyle(styleId);

        // Применяем стили к ячейкам таблицы
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                applyCellStyles(cell);
            }
        }
    }*/

}
