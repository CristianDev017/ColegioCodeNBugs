package colegio.servlet;

import colegio.model.Usuario;
import colegio.services.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import com.google.gson.JsonParseException;
import colegio.services.JwtTokenService;

@WebServlet("/api/auth/login")
public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        JsonObject datos;

        try {
            datos = gson.fromJson(request.getReader(), JsonObject.class);
        } catch (JsonParseException | IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"mensaje\":\"JSON inválido\"}");
            return;
        }

        if (datos == null || !datos.has("usuario") || !datos.has("password")
                || datos.get("usuario").isJsonNull()
                || datos.get("password").isJsonNull()
                || !datos.get("usuario").isJsonPrimitive()
                || !datos.get("password").isJsonPrimitive()
                || !datos.get("usuario").getAsJsonPrimitive().isString()
                || !datos.get("password").getAsJsonPrimitive().isString())
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"mensaje\":\"Faltan datos obligatorios\"}");
            return;
        }

        String usuario = datos.get("usuario").getAsString();
        String password = datos.get("password").getAsString();

        if (usuario.isBlank() || password.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"mensaje\":\"El usuario y la contraseña son obligatorios \"}");
            return;
        }

        AuthService auth = new AuthService();

        try {
            Usuario resultado = auth.login(usuario, password);

            if (resultado == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"mensaje\":\"Credenciales incorrectas\"}");
            } else {
                JwtTokenService jwtService = new JwtTokenService();

                String token = jwtService.generate(resultado);

                JsonObject respuesta = new JsonObject();
                respuesta.addProperty("mensaje", "Login exitoso");
                respuesta.addProperty("token", token);
                respuesta.addProperty("rol", resultado.getRol());

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(respuesta));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar la base de datos");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"mensaje\":\"Error interno del servidor\"}");
        }

    }
}
