package com.supergroup.asset.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AssetService {

    /**
     * @param multipartFile : file you want to upload
     * @param key: key of file
     */
    String saveFile(MultipartFile multipartFile, String key) throws IOException;

    String getUrl(String key);

    /**
     * @param key : key of file which you want to delete
     */
    void deleteFile(String key) throws Exception;
}
