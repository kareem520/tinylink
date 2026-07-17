package com.forMyCV.tinylink.models;

import com.forMyCV.tinylink.urlShortener.entity.UrlData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private String country;
    private String city;
    @ManyToOne
    @JoinColumn(name= "url_data_id")
    private UrlData urlData;
}