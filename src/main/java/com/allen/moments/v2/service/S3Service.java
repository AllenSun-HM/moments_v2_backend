package com.allen.moments.v2.service;

import com.allen.moments.v2.utils.S3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {
    @Qualifier("client")
    private final S3Client s3Client;

    static {
        System.out.println(("S3 service initialized at" + LocalDateTime.now()));
    }

    @Autowired()
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file) throws Exception {
        return s3Client.uploadFile(file, file.getName());
    }

    public List<String> upload(List<MultipartFile> files) throws Exception {
        ArrayList<String> fileUrls = new ArrayList<String>();
        for (MultipartFile file : files) {
            String url = s3Client.uploadFile(file, file.getName());
            fileUrls.add(url);
        }
        return fileUrls;
    }
}
