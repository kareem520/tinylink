package com.forMyCV.tinylink.urlShortener.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.forMyCV.tinylink.models.ClickEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlAnalyticsResponse {
    private String shortCode;
    private String originalUrl;
    private int totalClicks;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private List<ClickEvent> recentClicks;
    private Map<String, Integer> clicksByReferrer;
    private Map<String, Integer> clicksByHour;
    private Map<String, Integer> clicksByDay;
}