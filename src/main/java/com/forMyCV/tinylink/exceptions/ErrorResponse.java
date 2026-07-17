package com.forMyCV.tinylink.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;
    private String code;
    private List<ValidationError> validationErrorList = new ArrayList<>();



    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class ValidationError {
        private String field;
        private String message;
        private String code;
    }
}
