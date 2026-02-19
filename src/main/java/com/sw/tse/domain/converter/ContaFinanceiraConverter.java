package com.sw.tse.domain.converter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.db.UnidadeHoteleira;

@Component
public class ContaFinanceiraConverter {

    public ContaFinanceiraClienteDto toDto(ContaFinanceira contaFinanceira) {
        return toDto(contaFinanceira, false);
    }

    public ContaFinanceiraClienteDto toDto(ContaFinanceira contaFinanceira, boolean incluirMemoriaCalculo) {
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

        // Tratamento especial para destino = 'P'
        // Deve trazer como pago mesmo que não esteja pago
        String statusCalculado = contaFinanceira.calcularStatus();
        if ("P".equalsIgnoreCase(contaFinanceira.getDestino())) {
            statusCalculado = "PAGO";
        }
        dto.setStatusParcela(statusCalculado);

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

            // PessoaEmpreendimentoId - já mapeado como EmpresaId
            dto.setPessoaEmpreendimentoId(contaFinanceira.getEmpresa().getId());

            // EmpreendimentoCnpj e EmpreendimentoNome
            if (contaFinanceira.getEmpresa().getPessoa() != null) {
                dto.setEmpreendimentoCnpj(contaFinanceira.getEmpresa().getPessoa().getCpfCnpj());
                dto.setEmpreendimentoNome(contaFinanceira.getEmpresa().getPessoa().getNome());
            }
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
            // Usar descricao se disponível, senão usar codMeioPagamento
            String descricaoMeioPagamento = contaFinanceira.getMeioPagamento().getDescricao();
            if (descricaoMeioPagamento == null || descricaoMeioPagamento.trim().isEmpty()) {
                descricaoMeioPagamento = contaFinanceira.getMeioPagamento().getCodMeioPagamento();
            }
            dto.setMeioPagamento(descricaoMeioPagamento);
        }

        // Fracão Cota (via Contrato → CotaUh)
        if (contaFinanceira.getContrato() != null && contaFinanceira.getContrato().getCotaUh() != null) {
            dto.setFracaoCota(contaFinanceira.getContrato().getCotaUh().getIdentificadorUnicoCota());

            // NumeroImovel e BlocoCodigo (via CotaUh → UnidadeHoteleira)
            if (contaFinanceira.getContrato().getCotaUh().getUnidadeHoteleira() != null) {
                UnidadeHoteleira uh = contaFinanceira.getContrato().getCotaUh().getUnidadeHoteleira();

                // NumeroImovel
                dto.setNumeroImovel(uh.getDescricao());

                // BlocoCodigo e IdTorre (via UnidadeHoteleira → EdificioHotel)
                if (uh.getEdificioHotel() != null) {
                    dto.setBlocoCodigo(uh.getEdificioHotel().getDescricao());
                    dto.setIdTorre(uh.getEdificioHotel().getId());
                }
            }
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

        // PercentualMulta - usar regra existente
        dto.setPercentualMulta(contaFinanceira.getPercentualMultaCalculado());

        // PercentualMultaCar - valor da multa calculada para contas vencidas
        // Usar statusCalculado (já considera destinoContaFinanceira = 'P')
        if ("VENCIDO".equals(statusCalculado)) {
            dto.setPercentualMultaCar(contaFinanceira.calcularMulta());
        }

        // DataBaseAplicacaoJurosMultas - usar dataVencimentoOriginal
        dto.setDataBaseAplicacaoJurosMultas(contaFinanceira.getDataVencimentoOriginal());

        // PodeAplicarMulta - true para contas vencidas
        // Usar statusCalculado (já considera destinoContaFinanceira = 'P')
        if ("VENCIDO".equals(statusCalculado)) {
            dto.setPodeAplicarMulta("S"); // true como string
        } else {
            dto.setPodeAplicarMulta("N"); // false como string
        }

        dto.setValorAtualizado(contaFinanceira.calcularValorAtualizado());

        // Campos de data
        dto.setDataHoraBaixa(contaFinanceira.getDataBaixa());

        // Tratamento especial para destino = 'P'
        // Se não estiver paga, a data de pagamento pode setar a mesma data de
        // vencimento
        LocalDateTime dataProcessamento = contaFinanceira.getDataPagamento();
        if ("P".equalsIgnoreCase(contaFinanceira.getDestino()) && dataProcessamento == null) {
            // Se destino = 'P' e não tem data de pagamento, usar data de vencimento
            dataProcessamento = contaFinanceira.getDataVencimento();
        }
        dto.setDataProcessamento(dataProcessamento);

        // Status CRC - valor padrão
        dto.setStatusCrcBloqueiaPagamento("N");

        // Juros e Multa calculados (valores totais)
        dto.setJuros(contaFinanceira.calcularJuros()); // Juros total acumulado
        dto.setMulta(contaFinanceira.calcularMulta()); // Multa total

        // LimitePagamentoTransmitido - calcular para contas vencidas
        if (contaFinanceira.getCarteiraBoleto() != null &&
                contaFinanceira.getCarteiraBoleto().getQtdDiasNaoReceberAposVencimento() != null) {

            // Verificar se a conta está vencida usando statusCalculado (já considera
            // destinoContaFinanceira = 'P')
            if ("VENCIDO".equals(statusCalculado)) {
                LocalDateTime dataBase = contaFinanceira.getDataVencimentoOriginal() != null
                        ? contaFinanceira.getDataVencimentoOriginal()
                        : contaFinanceira.getDataVencimento();

                if (dataBase != null) {
                    Integer diasLimite = contaFinanceira.getCarteiraBoleto().getQtdDiasNaoReceberAposVencimento();
                    dto.setLimitePagamentoTransmitido(dataBase.plusDays(diasLimite));
                }
            }
        }

        // Memória de cálculo (apenas no modo simulação)
        if (incluirMemoriaCalculo) {
            dto.setMemoriaCalculo(contaFinanceira.obterMemoriaCalculo());
        }

        return dto;
    }
}
