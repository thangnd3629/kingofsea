package com.supergroup.asset.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DigitalOceanSpaceAssetService implements AssetService {

    private final AmazonS3 s3Client;

    private final String doSpaceBucket;

    @Override
    public String saveFile(MultipartFile multipartFile, String key) throws IOException {
        saveImageToServer(multipartFile, key);
        return s3Client.getUrl(doSpaceBucket, key).toString();
    }

    @Override
    public String getUrl(String key) {
        return s3Client.getUrl(doSpaceBucket, Objects.isNull(key) ? "" : key).toString();
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, key));
    }

    private void saveImageToServer(MultipartFile multipartFile, String key) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getInputStream().available());
        if (multipartFile.getContentType() != null && !"".equals(multipartFile.getContentType())) {
            metadata.setContentType(multipartFile.getContentType());
        }
        s3Client.putObject(new PutObjectRequest(doSpaceBucket, key, multipartFile.getInputStream(), metadata)
                                   .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
