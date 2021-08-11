package com.allen.moments.v2.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Configuration
public class S3Client {

    AmazonS3 client;
    @Value("${aws.access_key_id}")
    private String awsId;

    @Value("${aws.secret_access_key}")
    private String awsKey;

    @Value("${aws.s3.region}")
    private String region;

    private Logger logger = LogManager.getLogger(this.getClass());

    @Bean(name = "client")
    public AmazonS3 s3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        this.client = s3Client;
        return s3Client;
    }


    @Value("${aws.s3.endpoint_url}")
    private String endpointUrl;

    @Value("${aws.s3.bucket_name}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile, String fileName)
            throws Exception {
        String fileUrl = "";
        File file = convertMultiPartToFile(multipartFile);
        fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
        uploadFileToS3Bucket(fileName, file);
        if (!file.delete()) {
            logger.warn("File " + fileName + " deletion failed");
        }
        return fileUrl;
    }

    public Boolean deleteFileFromS3Bucket(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            client.deleteObject(bucketName, fileName);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    private void uploadFileToS3Bucket(String fileName, File file) {
        client.putObject(new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private File convertMultiPartToFile(MultipartFile file)
            throws IOException {
        File convFile = new File(String.valueOf(UUID.randomUUID()) + System.currentTimeMillis());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" +   Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

}