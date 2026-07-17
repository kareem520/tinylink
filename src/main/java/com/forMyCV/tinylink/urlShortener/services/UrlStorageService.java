package com.forMyCV.tinylink.urlShortener.services;


import com.forMyCV.tinylink.exceptions.BusinessException;
import com.forMyCV.tinylink.exceptions.ErrorCode;
import com.forMyCV.tinylink.models.ClickEvent;
import com.forMyCV.tinylink.urlShortener.entity.UrlData;
import com.forMyCV.tinylink.urlShortener.repository.UrlDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlStorageService {


    private final UrlDataRepository urlDataRepository;


    public void saveUrlDataForShortCode(UrlData urlData) {
        urlDataRepository.save(urlData);
    }
    public void deactivateUrlDataForShortCode(UrlData urlData) {
        urlData.setActive(false);
        urlDataRepository.save(urlData);
    }
    public void addClickEvent(UrlData urlData, ClickEvent clickEvent) {
        log.debug("addClickEvent urlData: {}", urlData);

        clickEvent.setUrlData(urlData);
        urlData.getClickEvents().add(clickEvent);
        urlDataRepository.save(urlData);
    }

    public String getOriginalUrl(String shortCode) {
        return getUrlData(shortCode).getOriginalUrl();
    }

    public UrlData getUrlData(String shortCode) {
        UrlData urlData = urlDataRepository.findByShortCodeAndIsActive(shortCode,true)
                .orElseThrow(()->new BusinessException(ErrorCode.SHORT_CODE_NOT_FOUND, shortCode));

        if (UrlDataIsExpired(urlData.getExpiresAt())){
            urlData.setActive(false);
            throw new BusinessException(ErrorCode.SHORT_CODE_IS_EXPIRED, shortCode);
        }
        return urlData;
    }

    private boolean UrlDataIsExpired(LocalDateTime expirationTime) {
        return expirationTime!= null && expirationTime.isBefore(LocalDateTime.now());
    }

    public boolean checkUniquenessForShortCode(String shortCode) {
        return urlDataRepository.findByShortCodeAndIsActive(shortCode, true).isEmpty();
    }
    public List<UrlData> getAlLUrlDataByEmail(String email) {
        return urlDataRepository.findAllByUser_email(email)
                .orElseThrow(()->new BusinessException(ErrorCode.NO_SHORTENED_URLS_FOUND));
    }

    public List<UrlData> getAllExpired(LocalDateTime localDateTime) {
        return urlDataRepository.findByIsActiveAndExpiresAtAfter(true,localDateTime);
    }
}
