package com.forMyCV.tinylink.services;

import com.forMyCV.tinylink.models.ClickEvent;
import com.forMyCV.tinylink.repositories.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClickEventStorageService {


    private final ClickEventRepository clickEventRepository;


    public List<ClickEvent> getShortCodeClickEvents(String shortCode) {
        return clickEventRepository.findAllByUrlData_ShortCode(shortCode);
    }

}
