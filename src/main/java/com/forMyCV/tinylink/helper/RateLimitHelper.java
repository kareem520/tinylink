package com.forMyCV.tinylink.helper;

import com.forMyCV.tinylink.response.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Data
public class RateLimitHelper {



   private final Response<Boolean>  Failedresponse = Response.<Boolean>builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("Too many Requests, try again later")
            .data(false)
            .build();


    private final Response<Boolean>  Successfulresponse = Response.<Boolean>builder()
            .statusCode(HttpStatus.OK.value())
            .message("ok, you are allowed to send your request")
            .data(true)
            .build();





}
