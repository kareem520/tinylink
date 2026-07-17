package com.forMyCV.tinylink.urlShortener.services;


import com.forMyCV.tinylink.urlShortener.dtos.ShortenerUrlRequest;
import com.forMyCV.tinylink.urlShortener.dtos.ShortenerUrlResponse;
import com.forMyCV.tinylink.urlShortener.dtos.UrlAnalyticsResponse;
import com.forMyCV.tinylink.urlShortener.dtos.UrlStatsResponse;
import com.forMyCV.tinylink.response.Response;

import java.util.List;

public interface UrlShortenerService {

    public Response<ShortenerUrlResponse> shortenUrl(ShortenerUrlRequest shortenerUrlRequest, String clientIp);
    public Response<String> getOriginalUrl(String shortCode);
    public void recordClick(String shortCode, String clientIp,String userAgent,String referrer);
    public Response<UrlStatsResponse> getUrlStats(String shortCode);
    public Response<UrlAnalyticsResponse>getUrlAnalytics(String shortCode);
    public Response<?>deleteUrl(String shortCode);
    public void cleanupExpiredUrls();
    public Response<List<ShortenerUrlResponse>> getAllMyUrls();
}
