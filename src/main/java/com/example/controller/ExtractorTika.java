package com.example.controller;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//получение содержимого файла Tika ocr
public class ExtractorTika {
    public ExtractorTika() {
    }

    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    ParseContext pcontext = new ParseContext();
    FileInputStream inputStream;
    PDFParser pdfParser = new PDFParser();

    public void importPDF(String file) throws FileNotFoundException {
        inputStream = new FileInputStream(file);
        try {
            pdfParser.parse(inputStream, handler, metadata, pcontext);
            System.out.println(handler.toString());
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public String getDocumentText() {
        return handler.toString();
    }

    public Map<String, String> getMetadata() {
        String[] metadatanames = metadata.names();
        Map<String, String> metamap = new HashMap<>();
        for (String name : metadatanames) {
            metamap.put(name, metamap.get(name));
        }
        return metamap;
    }

    public static void main(String[] args) {
        ExtractorTika extractorTika =new ExtractorTika();
        try {
            extractorTika.importPDF("D:\\test2.pdf");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
