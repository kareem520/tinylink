package com.forMyCV.tinylink.services;

import com.forMyCV.tinylink.rateLimit.entity.RateLimitData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {


    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ModelMapper modelMapper;
    final private ObjectMapper objectMapper;

    @Value("${tinylink.cache.ttl-minutes}")
    private int cacheTtlMinutes;

    public void cacheOriginalUrl(String shortCode, String originalUrl) {

        try{
            redisTemplate.opsForValue().set("url:"+shortCode,originalUrl,cacheTtlMinutes, TimeUnit.MINUTES);
        }catch (Exception e){
            log.warn("Failed to cache URL for {}:{}",shortCode,e.getMessage());
        }

    }

    public String getCacheUrlByShortCode(String shortCode) {
        try{
            Object obj = redisTemplate.opsForValue().get("url:"+shortCode);
            return obj==null?null:(String)obj;
        }catch (Exception e){
            log.warn("Failed to reach cached URL for {}:{}",shortCode,e.getMessage());
            return  null;
        }
    }


    public void deleteUrlFromCache(String shortCode) {
        try {
            Boolean deleted = redisTemplate.delete("url:" + shortCode);

            if (Boolean.TRUE.equals(deleted)) {
                log.debug("Cache entry deleted for {}", shortCode);
            } else {
                log.debug("Cache entry not found for {}", shortCode);
            }
        } catch (Exception e) {
            log.warn("Failed to delete cached URL for {}: {}", shortCode, e.getMessage());
        }
    }


    public RateLimitData getRateLimitDataFromRedis(String redisKey)
    {
        try {
            Object obj =  redisTemplate.opsForValue().get(redisKey);
           return obj==null?null:objectMapper.convertValue(obj,RateLimitData.class);
        }catch (Exception e){
            log.warn("Failed to get rate limit data from redis: {}",e.getMessage());
            return null;
        }
    }

    public void saveRateLimitDataToRedis(String redisKey, RateLimitData rateLimitData) {

        try{
            redisTemplate.opsForValue().set(redisKey,rateLimitData,1, TimeUnit.HOURS);
        }catch (Exception e){
            log.warn("Failed to save rate limit data to redis: {}",e.getMessage());
        }
    }
}
