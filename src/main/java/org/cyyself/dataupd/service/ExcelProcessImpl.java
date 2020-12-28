package org.cyyself.dataupd.service;

import org.cyyself.dataupd.model.UploadInfo;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ExcelProcessImpl implements ExcelProcess{
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private final List<UploadInfo> uploadInfos = new ArrayList<>();
    public int fromInputStream(InputStream file, String filename, MongoTemplate db, String storeCollection)  {
        UploadInfo newUploadInfo = new UploadInfo(filename,file,db, storeCollection);
        uploadInfos.add(newUploadInfo);
        threadPool.execute(newUploadInfo.getWorker());
        return 0;
    }
    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        for (UploadInfo x : uploadInfos) {
            result.put(Integer.toString(x.getId()),x.toJSONObject());
        }
        return result.toString();
    }
}
