package com.sw.tse.domain.service.impl.api;

import org.springframework.stereotype.Service;

import com.sw.tse.core.util.CpfUtil;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.expection.ClienteNaoEncontratoTseException;
import com.sw.tse.domain.expection.LoginInvalidoTseException;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.model.api.response.TokenApiResponse;
import com.sw.tse.domain.service.interfaces.LoginService;
import com.sw.tse.domain.service.interfaces.PessoaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoginApiService implements LoginService{
	
	private final TokenTseService tokenTseService;
	private final PessoaService pessoaService;

	@Override
	public TokenApiResponse logar(String login, String password) {
		TokenApiResponse tokenCliente = tokenTseService.gerarTokenClient(login, password);
		
		if(!tokenCliente.isError()) {
			return tokenCliente;
		}
		
		return validarErroLoginClient(tokenCliente, login, password);
	}

	private TokenApiResponse validarErroLoginClient(TokenApiResponse tokenCliente, String login, String password) {
		
		if("The user name or password is incorrect.".equals(tokenCliente.descricaoErro())) {
			log.info("Login informado não existe, tenando criar um usuário para o login: {}", login);
			if(!CpfUtil.isValid(login)) {
				log.info("Login ou senha incorretos para o usuário '{}'. Verificando se é um novo cadastro válido...", login);
				String errorMessage = String.format("Login '%s' não é um CPF válido e não pode ser usado para um novo cadastro.", login);
				throw new LoginInvalidoTseException(errorMessage);
			}
			
			log.info("Localizando a pessoa pelo cpf {}", login);
			PessoaCpfApiResponse pessoaCpf = pessoaService.buscarPorCpf(StringUtil.removeMascaraCpf(login))
					.orElseThrow(() -> new ClienteNaoEncontratoTseException(String.format("Cpf %s não localizado em nossa base de clientes", login)));
			
			
		}
		
		if("Login ou senha inválidos.".equals(tokenCliente.erro())) {
			log.info("Login ou senha inválidos");
			throw new LoginInvalidoTseException("Login ou senha inválidos");
		}
		

		return tokenCliente;
	}

}
