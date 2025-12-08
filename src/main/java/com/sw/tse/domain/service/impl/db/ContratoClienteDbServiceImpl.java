package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;
import com.sw.tse.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoClienteDbServiceImpl implements ContratoClienteService {

    private final ContratoRepository contratoRepository;

    @Override
    public List<ContratoClienteApiResponse> buscarContratosCliente() {
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            log.error("ID da pessoa cliente não encontrado no contexto JWT");
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        log.info("Buscando contratos para a pessoa cliente com ID: {}", idPessoaCliente);
        
        List<ContratoClienteApiResponse> todosContratos = contratoRepository.buscarContratosClientePorIdPessoa(idPessoaCliente);
        
        // Filtrar apenas contratos com status ATIVO ou ATIVOREV
        List<ContratoClienteApiResponse> contratosAtivos = todosContratos.stream()
                .filter(contrato -> "ATIVO".equals(contrato.getStatusContrato()) || "ATIVOREV".equals(contrato.getStatusContrato()))
                .toList();
        
        log.info("Filtrados {} contratos ativos de um total de {} contratos para a pessoa cliente ID: {}", 
                contratosAtivos.size(), todosContratos.size(), idPessoaCliente);
        
        return contratosAtivos;
    }

    @Override
    public List<ContratoClienteApiResponse> buscarContratosPorIdUsuario(Long idUsuario) {
        if (idUsuario == null) {
            log.error("ID do usuário não fornecido");
            throw new IllegalArgumentException("ID do usuário é obrigatório para buscar contratos");
        }
        
        log.info("Buscando contratos para o usuário com ID: {}", idUsuario);
        
        // Assumindo que idUsuario corresponde ao idPessoa na implementação DB
        List<ContratoClienteApiResponse> todosContratos = contratoRepository.buscarContratosClientePorIdPessoa(idUsuario);
        
        // Filtrar apenas contratos com status ATIVO ou ATIVOREV
        List<ContratoClienteApiResponse> contratosAtivos = todosContratos.stream()
                .filter(contrato -> "ATIVO".equals(contrato.getStatusContrato()) || "ATIVOREV".equals(contrato.getStatusContrato()))
                .toList();
        
        log.info("Filtrados {} contratos ativos de um total de {} contratos para o usuário ID: {}", 
                contratosAtivos.size(), todosContratos.size(), idUsuario);
        
        return contratosAtivos;
    }
}