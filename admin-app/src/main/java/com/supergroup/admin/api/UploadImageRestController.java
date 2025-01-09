package com.supergroup.admin.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.supergroup.admin.constant.ImageName;
import com.supergroup.admin.constant.ImageType;
import com.supergroup.asset.service.AssetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/asset/images")
@Slf4j
public class UploadImageRestController {

    private final AssetService assetService;

    @PostMapping
    private ResponseEntity<List<String>> uploadImage(@RequestParam("images") MultipartFile[] images,
                                             @RequestParam("name") ImageName name,
                                             @RequestParam("type") ImageType type) throws IOException {
        var listImage = new ArrayList<String>();
        for (MultipartFile image: images) {
            var key = "assets/images/" + name.getKey() + "/" + type.name().toLowerCase() + "/" + image.getOriginalFilename();
            log.info("Upload " + image.getOriginalFilename() + " to " + key);
            assetService.saveFile(image, key);
            listImage.add(assetService.getUrl(key));
        }
        return ResponseEntity.ok(listImage);
    }

}
