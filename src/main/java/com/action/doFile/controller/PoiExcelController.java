package com.action.doFile.controller;

import com.action.doFile.service.PoiExcelService;
import com.action.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * 集成POI之Excel导入导出
 */
@Slf4j
@RestController
@RequestMapping("/v1/poi/excel")
public class PoiExcelController {

    @Autowired
    private PoiExcelService poiExcelService;


    /**
     * 分片上传
     *
     * @param file     文件
     * @param chunk    分片（分片序号）
     * @param chunks   总分片数
     * @param fileName 文件名称
     * @return
     */
    @PostMapping("/file-chunk")
    public ResponseResult<String> uploadFileChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunk") int chunk,
            @RequestParam("chunks") int chunks,
            @RequestParam("fileName") String fileName) {

        try{
            poiExcelService.uploadFile(file,chunk,chunks,fileName);
        }catch (Exception e){
            return ResponseResult.fail("Failed to upload chunk");
        }

        return ResponseResult.success("Chunk uploaded successfully");
    }


    @PostMapping("/file-process")
    public ResponseResult<String> processFile(String filePath){
        poiExcelService.processFile(filePath);
        return ResponseResult.success("Chunk uploaded successfully");
    }


}
