package servlet.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.reportes.OpcionReporteService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;

@WebServlet("/api/reportes/opciones/*")
public class ReporteOpcionesServlet extends HttpServlet {

    private final OpcionReporteService servicio = new OpcionReporteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN, Roles.SECRETARIA)) {
            return;
        }
        try {
            String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
            if (ruta.equals("/ciclos")) {
                Json.escribir(resp, 200, servicio.ciclos());
            } else if (ruta.equals("/grados")) {
                Json.escribir(resp, 200, servicio.grados());
            } else if (ruta.equals("/secciones")) {
                Json.escribir(resp, 200, servicio.secciones(entero(req.getParameter("idGrado"))));
            } else if (ruta.equals("/estudiantes")) {
                Json.escribir(resp, 200, servicio.estudiantes());
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private int entero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}