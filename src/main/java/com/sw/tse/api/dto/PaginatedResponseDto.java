package com.sw.tse.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    private int statusCode;
    private boolean success;
    private List<T> data;
    private String message;
    private int pageNumber;
    private int lastPageNumber;
}

