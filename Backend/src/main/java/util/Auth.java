package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

public class Auth {

    public static int idUsuario(HttpServletRequest req) {
        return (Integer) req.getAttribute("usuarioId");
    }

    public static String username(HttpServletRequest req) {
        return (String) req.getAttribute("username");
    }

    public static String rol(HttpServletRequest req) {
        return (String) req.getAttribute("rol");
    }

    public static boolean exigirRol(HttpServletRequest req, HttpServletResponse resp, String... roles)
            throws IOException {
        if (Arrays.asList(roles).contains(rol(req))) {
            return true;
        }
        Json.error(resp, 403, "No tiene permiso para esta acción");
        return false;
    }
}