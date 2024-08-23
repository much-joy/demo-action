package com.action.doFile.service.impl;

import com.action.doFile.service.PoiExcelService;
import com.action.response.ResponseResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class PoiExcelServiceImpl implements PoiExcelService {

    //文件存储路径
    private static final String UPLOAD_DIR = "demo-action/files/";

    private static final int BATCH_SIZE = 1000; // 每批次处理的行数

    @Autowired
    private Executor taskExecutor;


    @Override
    public void processFile(String filePath)  {
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(file);

            // 遍历所有的Sheet页
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                processSheet(sheet);
            }
            workbook.close();
            file.close();

        }catch (Exception e){

        }
    }

    @Override
    public void uploadFile(MultipartFile file, int chunk, int chunks, String fileName) {
        try {
            Path tempFile = Paths.get(UPLOAD_DIR, fileName + ".part" + chunk);
            Files.createDirectories(tempFile.getParent());
            file.transferTo(tempFile);

            //所有文件都上传完毕
            if (areAllChunksUploaded(fileName,chunks)) {
                //合并文件分片（可以分片合并）
                File combinedFile = combineChunks(fileName,chunks);
                //处理合并后的文件(存放到服务器)
                processFile(combinedFile);
            }

        } catch (IOException e) {

        }
    }

    private void processSheet(Sheet sheet) {
        int totalRows = sheet.getPhysicalNumberOfRows();
        int numThreads = Runtime.getRuntime().availableProcessors();//使用处理器核心数量
        int rowsPerThread = totalRows / numThreads; // 每个线程处理的行数

/*
         //返回当前JVM可用的处理器核心数量。这通常对应于物理机或虚拟机的CPU核心数
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Row> batch = new ArrayList<>();
        for (int i = 0; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            batch.add(row);

            if (batch.size() == BATCH_SIZE || i == totalRows - 1) {
                List<Row> batchToProcess = new ArrayList<>(batch);
                executor.submit(() -> processBatch(batchToProcess));
                //使用自定义线程池执行
//                taskExecutor.execute(() -> processBatch(batchToProcess));
                batch.clear();
            }
        }*/

        //直接对totalRows分批多线程处理---优化
        // 提交任务到线程池，每个线程处理一定数量的行
        ExecutorService executor = (ExecutorService) taskExecutor;
        for (int i = 0; i < numThreads; i++) {
            final int startRow = i * rowsPerThread;
            final int endRow = (i == numThreads - 1) ? totalRows : (i + 1) * rowsPerThread;

            executor.submit(() -> processRows(sheet, startRow, endRow));
        }

        executor.shutdown();
    }

    private void processRows(Sheet sheet, int startRow, int endRow) {
        List<Row> batch = new ArrayList<>();
        for (int i = startRow; i < endRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                batch.add(row);
            }

            if (batch.size() == BATCH_SIZE || i == endRow - 1) {
                List<Row> batchToProcess = new ArrayList<>(batch);//线程安全，每个线程都有自己的批处理
                processBatch(batchToProcess); // 处理一批数据
                batch.clear();
            }
        }
    }

    @Transactional
    public void processBatch(List<Row> batch) {
        String sql = "INSERT INTO your_table (column1, column2, column3) VALUES (?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Row row : batch) {
            String column1Value = row.getCell(0).getStringCellValue();
            double column2Value = row.getCell(1).getNumericCellValue();
            String column3Value = row.getCell(2).getStringCellValue();

            batchArgs.add(new Object[]{column1Value, column2Value, column3Value});
        }
        //操作数据库
    }


    /**
     * 循环检查分片文件是不是完整
     * @param fileName 文件名称
     * @param totalChunks 分片总数
     * @return
     */
    private boolean areAllChunksUploaded(String fileName, int totalChunks) {
        for (int i = 0; i < totalChunks ; i++) {
            if (!Files.exists(Paths.get(UPLOAD_DIR, fileName + ".part" + i))) {
                return false;
            }
        }
        return true;
    }

    private File combineChunks(String fileName,int totalChunks) throws IOException{
        Path combinedFilePath = Paths.get(UPLOAD_DIR, fileName);
        try (FileOutputStream fos = new FileOutputStream(combinedFilePath.toFile())) {
            for (int i = 1; i <= totalChunks; i++) {
                Path partPath = Paths.get(UPLOAD_DIR, fileName + ".part" + i);
                Files.copy(partPath, fos);
                Files.delete(partPath); // Optional: delete part after combining
            }
        }
        return combinedFilePath.toFile();
    }

    public void processFile(File file){

    }

}
