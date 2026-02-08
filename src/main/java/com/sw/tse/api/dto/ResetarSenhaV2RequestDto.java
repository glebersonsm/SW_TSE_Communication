package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetarSenhaV2RequestDto(
        @JsonProperty("Email") @JsonAlias({
                "email" }) String email,
        @JsonProperty("Documento") @JsonAlias({ "documento" }) String documento) {
}
