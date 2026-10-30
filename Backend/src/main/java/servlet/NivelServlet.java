package servlet;

import dao.academico.NivelDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;

@WebServlet("/api/niveles")
public class NivelServlet extends HttpServlet {

    private final NivelDao dao = new NivelDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN, Roles.SECRETARIA)) {
            return;
        }
        try {
            Json.escribir(resp, 200, dao.listar());
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }
}