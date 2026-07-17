package com.forMyCV.tinylink.rateLimit.repository;

import com.forMyCV.tinylink.rateLimit.entity.RateLimitData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitDataRepository extends JpaRepository<RateLimitData, Long> {
}
