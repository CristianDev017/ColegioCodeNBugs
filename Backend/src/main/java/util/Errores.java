package util;

import com.google.gson.JsonParseException;
import exception.ServicioException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Errores {

    public static void responder(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof ServicioException) {
            ServicioException s = (ServicioException) e;
            Json.error(resp, s.getEstado(), s.getMessage());
        } else if (e instanceof JsonParseException) {
            Json.error(resp, 400, "Los datos enviados no son válidos");
        } else {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }
}