package com.action.doFile.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface PoiExcelService {

    void processFile(String filePath);


    void uploadFile(MultipartFile file, int chunk, int chunks, String fileName);
}
