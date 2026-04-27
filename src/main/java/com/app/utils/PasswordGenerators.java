package com.app.utils;

import java.util.Random;

public class PasswordGenerators {

    private PasswordGenerators(){}

    public static String generateRandomPassword(int length) {
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()_+[]'/.,><~/*-+";
    
        String allChars = uppercaseLetters + lowercaseLetters + digits + symbols;
        Random random = new Random();
    
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }
    
        return password.toString();
    }

}
