package com.action.doFile.controller;

import com.action.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/v1/file")
public class doFileController {

    @PutMapping("/upload")
    public ResponseResult<String> upload(@RequestParam(value = "file", required = true) MultipartFile file) {

        try {
            //本地存储文件
            String uploadPath = "/Users/pengchen/PENGCHEN/DemoProject/demo-action/files"; //
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            log.info(uploadDir.getAbsolutePath());
            File localFile = new File(uploadPath + File.separator + file.getOriginalFilename());
            //保存上传的文件
            file.transferTo(localFile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.fail(e.getMessage());
        }

        return ResponseResult.success();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response){
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment;filename=file_" + System.currentTimeMillis() + ".png");

        // 从文件读到servlet response输出流中
        File file = new File("/Users/pengchen/PENGCHEN/DemoProject/demo-action/files/test1.png"); // 改这里
        try (FileInputStream inputStream = new FileInputStream(file)) { // try-with-resources
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
