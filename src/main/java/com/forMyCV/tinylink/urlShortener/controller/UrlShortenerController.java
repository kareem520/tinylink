package com.forMyCV.tinylink.urlShortener.controller;


import com.forMyCV.tinylink.urlShortener.dtos.ShortenerUrlRequest;
import com.forMyCV.tinylink.urlShortener.dtos.ShortenerUrlResponse;
import com.forMyCV.tinylink.urlShortener.dtos.UrlStatsResponse;
import com.forMyCV.tinylink.response.Response;
import com.forMyCV.tinylink.rateLimit.services.RateLimitService;
import com.forMyCV.tinylink.urlShortener.services.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;
    private final RateLimitService rateLimitService;


    @PostMapping("/shorten")
    public ResponseEntity<Response<?>>shortenUrl(
            @RequestBody @Valid ShortenerUrlRequest shortenerUrlRequest,
            HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);

        Response<Boolean> isAllowed = rateLimitService.isAllowed(clientIp);


        if (!isAllowed.getData()){
            //it is not allowed to send the request now, try later
           return new ResponseEntity<>(isAllowed,HttpStatus.TOO_MANY_REQUESTS);
        }

        try {
            return new ResponseEntity<>(urlShortenerService.shortenUrl(shortenerUrlRequest,clientIp)
                                                                ,HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Error: " + e.getMessage())
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/safe/{shortCode}")
    public ResponseEntity<Void>redirectToUrl(
            @PathVariable String shortCode,
            HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String referer = httpRequest.getHeader("Referer");

        Response<String> originalUrl = urlShortenerService.getOriginalUrl(shortCode);

        urlShortenerService.recordClick(shortCode, clientIp, userAgent, referer);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl.getData()))
                .build();


    }
    private String getClientIp(HttpServletRequest httpRequest) {
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = httpRequest.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return httpRequest.getRemoteAddr();
    }

    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<Response<UrlStatsResponse>> getUrlStats(@PathVariable String shortCode) {

        return new ResponseEntity<>(urlShortenerService.getUrlStats(shortCode), HttpStatus.OK);
    }

    @GetMapping("analytics/{shortCode}")
    public ResponseEntity<Response<?>>getUrlAnalytics(@PathVariable String shortCode) {
        return new ResponseEntity<>(urlShortenerService.getUrlAnalytics(shortCode), HttpStatus.OK);

    }
    @GetMapping("/my-all")
    public ResponseEntity<Response<List<ShortenerUrlResponse>>>getMyAllUrls(HttpServletRequest httpRequest) {
        return new ResponseEntity<>(urlShortenerService.getAllMyUrls(), HttpStatus.OK);
    }
}
