package com.forMyCV.tinylink.urlShortener.repository;


import com.forMyCV.tinylink.urlShortener.entity.UrlData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlDataRepository extends JpaRepository<UrlData, Integer> {
    Optional<UrlData> findByShortCodeAndIsActive(String shortCode,boolean isActive);
    List<UrlData> findAllByIsActive(boolean isActive);
    List<UrlData> findByIsActiveAndExpiresAtAfter(Boolean isActive,LocalDateTime now);
    Optional<List<UrlData>> findAllByUser_email(String email);
}
