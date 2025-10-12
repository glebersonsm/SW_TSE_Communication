package com.sw.tse.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.service.interfaces.PessoaService;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PessoaService pessoaService;

    @MockBean
    private PessoaConverter pessoaConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void salvar_DeveRetornarIdPessoa_QuandoDadosValidos() throws Exception {
        // Given
        HospedeDto hospedeDto = new HospedeDto(
            1L, // id
            1L, // idHospede
            "TITULAR", // tipoHospede
            1L, // clienteId
            "S", // principal
            "João Silva", // nome
            "CPF", // tipoDocumento
            "12345678901", // numeroDocumento
            java.time.LocalDate.of(1990, 1, 1), // dataNascimento
            "joao@email.com", // email
            "55", // ddi
            "11", // ddd
            "999999999", // telefone
            "M", // sexo
            "1234567", // codigoIbge
            "Rua A", // logradouro
            "123", // numero
            "Centro", // bairro
            "Apto 1", // complemento
            "01234567", // cep
            java.time.LocalDate.now(), // checkIn
            java.time.LocalDate.now().plusDays(7) // checkOut
        );

        Long idPessoaSalva = 123L;
        when(pessoaService.salvar(any(HospedeDto.class))).thenReturn(idPessoaSalva);

        // When & Then
        mockMvc.perform(post("/api/v1/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hospedeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(idPessoaSalva))
                .andExpect(jsonPath("$.message").value("Pessoa salva com sucesso."));
    }

    @Test
    void salvar_DeveRetornarBadRequest_QuandoDadosInvalidos() throws Exception {
        // Given
        HospedeDto hospedeDto = new HospedeDto(
            null, // id - inválido
            null, // idHospede
            "TITULAR", // tipoHospede
            1L, // clienteId
            "S", // principal
            null, // nome - inválido
            null, // tipoDocumento - inválido
            "12345678901", // numeroDocumento
            null, // dataNascimento - inválido
            "joao@email.com", // email
            "55", // ddi
            "11", // ddd
            "999999999", // telefone
            "M", // sexo
            "1234567", // codigoIbge
            "Rua A", // logradouro
            "123", // numero
            "Centro", // bairro
            "Apto 1", // complemento
            "01234567", // cep
            null, // checkIn - inválido
            null // checkOut - inválido
        );

        // When & Then
        mockMvc.perform(post("/api/v1/pessoa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hospedeDto)))
                .andExpect(status().isBadRequest());
    }
}
