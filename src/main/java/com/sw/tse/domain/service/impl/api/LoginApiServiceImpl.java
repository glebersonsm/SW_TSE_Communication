package com.sw.tse.domain.service.impl.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.core.util.CpfUtil;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.LoginInvalidoTseException;
import com.sw.tse.domain.expection.PessoaSemContratoTseException;
import com.sw.tse.domain.model.api.response.BuscaOperadorSistemPessoaResponse;
import com.sw.tse.domain.model.api.response.ContratoPessoaApiResponse;
import com.sw.tse.domain.model.api.response.LoginResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaCriadoApiResponse;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.api.response.TokenApiResponse;
import com.sw.tse.domain.service.interfaces.ContratoService;
import com.sw.tse.domain.service.interfaces.LoginService;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.PessoaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoginApiServiceImpl implements LoginService {

    private final TokenTseService tokenTseService;
    private final OperadorSistemaService operadorSistemaService;
    private final PessoaService pessoaService;
    private final ContratoService contratoService;

    @Override
    public LoginResponse logarOperadorCliente(String login, String password) {
        TokenApiResponse tokenResponse = tokenTseService.gerarTokenClient(login, password);

        if (!tokenResponse.isError()) {
            return LoginResponse.fromToken(tokenResponse);
        }

        log.warn("Falha na tentativa inicial de login para '{}'. Mensagem da API: {}", login, tokenResponse.descricaoErro());
        
        return tratarFalhaDeLogin(login, password, tokenResponse);
    }

    private LoginResponse tratarFalhaDeLogin(String login, String password, TokenApiResponse tokenErrorResponse) {
        final String MSG_CREDENCIAIS_INCORRETAS = "The user name or password is incorrect.";
        
        if (MSG_CREDENCIAIS_INCORRETAS.equals(tokenErrorResponse.descricaoErro())) {
            return tentarCriarNovoOperador(login, password);
        }

        throw new LoginInvalidoTseException("Falha na autenticação: " + tokenErrorResponse.descricaoErro());
    }

    private LoginResponse tentarCriarNovoOperador(String login, String password) {
        log.info("Tentando fluxo de criação de operador para o login: {}", login);

        if (!CpfUtil.isValid(login)) {
            throw new LoginInvalidoTseException("CPF inválido. Não é possível prosseguir com o cadastro.");
        }

        PessoaCpfApiResponse pessoa = pessoaService.buscarPorCpf(login).get();

        List<ContratoPessoaApiResponse> contratosAtivos = contratoService.buscarContratoPorCPF(login).stream()
                .filter(contrato -> "ATIVO".equals(contrato.status()) || "ATIVOREV".equals(contrato.status()))
                .toList();

        if (contratosAtivos.isEmpty()) {
            log.warn("Pessoa com CPF {} não possui contratos ativos.", login);
            throw new PessoaSemContratoTseException("Você não possui contratos ativos para acessar o portal.");
        }

        BuscaOperadorSistemPessoaResponse operadorExistente = operadorSistemaService.buscarPorIdPessoa(pessoa.idPessoa());
        if (operadorExistente.idOperador() != null && operadorExistente.idOperador() > 0) {
            return lidarComOperadorExistente(operadorExistente, password);
        }

        List<Long> idsEmpresas = contratosAtivos.stream()
                .map(ContratoPessoaApiResponse::idEmpresa)
                .distinct()
                .collect(Collectors.toList());

        return criarEretornarNovoOperador(login, pessoa, idsEmpresas);
    }

    private LoginResponse lidarComOperadorExistente(BuscaOperadorSistemPessoaResponse operador, String password) {
        if (!operador.habilitado()) {
            throw new LoginInvalidoTseException("Seu acesso está desabilitado. Por favor, entre em contato com a Central de Relacionamento.");
        }
        
        log.info("Operador já existe para o login {}. Tentando logar novamente com o login correto.", operador.login());
        TokenApiResponse novoToken = tokenTseService.gerarTokenClient(operador.login(), password);

        if (novoToken.isError()) {
            throw new LoginInvalidoTseException("A senha fornecida está incorreta.");
        }
        return LoginResponse.fromToken(novoToken);
    }

    private LoginResponse criarEretornarNovoOperador(String login, PessoaCpfApiResponse pessoa, List<Long> idsEmpresas) {
        log.info("Criando novo operador para a pessoa: {} com acesso às empresas: {}", pessoa.nome(), idsEmpresas);
        
        login = StringUtil.removeMascaraCpf(login);
        
        if(!StringUtils.hasText(pessoa.email())) {
        	throw new LoginInvalidoTseException("Não é foi possível criar operador, pois não existe e-mail vinculado a pessoa");
        }
        
        OperadorSistemaRequestDto request = new OperadorSistemaRequestDto(
                login, 1, pessoa.email(), idsEmpresas, pessoa.idPessoa()
        );
        OperadorSistemaCriadoApiResponse novoOperador = operadorSistemaService.criarOperadorSistema(request);
        return LoginResponse.fromNovoOperadorCliente(novoOperador, pessoa.email());
    }
}
