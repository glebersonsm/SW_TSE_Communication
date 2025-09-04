package com.sw.tse.core.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.sw.tse.client.CriptografiaApiClient;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriptografiaService {

    private final CriptografiaApiClient criptografiaApiClient;
    private final TokenTseService tokenTseService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private String key = "QJ2ygv2zfK9hFwaJrzRgEiw739nOB21ihudCzNLAJU8=";
    private String vetor = "B5sBYM/K0NrzwVNlLtq9Sw==";

    
    private byte[] getVetor() {
        return Base64.getDecoder().decode(this.vetor);
    }
    private byte[] getKey() {
        return Base64.getDecoder().decode(this.key);
    }


    public String criptografarValor(String valor) {
    	String bearerToken = "Bearer " + tokenTseService.gerarToken();
    	return criptografiaApiClient.criptografarString(valor.toString(), bearerToken);
    }
    
    
    public String criptografarData(String data) {
    	String bearerToken = "Bearer " + tokenTseService.gerarToken();
    	
        String dataFormatada = LocalDate.parse(data).format(DATE_FORMATTER);
        return criptografiaApiClient.criptografarData(dataFormatada, bearerToken);
    }
    
    
    public String descriptografarValor(String texto) {
    	try {
            if (texto == null) {
                return null;
            }
            if(isEncriptado(texto)) {
            	texto = texto.replace("Z3/C5pzz41ZOUt5rKjeQcQ==", "");
	            byte[] encrypted = Base64.getDecoder().decode(texto);
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            IvParameterSpec ivSpec = new IvParameterSpec(getVetor());
	            SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(getKey(), "AES");
	            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
	            byte[] decrypted = cipher.doFinal(encrypted);
	            return new String(decrypted, StandardCharsets.UTF_8);
            } else {
            	return texto;
            }
		} catch (Exception e) {
			throw new RuntimeException("Erro ao descriptografar");
		}
    }
    
    public LocalDate descriptografarData(LocalDate date) {
    	if(isCriptografadaData(date)) {
    		return date.plusDays(189486);
    	}
    	return date;
    }
    
    public boolean isEncriptado(@NonNull String texto) {
    	boolean encriptado = false;
    	if((texto != null || "".equalsIgnoreCase(texto)) && texto.length() > 24) {
    		encriptado = texto.substring(0, 24).equalsIgnoreCase("Z3/C5pzz41ZOUt5rKjeQcQ==");
    	}
    	return encriptado;
    }
    
    public boolean isCriptografadaData(LocalDate date) {
        LocalDate referenceDate = LocalDate.of(1900, 1, 1);
        return date.isAfter(LocalDate.MIN) && date.isBefore(referenceDate);
    }

}