package com.forMyCV.tinylink.urlShortener.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlStatsResponse {
    private String shortCode;
    private String originalUrl;
    private int clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private String createdByUser;
    private String createdBy;
}
