package com.forMyCV.tinylink.urlShortener.entity;

import com.forMyCV.tinylink.auth_user.entity.User;
import com.forMyCV.tinylink.models.ClickEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UrlData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int clickCount;
    private String createdBy;
    private boolean isActive;



    @OneToMany(mappedBy = "urlData",cascade = CascadeType.ALL)
    private List<ClickEvent> clickEvents =  new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
