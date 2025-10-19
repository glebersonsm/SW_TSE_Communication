package com.sw.tse.domain.service.impl.db;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.core.config.UtilizacaoContratoPropertiesCustom;
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
import com.sw.tse.domain.service.interfaces.PessoaService;
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
    private final PessoaService pessoaService;
    private final TipoHospedeRepository tipoHospedeRepository;
    private final UtilizacaoContratoRepository utilizacaoContratoRepository;
    private final UtilizacaoContratoPropertiesCustom utilizacaoContratoConfig;
    
    @Override
    public UtilizacaoContrato criarUtilizacaoContratoReserva(
            PeriodoModeloCota periodoModeloCota,
            OperadorSistema usuarioResponsavel,
            TipoUtilizacaoContrato tipoUtilizacaoContrato,
            List<HospedeDto> hospedes) {
        
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
            HospedeDto hospedeDto = hospedes.get(i);
            
            // Salvar/atualizar pessoa usando service existente
            Long idPessoa = pessoaService.salvar(hospedeDto);
            
            // Buscar Pessoa salva
            Pessoa pessoa = pessoaRepository.findById(idPessoa)
                .orElseThrow(() -> new PessoaNotFoundException(idPessoa));
            
            // Buscar TipoHospede
            Long idTipoHospede = Long.parseLong(hospedeDto.tipoHospede());
            TipoHospede tipoHospede = tipoHospedeRepository.findById(idTipoHospede)
                .orElseThrow(() -> new TipoHospedeNotFoundException(idTipoHospede));
            
            // Separar nome completo em nome e sobrenome
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
                pessoa.getDataNascimento(),
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
        
        // Calcular quantidade de pagantes
        int qtdPagantes = calcularQtdPagantes(utilizacao.getHospedes());
        utilizacao.definirQtdPagantes(qtdPagantes);
        
        // Configurar pensão padrão
        utilizacao.definirIdUtilizacaoContratoTsTipoPensao(utilizacaoContratoConfig.getIdPensaoPadrao());
        
        log.info("Utilização criada com {} hóspedes: {} adultos, {} crianças, {} pagantes", 
            hospedes.size(), qtdAdultos, qtdCriancas, qtdPagantes);
        
        // Salvar utilização
        UtilizacaoContrato utilizacaoSalva = utilizacaoContratoRepository.save(utilizacao);
        
        log.info("Utilização de contrato RESERVA criada com sucesso. ID: {}", utilizacaoSalva.getId());
        
        return utilizacaoSalva;
    }
    
    private void validarParametros(List<HospedeDto> hospedes, TipoUtilizacaoContrato tipoUtilizacaoContrato) {
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
    
    private int calcularQtdPagantes(List<UtilizacaoContratoHospede> hospedes) {
        return (int) hospedes.stream()
            .filter(h -> {
                if (h.getFaixaEtaria() == null) return false;
                
                // Se faixa etária marca como pagante, incluir
                if (Boolean.TRUE.equals(h.getFaixaEtaria().getIsPagante())) {
                    return true;
                }
                
                // Ou se tem idade >= idade mínima configurada, incluir
                if (h.getDataNascimento() != null) {
                    LocalDate dataAtual = LocalDate.now();
                    long anos = ChronoUnit.YEARS.between(h.getDataNascimento(), dataAtual);
                    return anos >= utilizacaoContratoConfig.getIdadeMinimaPagante();
                }
                
                return false;
            })
            .count();
    }
}
