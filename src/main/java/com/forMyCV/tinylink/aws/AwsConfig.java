package com.forMyCV.tinylink.aws;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


@Configuration
public class AwsConfig {


    @Value("${aws.s3.region}")
    private String awsRegion;

    @Value("${aws.accessKeyId}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;



    @Bean
    public StaticCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey,awsSecretKey));
    }

    @Bean
    public S3Client s3Client(StaticCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }


}
