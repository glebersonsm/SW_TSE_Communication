package com.sw.tse.api.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.dto.UsuarioContextoDto;
import com.sw.tse.domain.service.interfaces.UsuarioContextoService;
import com.sw.tse.security.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller que expõe o contexto do usuário para o Portal do Proprietário.
 * Ativo apenas quando o backend usa banco de dados (database.enabled=true).
 */
@ConditionalOnBean(UsuarioContextoService.class)
@RestController
@RequestMapping("/api/v1/painelcliente")
@RequiredArgsConstructor
@Tag(name = "Contexto do Usuário", description = "Contexto para filtragem de tags de visualização")
public class UsuarioContextoController {

    private final UsuarioContextoService usuarioContextoService;

    @Operation(summary = "Obter contexto do usuário",
               description = "Retorna dados agregados do usuário (adimplência, empresas, grupos cota, próximo checkin) para filtragem de imagens/documentos no Portal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contexto obtido com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioContextoDto.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido ou ausente", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/contexto-usuario")
    public ResponseEntity<ApiResponseDto<UsuarioContextoDto>> obterContextoUsuario() {
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();

        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }

        UsuarioContextoDto contexto = usuarioContextoService.obterContextoUsuario(idPessoaCliente);

        ApiResponseDto<UsuarioContextoDto> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                contexto,
                "Contexto obtido com sucesso"
        );

        return ResponseEntity.ok(responseApi);
    }
}
