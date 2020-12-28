package org.cyyself.dataupd.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public interface ExcelProcess{
    public int fromInputStream(InputStream file, String originalFilename, MongoTemplate db, String storeCollection) throws IOException;
    public String toString();
}