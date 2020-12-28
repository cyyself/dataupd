package org.cyyself.dataupd.model;

import com.mongodb.BasicDBObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UploadInfo {
    private final int id;
    private final String filename;
    private double progress;
    private UploadStatus status;
    private final Runnable worker;
    private static int current_id = 0;
    private byte[] fileContent;
    public UploadInfo(String filename, InputStream file, MongoTemplate mongoTemplate, String storeCollection) {
        this.id = ++ current_id;
        this.filename = filename;
        this.progress = 0;
        this.status = UploadStatus.PENDING;
        UploadInfo that = this;
        try {
            this.fileContent = file.readAllBytes();
        }
        catch (Exception e) {
            e.printStackTrace();
            status = UploadStatus.ERROR;
            this.worker = new Runnable() {
                @Override
                public void run() {

                }
            };
            return;
        }
        this.worker = new Runnable() {
            @Override
            public void run() {
                InputStream file = new ByteArrayInputStream(fileContent);
                that.status = UploadStatus.RUNNING;
                Workbook wb = null;
                try {
                    wb = new HSSFWorkbook(file);
                } catch (Exception e) {
                    try {
                        wb = new XSSFWorkbook(file);
                    }
                    catch (Exception ee) {
                        ee.printStackTrace();
                        that.status = UploadStatus.ERROR;
                        return;
                    }
                }
                Sheet sheet = wb.getSheetAt(0);
                int rows = sheet.getPhysicalNumberOfRows() - sheet.getFirstRowNum();
                Row firstRow = sheet.getRow(sheet.getFirstRowNum());
                List<String> keys = new ArrayList<>();
                for (Cell x: firstRow) keys.add(x.getStringCellValue());
                for (int i = sheet.getFirstRowNum() + 1; i < rows; i++) {
                    Row currentRow = sheet.getRow(i);
                    BasicDBObject obj = new BasicDBObject();
                    for (int j = 0; j < Math.min(keys.size(),currentRow.getLastCellNum()); j++) {
                        String cellValue = null;
                        try {
                            cellValue = currentRow.getCell(j).getStringCellValue();
                        }
                        catch (Exception ignored) {}
                        if (cellValue == null) cellValue = "";
                        obj.put(keys.get(j),cellValue);
                    }
                    mongoTemplate.insert(obj,storeCollection);
                    that.progress = (double)i / (rows - 1);
                }
                that.status = UploadStatus.FINISHED;
            }
        };
    }
    public int getId() {
        return id;
    }
    public String getFilename() {
        return filename;
    }
    public double getProgress() {
        return progress;
    }
    public UploadStatus getStatus() {
        return status;
    }
    public Runnable getWorker() {
        return worker;
    }
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.put("id",id);
        result.put("filename",filename);
        result.put("progress",progress);
        result.put("status",status);
        return result;
    }
    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
