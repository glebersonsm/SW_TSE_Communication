package com.sw.tse.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TelefoneResponse {
    private String ddi;
    private String ddd;
    private String numero;
}
