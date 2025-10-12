package com.sw.tse.api;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.ValidationErrorResposeDto;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.expection.BrasilApiException;
import com.sw.tse.domain.expection.LoginInvalidoTseException;
import com.sw.tse.domain.expection.PessoaSemContratoTseException;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	
	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ApiResponseDto<?>> handleInvalidDataAccessResourceUsageException(
    		InvalidDataAccessResourceUsageException ex, WebRequest request) {
        
		ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
				HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null, ex.getMessage()                 
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> handleDataIntegrityViolationException(
    		DataIntegrityViolationException ex, WebRequest request) {
        
		String userMessage = "Ocorreu um erro de integridade dos dados. Verifique se os valores fornecidos são válidos e únicos.";
		
		log.error("Erro de integridade de dados: {}", ex.getMessage(), ex);

		ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
				HttpStatus.BAD_REQUEST.value(), false, null, userMessage                 
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
	

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleGenericException(Exception ex, WebRequest request) {
    	log.error("Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.", ex);
    	String errorMessage = "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.";
        ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null, errorMessage
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(HibernateException.class)
    public ResponseEntity<ApiResponseDto<?>> handleGenericException(HibernateException ex, WebRequest request) {
        ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null, ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(ApiTseException.class)
    public ResponseEntity<ApiResponseDto<?>> handleApiTseException(ApiTseException ex, WebRequest request){
    	ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.BAD_REQUEST.value() , false, null, ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(LoginInvalidoTseException.class)
    public ResponseEntity<ApiResponseDto<?>> handleLoginInvalidoTseException(LoginInvalidoTseException ex, WebRequest request){
    	ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.UNAUTHORIZED.value() , false, null, ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(PessoaSemContratoTseException.class)
    public ResponseEntity<ApiResponseDto<?>> handlePessoaSemContratoTseException(PessoaSemContratoTseException ex, WebRequest request){
    	ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.UNAUTHORIZED.value() , false, null, ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
     
    
    @ExceptionHandler(BrasilApiException.class)
    public ResponseEntity<ApiResponseDto<?>> handleApiTseException(BrasilApiException ex, WebRequest request){
    	ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.BAD_REQUEST.value() , false, null, ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        List<ValidationErrorResposeDto> validationErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            
        	Object rejectedValue = fieldError.getRejectedValue();

            ValidationErrorResposeDto error = new ValidationErrorResposeDto(
                    fieldError.getField(),
                    rejectedValue, 
                    fieldError.getDefaultMessage()
            );
            validationErrors.add(error);
        });
        

        ex.getBindingResult().getGlobalErrors().forEach(globalError -> {
            ValidationErrorResposeDto error = new ValidationErrorResposeDto(
                    globalError.getObjectName(),
                    null, 
                    globalError.getDefaultMessage()
            );
            validationErrors.add(error);
        });

        String detail = "Um ou mais campos estão inválidos. Verifique os detalhes e tente novamente.";
        
        ApiResponseDto<List<ValidationErrorResposeDto>> errorResponse = new ApiResponseDto<>(
            HttpStatus.BAD_REQUEST.value(),
            false,
            validationErrors,
            detail
        );

        return handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

    
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            @NonNull NoResourceFoundException ex, 
            @NonNull HttpHeaders headers, 
            @NonNull HttpStatusCode status, 
            @NonNull WebRequest request) {

        String errorMessage = String.format("O recurso '%s' que você tentou acessar não existe.", ex.getResourcePath());
        
        ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.NOT_FOUND.value(), 
                false,
                null,
                errorMessage
        );

        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }
    
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponseDto<?>> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
        String originalMessage = ex.getMessage();
        String errorMessage = "Um parâmetro numérico esperado recebeu um valor inválido. Verifique os dados enviados.";

        try {
            String invalidValue = originalMessage.substring(originalMessage.indexOf("\"") + 1, originalMessage.lastIndexOf("\""));
            errorMessage = String.format("O valor '%s' não é um número válido para um dos parâmetros da requisição.", invalidValue);
        } catch (Exception e) {

        }
        
        log.warn("Falha de conversão de número: {}", errorMessage);

        ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
                HttpStatus.BAD_REQUEST.value(), 
                false,
                null,
                errorMessage
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(TokenJwtInvalidoException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleTokenJwtInvalidoException(TokenJwtInvalidoException ex) {
        log.warn("Token JWT inválido ou dados ausentes: {}", ex.getMessage());
        
        ApiResponseDto<Object> response = new ApiResponseDto<>(
            HttpStatus.UNAUTHORIZED.value(),
            false,
            null,
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}