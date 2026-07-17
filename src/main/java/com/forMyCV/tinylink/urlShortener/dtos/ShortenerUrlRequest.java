package com.forMyCV.tinylink.urlShortener.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortenerUrlRequest {

    @NotBlank(message = "URl needed to change is required")
    @Pattern(regexp = "^https?://.*",message = "URL must start with http or https")
    private String originalUrl;

    private String customAlias;

    private LocalDateTime expiresAt;
}
