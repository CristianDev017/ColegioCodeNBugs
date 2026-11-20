
package colegio.servlet;

import colegio.model.Usuario;
import colegio.services.AuthService;
import colegio.services.JwtTokenService;
import colegio.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/auth/login")
public class AuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Gson gson = new Gson();
        JsonObject datos;

        try {
            datos = gson.fromJson(request.getReader(), JsonObject.class);

        } catch (JsonParseException | IllegalStateException e) {
            Json.error(response, 400, "JSON inválido");
            return;
        }

        if (datos == null || !datos.has("usuario") || !datos.has("password")
                || datos.get("usuario").isJsonNull()
                || datos.get("password").isJsonNull()
                || !datos.get("usuario").isJsonPrimitive()
                || !datos.get("password").isJsonPrimitive()
                || !datos.get("usuario").getAsJsonPrimitive().isString()
                || !datos.get("password").getAsJsonPrimitive().isString()) {

            Json.error(response, 400, "Faltan datos obligatorios");
            return;
        }

        String usuario = datos.get("usuario").getAsString();
        String password = datos.get("password").getAsString();

        if (usuario.isBlank() || password.isBlank()) {
            Json.error(response, 400, "El usuario y la contraseña son obligatorios");
            return;
        }

        AuthService auth = new AuthService();

        try {
            Usuario resultado = auth.login(usuario, password);

            if (resultado == null) {
                Json.error(response, 401, "Credenciales incorrectas");

            } else {
                JwtTokenService jwtService = new JwtTokenService();

                String token = jwtService.generate(resultado);

                Json.escribir(response, 200, Map.of(
                        "mensaje", "Login exitoso",
                        "token", token,
                        "rol", resultado.getRol()
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar la base de datos");
            e.printStackTrace();
            Json.error(response, 500, "Error interno del servidor");
        }
    }
}
