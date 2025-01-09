package com.supergroup.kos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.supergroup.asset.service.AssetService;
import com.supergroup.asset.service.DigitalOceanSpaceAssetService;

@Configuration
public class AssetServiceConfig {

    @Value("${do.space.key}")
    private String doSpaceKey;

    @Value("${do.space.secret}")
    private String doSpaceSecret;

    @Value("${do.space.endpoint}")
    private String doSpaceEndpoint;

    @Value("${do.space.region}")
    private String doSpaceRegion;

    @Value("${do.space.bucket}")
    private String doSpaceBucket;

    @Bean
    public AssetService assetService() {
        var creds = new BasicAWSCredentials(doSpaceKey, doSpaceSecret);
        var client = AmazonS3ClientBuilder.standard()
                                    .withEndpointConfiguration(new EndpointConfiguration(doSpaceEndpoint, doSpaceRegion))
                                    .withCredentials(new AWSStaticCredentialsProvider(creds)).build();
        return new DigitalOceanSpaceAssetService(client, doSpaceBucket);
    }
}
