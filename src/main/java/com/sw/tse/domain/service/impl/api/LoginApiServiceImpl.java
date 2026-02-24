package com.sw.tse.domain.service.impl.api;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.core.util.CpfUtil;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ApiTseException;
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
        login = StringUtil.removeMascaraCpf(login);

        TokenApiResponse tokenResponse = tokenTseService.gerarTokenClient(login, password);

        if (!tokenResponse.isError()) {
            log.info("Login via token TSE bem-sucedido para: {}", login);

            // Login bem-sucedido - buscar dados da pessoa pelo CPF (login/userName do
            // token)
            PessoaCpfApiResponse pessoa = pessoaService.buscarPorCpf(login)
                    .orElseThrow(() -> new ApiTseException("Não foi possível localizar os dados da pessoa"));

            log.debug("Dados da pessoa localizados: IdPessoa={}, Nome={}", pessoa.idPessoa(), pessoa.nome());

            LoginResponse loginResponse = LoginResponse.fromToken(tokenResponse, pessoa, login);

            // BUSCA O OPERADOR PARA GARANTIR O ID NO PORTAL
            try {
                BuscaOperadorSistemPessoaResponse operador = operadorSistemaService
                        .buscarPorIdPessoa(pessoa.idPessoa());
                if (operador != null && operador.idOperador() != null) {
                    log.info("Operador localizado para a pessoa: IdOperador={}", operador.idOperador());
                    loginResponse.setIdOperadorManual(operador.idOperador());
                } else {
                    log.warn("Nenhum operador sistema localizado para a pessoa ID: {}", pessoa.idPessoa());
                }
            } catch (Exception e) {
                log.error("Erro ao tentar buscar operador sistema para ID Pessoa {}: {}", pessoa.idPessoa(),
                        e.getMessage());
            }

            return loginResponse;
        }

        log.warn("Falha na tentativa inicial de login para '{}'. Mensagem da API: {}", login,
                tokenResponse.descricaoErro());

        return tratarFalhaDeLogin(login, password, tokenResponse);
    }

    private LoginResponse tratarFalhaDeLogin(String login, String password, TokenApiResponse tokenErrorResponse) {
        final String MSG_USERNAME_INCORRETO = "The user name or password is incorrect.";

        if (MSG_USERNAME_INCORRETO.equals(tokenErrorResponse.descricaoErro())) {
            return tentarCriarNovoOperador(login, password);
        }

        String erroAutenticacao = tokenErrorResponse.descricaoErro() == null ? tokenErrorResponse.erro()
                : tokenErrorResponse.descricaoErro();

        throw new LoginInvalidoTseException(erroAutenticacao);
    }

    private LoginResponse tentarCriarNovoOperador(String cpf, String password) {
        log.info("Tentando fluxo de criação de operador para o login: {}", cpf);

        if (!CpfUtil.isValid(cpf)) {
            throw new LoginInvalidoTseException("CPF inválido. Não é possível prosseguir com o cadastro.");
        }

        PessoaCpfApiResponse pessoa = pessoaService.buscarPorCpf(cpf)
                .orElseThrow(() -> new ApiTseException(
                        "Não localizado em nossa base de clientes, um cliente com cpf informado"));

        List<ContratoPessoaApiResponse> contratosAtivos = contratoService.buscarContratoPorCPF(cpf).stream()
                .filter(contrato -> "ATIVO".equals(contrato.status()) || "ATIVOREV".equals(contrato.status()))
                .toList();

        if (contratosAtivos.isEmpty()) {
            log.warn("Pessoa com CPF {} não possui contratos ativos.", cpf);
            throw new PessoaSemContratoTseException("Você não possui contratos ativos para acessar o portal.");
        }

        BuscaOperadorSistemPessoaResponse operadorExistente = operadorSistemaService
                .buscarPorIdPessoa(pessoa.idPessoa());
        if (operadorExistente.idOperador() != null && operadorExistente.idOperador() > 0) {
            return lidarComOperadorExistente(operadorExistente, password, pessoa, cpf);
        }

        List<Long> idsEmpresas = contratosAtivos.stream()
                .map(ContratoPessoaApiResponse::idEmpresa)
                .distinct()
                .sorted()
                .toList();

        return criarEretornarNovoOperador(cpf, pessoa, idsEmpresas);
    }

    private LoginResponse lidarComOperadorExistente(BuscaOperadorSistemPessoaResponse operador, String password,
            PessoaCpfApiResponse pessoa, String cpf) {
        if (!operador.habilitado()) {
            throw new LoginInvalidoTseException(
                    "Seu acesso está desabilitado. Por favor, entre em contato com a Central de Relacionamento.");
        }

        log.info("Operador já existe para o login {}. Tentando logar novamente com o login correto.", operador.login());
        TokenApiResponse novoToken = tokenTseService.gerarTokenClient(operador.login(), password);

        if (novoToken.isError()) {
            throw new LoginInvalidoTseException("A senha fornecida está incorreta.");
        }

        LoginResponse response = LoginResponse.fromToken(novoToken, pessoa, cpf);
        response.setIdOperadorManual(operador.idOperador());
        return response;
    }

    private LoginResponse criarEretornarNovoOperador(String cpf, PessoaCpfApiResponse pessoa, List<Long> idsEmpresas) {
        log.info("Criando novo operador para a pessoa: {} com acesso às empresas: {}", pessoa.nome(), idsEmpresas);

        if (!StringUtils.hasText(pessoa.email())) {
            throw new LoginInvalidoTseException(
                    "Não é foi possível criar operador, pois não existe e-mail vinculado a pessoa");
        }

        OperadorSistemaRequestDto request = new OperadorSistemaRequestDto(
                cpf, 1, pessoa.email(), idsEmpresas, pessoa.idPessoa());
        OperadorSistemaCriadoApiResponse novoOperador = operadorSistemaService.criarOperadorSistema(request);
        return LoginResponse.fromNovoOperadorCliente(novoOperador, pessoa.email(), pessoa, cpf);
    }
}
