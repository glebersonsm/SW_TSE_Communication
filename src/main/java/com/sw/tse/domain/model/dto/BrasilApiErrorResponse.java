package com.sw.tse.domain.model.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrasilApiErrorResponse {
    private String message;
    private String type;
    private String name;
    private List<BrasilApiError> errors;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrasilApiError {
        private String name;
        private String message;
        private String service;
    }
}

