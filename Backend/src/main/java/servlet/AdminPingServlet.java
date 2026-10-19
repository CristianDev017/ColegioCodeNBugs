package servlet;

import util.Auth;
import util.Json;
import util.Roles;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/ping")
public class AdminPingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN)) {
            return;
        }
        Json.escribir(resp, 200, Map.of("mensaje", "Acceso de administración correcto"));
    }
}