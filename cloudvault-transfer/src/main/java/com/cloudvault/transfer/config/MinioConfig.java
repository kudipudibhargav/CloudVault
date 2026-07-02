package com.cloudvault.transfer.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build()
            );
            if (!exists) {
                client.makeBucket(
                        MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build()
                );
                log.info("MinIO bucket auto-provisioned: {}", minioProperties.getBucketName());
            }
        } catch (Exception e) {
            log.warn("MinIO bucket pre-verification deferred: {}", e.getMessage());
        }

        return client;
    }
}
