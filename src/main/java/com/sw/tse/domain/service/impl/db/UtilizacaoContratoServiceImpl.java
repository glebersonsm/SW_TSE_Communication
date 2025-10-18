package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.HospedeReservaDto;
import com.sw.tse.domain.expection.HospedesObrigatoriosException;
import com.sw.tse.domain.expection.PessoaNotFoundException;
import com.sw.tse.domain.expection.TipoHospedeNotFoundException;
import com.sw.tse.domain.expection.TipoUtilizacaoContratoInvalidoException;
import com.sw.tse.domain.model.db.FaixaEtaria;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.model.db.PeriodoModeloCota;
import com.sw.tse.domain.model.db.TipoHospede;
import com.sw.tse.domain.model.db.TipoUtilizacaoContrato;
import com.sw.tse.domain.model.db.UtilizacaoContrato;
import com.sw.tse.domain.model.db.UtilizacaoContratoHospede;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.repository.TipoHospedeRepository;
import com.sw.tse.domain.repository.UtilizacaoContratoRepository;
import com.sw.tse.domain.service.interfaces.FaixaEtariaService;
import com.sw.tse.domain.service.interfaces.UtilizacaoContratoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UtilizacaoContratoServiceImpl implements UtilizacaoContratoService {
    
    private final FaixaEtariaService faixaEtariaService;
    private final PessoaRepository pessoaRepository;
    private final TipoHospedeRepository tipoHospedeRepository;
    private final UtilizacaoContratoRepository utilizacaoContratoRepository;
    
    @Value("${sw.tse.utilizacao.id.pensao}")
    private Long idUtilizacaoContratoPensao;
    
    @Override
    public UtilizacaoContrato criarUtilizacaoContratoReserva(
            PeriodoModeloCota periodoModeloCota,
            OperadorSistema usuarioResponsavel,
            TipoUtilizacaoContrato tipoUtilizacaoContrato,
            List<HospedeReservaDto> hospedes) {
        
        log.info("Iniciando criação de utilização de contrato do tipo RESERVA");
        
        // Validações obrigatórias
        validarParametros(hospedes, tipoUtilizacaoContrato);
        
        // Criar utilização usando método factory simplificado
        UtilizacaoContrato utilizacao = UtilizacaoContrato.criarUtilizacaoContratoReserva(
            periodoModeloCota,
            usuarioResponsavel,
            tipoUtilizacaoContrato
        );
        
        // Contadores para quantitativos
        int qtdAdultos = 0;
        int qtdCriancas = 0;
        
        // Processar cada hóspede
        for (int i = 0; i < hospedes.size(); i++) {
            HospedeReservaDto hospedeDto = hospedes.get(i);
            
            // Buscar Pessoa
            Pessoa pessoa = pessoaRepository.findById(hospedeDto.idPessoa())
                .orElseThrow(() -> new PessoaNotFoundException(hospedeDto.idPessoa()));
            
            // Buscar TipoHospede
            TipoHospede tipoHospede = tipoHospedeRepository.findById(hospedeDto.idTipoHospede())
                .orElseThrow(() -> new TipoHospedeNotFoundException(hospedeDto.idTipoHospede()));
            
            // Separar nome completo
            String nomeCompleto = pessoa.getNome();
            String[] partesNome = nomeCompleto != null ? nomeCompleto.split(" ", 2) : new String[]{"", ""};
            String nome = partesNome[0];
            String sobrenome = partesNome.length > 1 ? partesNome[1] : "";
            
            // Definir se é principal (primeiro hóspede)
            boolean isPrincipal = (i == 0);
            
            // Calcular faixa etária
            FaixaEtaria faixaEtaria = faixaEtariaService.calcularFaixaEtariaPorDataNascimento(pessoa.getDataNascimento());
            
            // Contar quantitativos baseado na faixa etária
            String siglaFaixaEtaria = faixaEtaria.getSigla();
            if ("ADT".equals(siglaFaixaEtaria)) {
                qtdAdultos++;
            } else if ("CH1".equals(siglaFaixaEtaria) || "CH2".equals(siglaFaixaEtaria)) {
                qtdCriancas++;
            }
            
            log.debug("Hóspede {}: nome={}, faixa etária={}, isPrincipal={}", 
                i + 1, nomeCompleto, siglaFaixaEtaria, isPrincipal);
            
            // Criar hóspede
            UtilizacaoContratoHospede hospede = UtilizacaoContratoHospede.novoHospede(
                utilizacao,
                nome,
                sobrenome,
                pessoa.getCpfCnpj(),
                pessoa.getSexo() != null ? pessoa.getSexo().getCodigo() : null,
                pessoa.getDataNascimento() != null ? pessoa.getDataNascimento().atStartOfDay() : null,
                faixaEtaria,
                tipoHospede,
                isPrincipal,
                utilizacao.getResponsavelCadastro()
            );
            
            // Adicionar hóspede à utilização
            utilizacao.adicionarHospede(hospede);
        }
        
        // Setar quantitativos na utilização
        utilizacao.setQuantitativosHospedes(qtdAdultos, qtdCriancas);
        
        log.info("Utilização criada com {} hóspedes: {} adultos, {} crianças", 
            hospedes.size(), qtdAdultos, qtdCriancas);
        
        // Salvar utilização
        UtilizacaoContrato utilizacaoSalva = utilizacaoContratoRepository.save(utilizacao);
        
        log.info("Utilização de contrato RESERVA criada com sucesso. ID: {}", utilizacaoSalva.getId());
        
        return utilizacaoSalva;
    }
    
    private void validarParametros(List<HospedeReservaDto> hospedes, TipoUtilizacaoContrato tipoUtilizacaoContrato) {
        // Validar lista de hóspedes
        if (hospedes == null || hospedes.isEmpty()) {
            throw new HospedesObrigatoriosException();
        }
        
        // Validar sigla do tipo
        if (tipoUtilizacaoContrato == null || !"RESERVA".equals(tipoUtilizacaoContrato.getSigla())) {
            String sigla = tipoUtilizacaoContrato != null ? tipoUtilizacaoContrato.getSigla() : "null";
            throw new TipoUtilizacaoContratoInvalidoException(sigla);
        }
        
        log.debug("Validações de parâmetros passaram com sucesso");
    }
}
