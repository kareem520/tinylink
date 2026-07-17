package com.forMyCV.tinylink.rateLimit.services;

import com.forMyCV.tinylink.rateLimit.entity.RateLimitData;
import com.forMyCV.tinylink.rateLimit.repository.RateLimitDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimitStorageService {


    private final RateLimitDataRepository rateLimitDataRepository;

    public RateLimitData createRateLimitData(String clientIp, LocalDateTime now)
    {

        RateLimitData rateLimitData = RateLimitData.builder()
                .minuteCount(0)
                .hourCount(0)
                .minuteWindowStart(now)
                .hourWindowStart(now)
                .clientIp(clientIp)
                .build();

       RateLimitData savedRateLimitData = rateLimitDataRepository.save(rateLimitData);

        return savedRateLimitData;

    }
}
