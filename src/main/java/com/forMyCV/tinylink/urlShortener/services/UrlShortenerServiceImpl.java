package com.forMyCV.tinylink.urlShortener.services;

import com.forMyCV.tinylink.auth_user.entity.User;
import com.forMyCV.tinylink.auth_user.services.UserService;
import com.forMyCV.tinylink.urlShortener.dtos.ShortenerUrlRequest;
import com.forMyCV.tinylink.urlShortener.dtos.ShortenerUrlResponse;
import com.forMyCV.tinylink.urlShortener.dtos.UrlAnalyticsResponse;
import com.forMyCV.tinylink.urlShortener.dtos.UrlStatsResponse;
import com.forMyCV.tinylink.exceptions.BusinessException;
import com.forMyCV.tinylink.exceptions.ErrorCode;
import com.forMyCV.tinylink.helper.UrlShortenerHelper;
import com.forMyCV.tinylink.models.ClickEvent;
import com.forMyCV.tinylink.services.CacheService;
import com.forMyCV.tinylink.services.ClickEventStorageService;
import com.forMyCV.tinylink.urlShortener.entity.UrlData;
import com.forMyCV.tinylink.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Locale.filter;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {


    private final UrlShortenerHelper urlShortenerHelper;
    private final ShortCodeGeneratorService shortCodeGeneratorService;
    private final UrlStorageService urlStorageService;
    private final CacheService cacheService;
    private final ClickEventStorageService clickEventStorageService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    @Override
    public Response<ShortenerUrlResponse> shortenUrl(ShortenerUrlRequest shortenerUrlRequest, String clientIp) {

        User user = userService.getCurrentLoggedInUser();


        String shortCode = shortenerUrlRequest.getCustomAlias();

        if (shortCode != null && !shortCode.trim().isEmpty()) {
            shortCode = shortCode.trim();

            if(!urlStorageService.checkUniquenessForShortCode(shortCode)) {
                throw new BusinessException(ErrorCode.CUSTOM_ALIAS_EXISTS);
            }
        }
        else {
            shortCode = shortCodeGeneratorService.generateUniqueShortCode();
        }

        LocalDateTime expiresAt = shortenerUrlRequest.getExpiresAt();

        UrlData urlData = UrlData.builder()
                .user(user)
                .originalUrl(shortenerUrlRequest.getOriginalUrl())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt==null?LocalDateTime.now().plusDays(2):expiresAt)
                .clickCount(0)
                .createdBy(clientIp)
                .isActive(true)
                .clickEvents(new ArrayList<>())
                .build();

        urlStorageService.saveUrlDataForShortCode(urlData);

//        clickEventStorageService.createShortCodeClickEventRelation(shortCode);

        cacheService.cacheOriginalUrl(shortCode,shortenerUrlRequest.getOriginalUrl());

        log.info("Short code {} for {} has been created", shortCode,shortenerUrlRequest.getOriginalUrl());

        ShortenerUrlResponse shortenerUrlResponse = ShortenerUrlResponse.builder()
                .originalUrl(shortenerUrlRequest.getOriginalUrl())
                .shortCode(shortCode)
                .shortUrl(urlShortenerHelper.buildShortUrl(shortCode))
                .createdAt(urlData.getCreatedAt())
                .expiresAt(urlData.getExpiresAt())
                .build();

        return Response.<ShortenerUrlResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("short url created successfully")
                .data(shortenerUrlResponse)
                .build();
    }





    @Override
    public Response<String> getOriginalUrl(String shortCode) {

        log.info("Inside getOriginalUrl()");

        String response = cacheService.getCacheUrlByShortCode(shortCode);

        if (response == null)
            response =  urlStorageService.getOriginalUrl(shortCode);


        cacheService.cacheOriginalUrl(shortCode, response);

        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Original url retrieved successfully " + shortCode)
                .data(response)
                .build();

    }

    @Override
    @Transactional
    public void recordClick(String shortCode, String clientIp, String userAgent, String referrer) {

        log.info("Inside recordClick()");

        UrlData urlData = urlStorageService.getUrlData(shortCode);

        urlData.setClickCount(urlData.getClickCount() + 1);

        ClickEvent clickEvent = ClickEvent.builder()
                .timestamp(LocalDateTime.now())
                .ipAddress(clientIp)
                .userAgent(userAgent)
                .referrer(referrer)
                .build();

        urlStorageService.addClickEvent(urlData,clickEvent);

        log.debug("Recorder click for short code: {}", shortCode);
    }

    @Override
    public Response<UrlStatsResponse> getUrlStats(String shortCode) {

        UrlData urlData = urlStorageService.getUrlData(shortCode);


        UrlStatsResponse response = UrlStatsResponse.builder()
                .shortCode(shortCode)
                .originalUrl(urlData.getOriginalUrl())
                .clickCount(urlData.getClickCount())
                .createdAt(urlData.getCreatedAt())
                .expiresAt(urlData.getExpiresAt())
                .isActive(urlData.isActive())
                .createdBy(urlData.getCreatedBy())
                .createdByUser(urlData.getUser().getEmail())
                .build();

        return Response.<UrlStatsResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("URL stats retrieved successfully")
                .data(response)
                .build();

    }

    @Override
    public Response<UrlAnalyticsResponse> getUrlAnalytics(String shortCode) {

        UrlData urlData = urlStorageService.getUrlData(shortCode);


        List<ClickEvent> clickEvents = clickEventStorageService.getShortCodeClickEvents(shortCode);
        if (clickEvents == null || clickEvents.isEmpty()) {clickEvents = new ArrayList<>();}


        Map<String,Integer>clicksByReferrer = clickEvents
                .stream()
                .filter(c->c.getReferrer()!=null)
                .collect(Collectors.groupingBy(ClickEvent::getReferrer,Collectors.summingInt(c->1)));

        Map<String, Integer> clicksByHour = clickEvents.stream()
                .collect(Collectors.groupingBy(
                        c->c.getTimestamp().getHour() + ":00",
                        Collectors.summingInt(e->1)
                ));

        Map<String, Integer> clicksByDay = clickEvents.stream()
                .collect(Collectors.groupingBy(
                        c->c.getTimestamp().toLocalDate().toString(),
                        Collectors.summingInt(e->1)
                ));


        List<ClickEvent> recentClicks = clickEvents.stream()
                .sorted(Comparator.comparing(ClickEvent::getTimestamp).reversed())
                .limit(5)
                .peek(click -> click.setUrlData(null))
                .toList();

        UrlAnalyticsResponse urlAnalyticsResponse = UrlAnalyticsResponse.builder()
                .shortCode(shortCode)
                .originalUrl(urlData.getOriginalUrl())
                .totalClicks(urlData.getClickCount())
                .createdAt(urlData.getCreatedAt())
                .expiresAt(urlData.getExpiresAt())
                .recentClicks(recentClicks.isEmpty()?null:recentClicks)
                .clicksByReferrer(clicksByReferrer.isEmpty()?null:clicksByReferrer)
                .clicksByHour(clicksByHour.isEmpty()?null:clicksByHour)
                .clicksByDay(clicksByDay.isEmpty()?null:clicksByDay)
                .build();

        return Response.<UrlAnalyticsResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("URL Analytics retrieved successfully")
                .data(urlAnalyticsResponse)
                .build();
    }

    @Override
    public Response<?> deleteUrl(String shortCode) {

        UrlData urlData = urlStorageService.getUrlData(shortCode);

        urlStorageService.deactivateUrlDataForShortCode(urlData);
        cacheService.deleteUrlFromCache(shortCode);
        log.info("Deleted URL for {}", shortCode);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("URL deleted successfully")
                .build();

    }

    @Override
    public Response<List<ShortenerUrlResponse>> getAllMyUrls() {
        User user = userService.getCurrentLoggedInUser();

        List<UrlData> urlDataList = urlStorageService.getAlLUrlDataByEmail(user.getEmail());

        List<ShortenerUrlResponse> shortenerUrlResponseList =
                urlDataList.stream()
                        .map(urlData -> {
                            ShortenerUrlResponse shortenerUrlResponse = modelMapper.map(urlData, ShortenerUrlResponse.class);
                            shortenerUrlResponse.setShortUrl(urlShortenerHelper.buildShortUrl(urlData.getShortCode()));
                            return shortenerUrlResponse;
                        })
                        .toList();
        return Response.<List<ShortenerUrlResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("retrieved successfully")
                .data(shortenerUrlResponseList)
                .build();
    }




    public void cleanupExpiredUrls() {
        int cleanedCount = 0;

        LocalDateTime now = LocalDateTime.now();

        List<UrlData> ExpiredurlDataList = urlStorageService.getAllExpired(now);

        for(UrlData urlData:ExpiredurlDataList) {
            urlData.setActive(false);
            cacheService.deleteUrlFromCache(urlData.getShortCode());
            cleanedCount++;
        }

        if(cleanedCount > 0){
            log.info("Cleaned up {} expired URLs", cleanedCount);
        }
    }
}
