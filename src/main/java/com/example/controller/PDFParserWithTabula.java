package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

//получение простой таблицы tabula
public class PDFParserWithTabula {

    public static void main(String[] args) throws IOException {

        final String FILENAME="D:\\test2.pdf";

        PDDocument pd = PDDocument.load(new File(FILENAME));

        int totalPages = pd.getNumberOfPages();
        System.out.println("Total Pages in Document: "+totalPages);

        ObjectExtractor oe = new ObjectExtractor(pd);
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        Page page = oe.extract(1);

        // extract text from the table after detecting
        List<Table> table = sea.extract(page);
        for(Table tables: table) {
            List<List<RectangularTextContainer>> rows = tables.getRows();

            for(int i=0; i<rows.size(); i++) {

                List<RectangularTextContainer> cells = rows.get(i);

                for(int j=0; j<cells.size(); j++) {
                    System.out.print(cells.get(j).getText()+"|");
                }

                System.out.println();
            }
        }

    }

}