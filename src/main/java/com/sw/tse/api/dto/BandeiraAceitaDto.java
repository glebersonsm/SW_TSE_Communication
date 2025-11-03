package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandeiraAceitaDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("bandeira")
    private String bandeira;
}

