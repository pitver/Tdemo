package com.example.controller;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class OCRTesseract {

  private  final Tesseract tesseract;

    public OCRTesseract(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    @PostMapping("/ocr")
    public String performOCR(@RequestBody MultipartFile file) throws IOException, TesseractException {
        // Создаем временный файл для сохранения загруженного изображения
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        // Распознаем текст на изображении
        String result = tesseract.doOCR(tempFile);

        // Удаляем временный файл
        if (tempFile.exists()) {
            tempFile.delete();
        }

        return result;

    }

}


