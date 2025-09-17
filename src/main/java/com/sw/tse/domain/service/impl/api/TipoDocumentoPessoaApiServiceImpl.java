package com.sw.tse.domain.service.impl.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.client.RelatorioCustomizadoApiClient;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.request.FiltroRelatorioCustomizadoApiRequest;
import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaApiResponse;
import com.sw.tse.domain.service.interfaces.TipoDocumentoPessoaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
public class TipoDocumentoPessoaApiServiceImpl implements TipoDocumentoPessoaService {

    private final RelatorioCustomizadoApiService relatorioCustomizado;
    
    @Value("${api.tse.relatorios.tipodocumentopessoa}")
    private Long idRelatorioTipoDocumentoPessoa;

    @Override
    public List<TipoDocumentoPessoaApiResponse> listarTiposDocumento() {
        
        if (idRelatorioTipoDocumentoPessoa == null || idRelatorioTipoDocumentoPessoa.equals(0L)) {
            throw new ApiTseException("Relatório customizado para tipo documento pessoa não parametrizado");
        }
                
        try {
            List<FiltroRelatorioCustomizadoApiRequest> filtros = List.of();
            return relatorioCustomizado.buscarRelatorioGenerico(idRelatorioTipoDocumentoPessoa, filtros, TipoDocumentoPessoaApiResponse.class);
            
        } catch (FeignException e) {
            log.error("Erro ao chamar a api de tipos de documento pessoa: {}", e.getMessage());
            throw new ApiTseException(String.format("Erro: %s ao obter tipos de documento pela api do TSE", e.contentUTF8()));
        }
    }
}