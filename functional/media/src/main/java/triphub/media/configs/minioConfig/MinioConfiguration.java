package triphub.media.configs.minioConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfiguration {
    @Value("${minio.url}")
    private String minioUrl;
    
    @Value("${minio.access-key}")
    private String accessKey;
    
    @Value("${minio.secret-key}")
    private String secretKey;
    
    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Bean
    public MinioClient minioClient() {
        System.out.println(accessKey);
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
    
    @Bean
    public String bucketName() {
        return bucketName;
    }
}