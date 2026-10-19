package util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PasswordService {

    public static String encriptar(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean coincide(String passwordPlano, String passwordGuardada) {
        return encriptar(passwordPlano).equals(passwordGuardada);
    }
}