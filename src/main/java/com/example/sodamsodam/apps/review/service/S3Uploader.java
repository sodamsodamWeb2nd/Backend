package com.example.sodamsodam.apps.review.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {

        String fileName = dirName + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            log.info("S3 업로드 시작: bucket={}, key={}", bucket, fileName);
            amazonS3.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (AmazonServiceException e) {
            log.error("S3 업로드 실패 (AWS 서비스 예외) - StatusCode: {}, ErrorCode: {}, Message: {}",
                    e.getStatusCode(), e.getErrorCode(), e.getErrorMessage());
            throw new RuntimeException("S3 서비스 에러: " + e.getErrorMessage(), e);
        } catch (SdkClientException e) {
            log.error("S3 업로드 실패 (클라이언트 오류) - Message: {}", e.getMessage());
            throw new RuntimeException("S3 클라이언트 오류: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("S3 업로드 실패 (IO 에러) - Message: {}", e.getMessage());
            throw new RuntimeException("S3 파일 입출력 에러: " + e.getMessage(), e);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
