package com.forMyCV.tinylink.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
@RequestMapping("/api/upload")
public class AwsTestUpload {

    @Autowired
    private AwsS3Service awsS3Service;

    @PostMapping
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("keyName") String keyName)

    {
        URL savedFileURL = awsS3Service.uploadFile(keyName,file);
        return ResponseEntity.ok(savedFileURL.toString());
    }
}
