package com.sw.tse.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.service.impl.db.PessoaDbServiceImpl;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private PessoaConverter pessoaConverter;

    @Mock
    private OperadorSistemaService operadorSistemaService;

    @InjectMocks
    private PessoaDbServiceImpl pessoaService;

    private HospedeDto hospedeDto;
    private OperadorSistema operadorSistema;

    @BeforeEach
    void setUp() {
        hospedeDto = new HospedeDto(
            1L, // id
            1L, // idHospede
            "TITULAR", // tipoHospede
            1L, // clienteId
            "S", // principal
            "João Silva", // nome
            "CPF", // tipoDocumento
            "12345678901", // numeroDocumento
            LocalDate.of(1990, 1, 1), // dataNascimento
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
            LocalDate.now(), // checkIn
            LocalDate.now().plusDays(7) // checkOut
        );

        operadorSistema = new OperadorSistema();
        operadorSistema.setId(1L);
        operadorSistema.setNome("Sistema");
    }

    @Test
    void salvar_DeveRetornarIdPessoa_QuandoPessoaNova() {
        // Given
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(123L);
        
        when(operadorSistemaService.operadorSistemaPadraoCadastro()).thenReturn(operadorSistema);
        when(pessoaConverter.hospedeDtoToPessoa(any(HospedeDto.class), any(Pessoa.class), any(OperadorSistema.class)))
                .thenReturn(pessoa);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        // When
        Long idPessoa = pessoaService.salvar(hospedeDto);

        // Then
        assertEquals(123L, idPessoa);
    }

    @Test
    void buscarPorCpf_DeveRetornarPessoa_QuandoCpfExiste() {
        // Given
        String cpf = "12345678901";
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(123L);
        pessoa.setCpfCnpj(cpf);
        
        PessoaCpfApiResponse expectedResponse = new PessoaCpfApiResponse(123L, "João Silva", cpf);
        
        when(pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(cpf))
                .thenReturn(java.util.List.of(pessoa));
        when(pessoaConverter.toPessoaCpfApiResponse(any(Pessoa.class)))
                .thenReturn(expectedResponse);

        // When
        Optional<PessoaCpfApiResponse> result = pessoaService.buscarPorCpf(cpf);

        // Then
        assertEquals(true, result.isPresent());
        assertEquals(123L, result.get().idPessoa());
        assertEquals("João Silva", result.get().nome());
    }

    @Test
    void buscarPorCpf_DeveRetornarEmpty_QuandoCpfNaoExiste() {
        // Given
        String cpf = "99999999999";
        
        when(pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(cpf))
                .thenReturn(java.util.List.of());

        // When
        Optional<PessoaCpfApiResponse> result = pessoaService.buscarPorCpf(cpf);

        // Then
        assertEquals(false, result.isPresent());
    }
}
