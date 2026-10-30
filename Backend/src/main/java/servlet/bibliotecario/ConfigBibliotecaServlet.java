package servlet.bibliotecario;

import dto.biblioteca.PeticionConfigBiblioteca;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.biblioteca.ConfigBibliotecaService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;

@WebServlet("/api/biblioteca-config")
public class ConfigBibliotecaServlet extends HttpServlet {

    private final ConfigBibliotecaService servicio = new ConfigBibliotecaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.BIBLIOTECARIO)) {
            return;
        }
        try {
            Json.escribir(resp, 200, servicio.obtener());
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.BIBLIOTECARIO)) {
            return;
        }
        try {
            PeticionConfigBiblioteca datos = Json.leer(req, PeticionConfigBiblioteca.class);
            Json.escribir(resp, 200, servicio.guardar(datos));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }
}
