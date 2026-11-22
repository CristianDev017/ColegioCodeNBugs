
package colegio.util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class Json {

    private static final Gson gson = new Gson();

    public static void escribir(HttpServletResponse response, int estado, Object datos) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(datos));
    }

    public static void error(HttpServletResponse response, int estado, String mensaje) throws IOException {
        escribir(response, estado, Map.of("mensaje", mensaje));
    }
}
