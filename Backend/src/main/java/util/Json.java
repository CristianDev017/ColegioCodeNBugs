package util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class Json {

    private static final Gson gson = new Gson();

    public static <T> T leer(HttpServletRequest req, Class<T> clase) throws IOException {
        req.setCharacterEncoding("UTF-8");
        return gson.fromJson(req.getReader(), clase);
    }

    public static void escribir(HttpServletResponse resp, int estado, Object objeto) throws IOException {
        resp.setStatus(estado);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(objeto));
    }

    public static void error(HttpServletResponse resp, int estado, String mensaje) throws IOException {
        escribir(resp, estado, Map.of("mensaje", mensaje));
    }
}