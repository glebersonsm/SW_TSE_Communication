package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.model.dto.UsuarioContextoDto;

/**
 * Serviço que fornece o contexto agregado do usuário para filtragem de tags de visualização.
 * Usado pelo Portal do Proprietário para decidir quais imagens/documentos o usuário pode ver.
 */
public interface UsuarioContextoService {

    /**
     * Obtém o contexto do usuário a partir do ID da pessoa.
     *
     * @param idPessoa ID da pessoa logada
     * @return contexto agregado (adimplência, empresas, grupos cota, próximo checkin, etc.)
     */
    UsuarioContextoDto obterContextoUsuario(Long idPessoa);
}
