package com.sw.tse.core.util;

public final class StringUtil {

	
	public static String validarTamanho(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }
	
	public static String removeMascaraCpf(String cpf) {
		if(cpf == null) {
			return null;
		}
		cpf = cpf.trim();
		cpf = cpf.replace(".", "");
		cpf = cpf.replace("-", "");
		
		return cpf;
	}
	
	public static String removerMascaraCep(String cep) {
		cep = cep.trim();
		cep = cep.replace("-", "");
		cep = cep.replace(".", "");
		cep = cep.replaceAll("\\s+", "");
		return cep;
	}
	
	public static String removerMascaraTelefone(String telefone) {
		telefone = telefone.trim();
		telefone = telefone.replace("(", "");
		telefone = telefone.replace(")", "");
		telefone = telefone.replace("-", "");
		telefone = telefone.replaceAll("\\D", "");
		return telefone;
	}
}
