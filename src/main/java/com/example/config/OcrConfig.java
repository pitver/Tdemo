package com.example.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OcrConfig {
    @Bean
    public Tesseract tesseract(){
        // Создаем экземпляр Tesseract
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("D:\\tesseract\\tessdata");
        tesseract.setLanguage("rus+eng");
        return tesseract;
    }
}
