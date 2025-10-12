package com.sw.tse.domain.converter;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.model.db.ContaFinanceira;

@Component
public class ContaFinanceiraConverter {
    
    public ContaFinanceiraClienteDto toDto(ContaFinanceira contaFinanceira) {
        if (contaFinanceira == null) {
            return null;
        }
        
        ContaFinanceiraClienteDto dto = new ContaFinanceiraClienteDto();
        
        // Campos básicos
        dto.setId(contaFinanceira.getId());
        
        // BoletoId só é preenchido se o meio de pagamento for BOLETO
        if (contaFinanceira.getMeioPagamento() != null && 
            "BOLETO".equalsIgnoreCase(contaFinanceira.getMeioPagamento().getCodMeioPagamento())) {
            dto.setBoletoId(contaFinanceira.getId());
        }
        dto.setStatusParcela(contaFinanceira.calcularStatus());
        dto.setDataHoraCriacao(contaFinanceira.getDataCadastro());
        dto.setDataCriacao(contaFinanceira.getDataCadastro());
        dto.setVencimento(contaFinanceira.getDataVencimento());
        dto.setObservacao(contaFinanceira.getHistorico());
        dto.setLinhaDigitavelBoleto(contaFinanceira.getLinhaDigitavelBoleto());
        dto.setNossoNumeroBoleto(contaFinanceira.getNossoNumeroBoletoCalculado());
        
        // Contrato
        if (contaFinanceira.getContrato() != null) {
            dto.setContrato(contaFinanceira.getContrato().getId());
            dto.setNumeroContrato(contaFinanceira.getContrato().getNumeroContrato());
            
            // Cliente (cessionário)
            if (contaFinanceira.getContrato().getPessoaCessionario() != null) {
                dto.setClienteId(contaFinanceira.getContrato().getPessoaCessionario().getIdPessoa());
                dto.setNomePessoa(contaFinanceira.getContrato().getPessoaCessionario().getNome());
                dto.setDocumentoCliente(contaFinanceira.getContrato().getPessoaCessionario().getCpfCnpj());
            }
        }
        
        // Empresa
        if (contaFinanceira.getEmpresa() != null) {
            dto.setEmpresaId(contaFinanceira.getEmpresa().getId());
            dto.setEmpresaNome(contaFinanceira.getEmpresa().getSigla());
        }
        
        // Pessoa
        if (contaFinanceira.getPessoa() != null) {
            dto.setPessoaId(contaFinanceira.getPessoa().getIdPessoa());
            dto.setPessoaProviderId(contaFinanceira.getPessoa().getIdPessoa());
        }
        
        // Origem Conta (Tipo Origem)
        if (contaFinanceira.getOrigemConta() != null) {
            dto.setCodigoTipoConta(contaFinanceira.getOrigemConta().getIdTipoOrigemContaFinanceira());
            dto.setNomeTipoConta(contaFinanceira.getOrigemConta().getDescricao());
        }
        
        // Meio Pagamento
        if (contaFinanceira.getMeioPagamento() != null) {
            dto.setIdMeioPagamento(contaFinanceira.getMeioPagamento().getIdMeioPagamento());
            dto.setMeioPagamento(contaFinanceira.getMeioPagamento().getDescricao());
        }
        
        // Fracão Cota (via Contrato → CotaUh)
        if (contaFinanceira.getContrato() != null && contaFinanceira.getContrato().getCotaUh() != null) {
            dto.setFracaoCota(contaFinanceira.getContrato().getCotaUh().getIdentificadorUnicoCota());
        }
        
        // Valor calculado usando o método da entity (valor original sem juros/multa)
        dto.setValor(contaFinanceira.calcularValorTotal());
        
        // Saldo fixo em 0 conforme solicitado
        dto.setSaldo(BigDecimal.ZERO);
        
        // Campos de juros e multas usando os novos métodos de cálculo
        dto.setValorJuroDiario(contaFinanceira.calcularJuroDiario());
        dto.setPercentualJuroDiario(contaFinanceira.getPercentualJuroDiario());
        dto.setValorJuroMensal(contaFinanceira.calcularJuroMensal()); // Juros de 30 dias
        dto.setPercentualJuroMensal(contaFinanceira.getPercentualJuroMensal());
        dto.setPercentualMulta(contaFinanceira.getPercentualMultaCalculado());
        dto.setValorAtualizado(contaFinanceira.calcularValorAtualizado());
        
        // Campos de data
        dto.setDataHoraBaixa(contaFinanceira.getDataBaixa());
        dto.setDataProcessamento(contaFinanceira.getDataPagamento());
        
        // Status CRC - valor padrão
        dto.setStatusCrcBloqueiaPagamento("N");
        
        return dto;
    }
}
