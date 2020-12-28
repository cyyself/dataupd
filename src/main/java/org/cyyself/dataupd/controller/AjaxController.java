package org.cyyself.dataupd.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/ajax",produces = MediaType.APPLICATION_JSON_VALUE)
public interface AjaxController {
    @PostMapping(value = "/excelUpload")
    public String excelUpload(@RequestParam MultipartFile file);
    @GetMapping(value = "/getStatus")
    public String getStatus();
    @GetMapping(value = "/getKeys")
    public String getKeys();
    @GetMapping(value = "/getPage")
    public String getPage(@RequestParam Integer page, @RequestParam Integer pagesize);
}
