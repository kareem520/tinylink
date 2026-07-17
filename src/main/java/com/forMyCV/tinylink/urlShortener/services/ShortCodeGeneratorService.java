package com.forMyCV.tinylink.urlShortener.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortCodeGeneratorService {


    @Value("${tinylink.short-code.length}")
    private int shortCodeLength;

    @Value("${tinylink.short-code.max-attempts}")
    private int maxGenerationAttempts;

    private static final String BASE_62_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    private final UrlStorageService urlStorageService;

    public String generateUniqueShortCode() {
        for (int attempts = 0; attempts < maxGenerationAttempts; attempts++) {
            String shortCode = generateBase62RandomCode();
          if (urlStorageService.checkUniquenessForShortCode(shortCode)) {
              return shortCode;
          }
        }
        throw new RuntimeException("Failed to generate unique short code after " + maxGenerationAttempts + " attempts");
    }
    private String generateBase62RandomCode() {
        StringBuilder sb = new StringBuilder(shortCodeLength);
        for (int i = 0; i < shortCodeLength; i++) {
            int index = ThreadLocalRandom.current().nextInt(BASE_62_CHARS.length());

            sb.append(BASE_62_CHARS.charAt(index));
        }
        return sb.toString();
    }



}
