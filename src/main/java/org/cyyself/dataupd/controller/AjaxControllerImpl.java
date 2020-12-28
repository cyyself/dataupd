package org.cyyself.dataupd.controller;

import com.mongodb.BasicDBObject;
import org.cyyself.dataupd.service.ExcelProcess;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ajax",produces = MediaType.APPLICATION_JSON_VALUE)
public class AjaxControllerImpl implements AjaxController{
    @Autowired
    private ExcelProcess excelProcess;
    @Autowired
    private MongoTemplate db;
    @Value("${app.storeCollection}")
    String storeCollection;
    @PostMapping(value = "/excelUpload")
    public String excelUpload(@RequestParam MultipartFile file) {
        JSONObject result = new JSONObject();
        try {
            excelProcess.fromInputStream(file.getInputStream(),file.getOriginalFilename(),db,storeCollection);
        }
        catch (Exception e) {
            e.printStackTrace();
            result.put("status",500);
            return result.toString();
        }
        result.put("status",200);
        return result.toString();
    }
    @GetMapping(value = "/getStatus")
    public String getStatus() {
        return excelProcess.toString();
    }
    @GetMapping(value = "/getKeys")
    public String getKeys() {
        JSONObject result = new JSONObject();
        BasicDBObject queryResult = db.findOne(new Query(), BasicDBObject.class,storeCollection);
        if (queryResult == null) {
            result.put("status",500);
        }
        else {
            List<String> keys = new ArrayList<>(queryResult.keySet());
            result.put("keys",keys);
            result.put("count",db.count(new Query(),storeCollection));
        }
        return result.toString();
    }
    @GetMapping(value = "/getPage")
    public String getPage(@RequestParam Integer page, @RequestParam Integer pagesize) {
        if (page == null) page = 0;
        if (pagesize == null) pagesize = 20;
        Query queryParams = new Query().skip(page * pagesize).limit(pagesize);
        List<BasicDBObject> queryResult  = db.find(queryParams,BasicDBObject.class,storeCollection);
        JSONObject result = new JSONObject();
        if (queryResult == null) result.put("status",500);
        else {
            result.put("status",200);
            List<JSONObject> queryResultJSON = new ArrayList<>();
            for (BasicDBObject x : queryResult) {
                JSONObject tmp = new JSONObject();
                for (String key : x.keySet()) tmp.put(key,x.get(key));
                queryResultJSON.add(tmp);
            }
            result.put("count",db.count(new Query(),storeCollection));
            result.put("list",queryResultJSON);
        }
        return result.toString();
    }
}
