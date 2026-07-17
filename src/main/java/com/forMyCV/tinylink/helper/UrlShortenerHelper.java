package com.forMyCV.tinylink.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class  UrlShortenerHelper {





    @Value("${tinylink.base-url}")
    private String baseUrl;

    public String buildShortUrl(String shortCode) {
        return baseUrl + "/api/safe/"+shortCode;
    }



}
