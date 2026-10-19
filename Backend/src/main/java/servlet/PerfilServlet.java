package servlet;

import util.Auth;
import util.Json;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/me")
public class PerfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json.escribir(resp, 200, Map.of(
                "id", Auth.idUsuario(req),
                "username", Auth.username(req),
                "rol", Auth.rol(req)));
    }
}