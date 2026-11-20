package colegio.servlet;

import colegio.services.NivelService;
import colegio.services.Permisos;
import colegio.util.Json;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/niveles")
public class NivelServlet extends HttpServlet {

    private NivelService service = new NivelService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String rol = (String) req.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "SUPER_ADMIN")) {
            Json.error(resp, 403, "No tienes permiso para consultar niveles");
            return;
        }

        try {
            Json.escribir(resp, 200, service.listar());
        } catch (SQLException e) {
            Json.error(resp, 500, "No se pudieron consultar los niveles");
        }
    }
}
