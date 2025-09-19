package com.sw.tse.core.util;

import java.util.regex.Pattern;


public class CpfUtil {

    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}");
    

    private static final String[] INVALID_CPFS = {
        "00000000000", "11111111111", "22222222222", "33333333333",
        "44444444444", "55555555555", "66666666666", "77777777777",
        "88888888888", "99999999999"
    };


    public static boolean isValid(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        

        String cleanCpf = cpf.replaceAll("[^\\d]", "");
        

        if (!CPF_PATTERN.matcher(cleanCpf).matches()) {
            return false;
        }
        

        for (String invalidCpf : INVALID_CPFS) {
            if (cleanCpf.equals(invalidCpf)) {
                return false;
            }
        }
        

        return validateDigits(cleanCpf);
    }
    

    private static boolean validateDigits(String cpf) {

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }
        

        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }
        

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }
        

        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }
    

    public static String clean(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^\\d]", "");
    }
    

    public static String format(String cpf) {
        if (cpf == null) {
            return null;
        }
        
        String cleanCpf = clean(cpf);
        if (cleanCpf.length() != 11) {
            return null;
        }
        
        return cleanCpf.substring(0, 3) + "." +
               cleanCpf.substring(3, 6) + "." +
               cleanCpf.substring(6, 9) + "-" +
               cleanCpf.substring(9, 11);
    }
}