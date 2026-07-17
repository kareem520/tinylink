package com.forMyCV.tinylink.repositories;

import com.forMyCV.tinylink.models.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Integer> {
    List<ClickEvent> findAllByUrlData_ShortCode(String shortCode);
}
