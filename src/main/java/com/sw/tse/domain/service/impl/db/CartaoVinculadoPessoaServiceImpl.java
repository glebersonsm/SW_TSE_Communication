package com.sw.tse.domain.service.impl.db;

import com.sw.tse.api.dto.*;
import com.sw.tse.domain.model.db.*;
import com.sw.tse.domain.repository.*;
import com.sw.tse.domain.service.interfaces.CartaoVinculadoPessoaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartaoVinculadoPessoaServiceImpl implements CartaoVinculadoPessoaService {
    
    private final CartaoVinculadoPessoaRepository cartaoRepository;
    private final PessoaRepository pessoaRepository;
    private final OperadorSistemaRepository operadorSistemaRepository;
    private final BandeirasAceitasRepository bandeirasAceitasRepository;
    
    @Override
    @Transactional
    public CartaoVinculadoResponseDto salvarCartao(SalvarCartaoRequestDto dto, Long pessoaId) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        
        // Buscar OperadorSistema responsável pelo cadastro
        OperadorSistema responsavel = operadorSistemaRepository
            .findByPessoa_IdPessoa(pessoaId)
            .orElse(null);
        
        // Buscar bandeira selecionada pelo usuário
        BandeirasAceitas bandeira = null;
        if (dto.getIdBandeirasAceitas() != null) {
            bandeira = bandeirasAceitasRepository.findById(dto.getIdBandeirasAceitas()).orElse(null);
        }
        // Se não foi informada, tentar identificar automaticamente
        if (bandeira == null) {
            bandeira = identificarBandeira(dto.getNumeroCartao());
        }
        
        CartaoVinculadoPessoa cartao = new CartaoVinculadoPessoa();
        cartao.setPessoa(pessoa);
        cartao.setResponsavelCadastro(responsavel);
        cartao.setBandeira(bandeira);
        cartao.setNumeroCartao(dto.getNumeroCartao());
        cartao.setCodigoSeguranca(dto.getCodigoSeguranca());
        cartao.setMesValidade(dto.getMesValidade());
        cartao.setAnoValidade(dto.getAnoValidade());
        cartao.setNomeNoCartao(dto.getNomeNoCartao());
        cartao.setNumeroMascarado(formatarNumeroMascarado(dto.getNumeroCartao()));
        cartao.setTipoOperacao("CREDAV");
        cartao.setAtivo(true);
        cartao.setPertenceTerceiro(false);
        
        CartaoVinculadoPessoa salvo = cartaoRepository.save(cartao);
        
        log.info("Cartão salvo com sucesso para pessoa ID: {}, Bandeira: {}", 
            pessoaId, bandeira != null ? bandeira.getBandeira() : "Não identificada");
        
        return mapearParaResponse(salvo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartaoVinculadoResponseDto> listarCartoesPessoa(Long pessoaId) {
        List<CartaoVinculadoPessoa> cartoes = cartaoRepository.findAtivosByPessoaId(pessoaId);
        return cartoes.stream()
            .map(this::mapearParaResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CartaoParaPagamentoDto obterCartaoParaPagamento(Long cartaoId, Long pessoaId) {
        CartaoVinculadoPessoa cartao = cartaoRepository.findByIdAndPessoaIdAndAtivoTrue(cartaoId, pessoaId)
            .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        
        return CartaoParaPagamentoDto.builder()
            .numeroCartao(cartao.getNumeroCartao())
            .codigoSeguranca(cartao.getCodigoSeguranca())
            .mesValidade(cartao.getMesValidade())
            .anoValidade(cartao.getAnoValidade())
            .nomeNoCartao(cartao.getNomeNoCartao())
            .build();
    }
    
    @Override
    @Transactional
    public void removerCartao(Long cartaoId, Long pessoaId) {
        CartaoVinculadoPessoa cartao = cartaoRepository.findByIdAndPessoaIdAndAtivoTrue(cartaoId, pessoaId)
            .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        
        cartao.setAtivo(false);
        cartaoRepository.save(cartao);
        
        log.info("Cartão ID: {} removido (soft delete) para pessoa ID: {}", cartaoId, pessoaId);
    }
    
    private CartaoVinculadoResponseDto mapearParaResponse(CartaoVinculadoPessoa cartao) {
        String descricaoBandeira = cartao.getBandeira() != null 
            ? cartao.getBandeira().getBandeira() 
            : "Não identificada";
            
        return CartaoVinculadoResponseDto.builder()
            .id(cartao.getId())
            .ultimosDigitos(cartao.getNumeroMascarado())
            .mesValidade(cartao.getMesValidade())
            .anoValidade(cartao.getAnoValidade())
            .nomeNoCartao(cartao.getNomeNoCartao())
            .bandeira(descricaoBandeira)
            .build();
    }
    
    private BandeirasAceitas identificarBandeira(String numeroCartao) {
        String numero = numeroCartao.replaceAll("\\s", "");
        String nomeBandeira = null;
        
        // Identificar bandeira pelos primeiros dígitos
        if (numero.startsWith("4")) {
            nomeBandeira = "Visa";
        } else if (numero.startsWith("5")) {
            nomeBandeira = "Mastercard";
        } else if (numero.startsWith("34") || numero.startsWith("37")) {
            nomeBandeira = "Amex";
        } else if (numero.startsWith("6011") || numero.startsWith("65")) {
            nomeBandeira = "Discover";
        } else if (numero.startsWith("3528") || numero.startsWith("3589")) {
            nomeBandeira = "JCB";
        } else if (numero.startsWith("636") || numero.startsWith("638")) {
            nomeBandeira = "Elo";
        } else if (numero.startsWith("606282")) {
            nomeBandeira = "Hipercard";
        }
        
        // Buscar bandeira na tabela
        if (nomeBandeira != null) {
            return bandeirasAceitasRepository.findByBandeiraIgnoreCase(nomeBandeira).orElse(null);
        }
        
        return null;
    }
    
    private String formatarNumeroMascarado(String numeroCartao) {
        String numero = numeroCartao.replaceAll("\\s", "");
        if (numero.length() >= 8) {
            String primeiros4 = numero.substring(0, 4);
            String ultimos4 = numero.substring(numero.length() - 4);
            return primeiros4 + " **** **** " + ultimos4;
        }
        return "****";
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BandeiraAceitaDto> listarBandeirasAceitas() {
        return bandeirasAceitasRepository.findAll().stream()
            .map(bandeira -> BandeiraAceitaDto.builder()
                .id(bandeira.getId())
                .bandeira(bandeira.getBandeira())
                .build())
            .collect(Collectors.toList());
    }
}

