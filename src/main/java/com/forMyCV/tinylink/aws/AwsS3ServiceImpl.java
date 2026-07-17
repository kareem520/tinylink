package com.forMyCV.tinylink.aws;

import com.forMyCV.tinylink.exceptions.BusinessException;
import com.forMyCV.tinylink.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;


@Service
@Slf4j
public class AwsS3ServiceImpl implements AwsS3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    @Override
    public URL uploadFile(String keyName, MultipartFile file) {
        log.info("Uploading file to S3");

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return s3Client.utilities().getUrl(builder ->  builder.bucket(bucketName).key(keyName));
        }catch (Exception e){
            log.error("Failed to upload file to S3", e);
            throw  new BusinessException(ErrorCode.AWS_FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void deleteFile(String keyName) {

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File {} deleted from Bucket {}", keyName, bucketName);
        }catch (Exception e){
            log.error("Failed to delete file {} from bucket {}", keyName, bucketName, e);
            throw new BusinessException(ErrorCode.AWS_FILE_DELETE_FAILED);
        }

    }
}
