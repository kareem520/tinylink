package com.forMyCV.tinylink.rateLimit.services;

import com.forMyCV.tinylink.helper.RateLimitHelper;
import com.forMyCV.tinylink.rateLimit.entity.RateLimitData;
import com.forMyCV.tinylink.response.Response;
import com.forMyCV.tinylink.services.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {




    private final RateLimitHelper rateLimitHelper;
    private final CacheService cacheService;
    private final RateLimitStorageService rateLimitStorageService;


    private static final String REDIS_KEY_PREFIX = "rate-limiter:";


    @Value("${tinylink.rate-limit.requests-per-minute}")
    private int requestPerMinute;
    @Value("${tinylink.rate-limit.requests-per-hour}")
    private int requestPerHour;


    @Override
    public Response<Boolean> isAllowed(String clientIp) {

        String redisKey = REDIS_KEY_PREFIX + clientIp;

        LocalDateTime now = LocalDateTime.now();

        RateLimitData rateLimitData = cacheService.getRateLimitDataFromRedis(redisKey);

        if(rateLimitData == null){
            rateLimitData =
                    rateLimitStorageService.createRateLimitData(clientIp,now);
        }

        boolean isWithInMinuteWindow =  rateLimitData.getMinuteWindowStart()!=null &&
                ChronoUnit.MINUTES.between(rateLimitData.getMinuteWindowStart(),now)<1;

        if (isWithInMinuteWindow) {
            if (rateLimitData.getMinuteCount()>=requestPerMinute) {
                log.warn("Minute limit exceeded for {}", clientIp);
                return rateLimitHelper.getFailedresponse();
            }
        }
        else{
            rateLimitData.setMinuteCount(0);
            rateLimitData.setMinuteWindowStart(now);
        }

        boolean isWithInHourWindow =  rateLimitData.getHourWindowStart()!=null &&
                ChronoUnit.HOURS.between(rateLimitData.getHourWindowStart(),now)<1;
        if (isWithInHourWindow) {
            if (rateLimitData.getHourCount()>=requestPerHour) {
                log.warn("Hour limit exceeded for {}", clientIp);
                return rateLimitHelper.getFailedresponse();
            }
        }else{
            rateLimitData.setHourCount(0);
            rateLimitData.setHourWindowStart(now);
        }

        rateLimitData.setMinuteCount(rateLimitData.getMinuteCount()+1);
        rateLimitData.setHourCount(rateLimitData.getHourCount()+1);

        cacheService.saveRateLimitDataToRedis(redisKey,rateLimitData);

        return rateLimitHelper.getSuccessfulresponse();
    }

}
