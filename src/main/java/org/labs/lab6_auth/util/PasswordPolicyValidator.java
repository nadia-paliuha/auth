package org.labs.lab6_auth.util;

public class PasswordPolicyValidator {
    public static boolean validate(String pwd) {
        if (pwd == null || pwd.length() < 8) return false;
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSymbol = false;
        for (char c: pwd.toCharArray()){
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSymbol = true;
        }
        return hasLower && hasUpper && hasDigit && hasSymbol;
    }
}
