package servlet;

import dto.academico.PeticionSeccion;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.academico.SeccionService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/secciones/*")
public class SeccionServlet extends HttpServlet {

    private final SeccionService servicio = new SeccionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN, Roles.SECRETARIA)) {
            return;
        }
        int idGrado = numero(req.getParameter("idGrado"), 0);
        int pagina = Math.max(1, numero(req.getParameter("pagina"), 1));
        int tamano = Math.min(Math.max(1, numero(req.getParameter("tamano"), 10)), 50);

        try {
            Json.escribir(resp, 200, Map.of(
                    "datos", servicio.listar(idGrado, pagina, tamano),
                    "total", servicio.contar(idGrado),
                    "pagina", pagina,
                    "tamano", tamano));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN)) {
            return;
        }
        try {
            PeticionSeccion datos = Json.leer(req, PeticionSeccion.class);
            Json.escribir(resp, 201, servicio.crear(datos));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN)) {
            return;
        }
        try {
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length < 2) {
                throw new DatosInvalidosException("Falta el id de la sección");
            }
            int id = leerId(partes[1]);

            if (partes.length == 2) {
                PeticionSeccion datos = Json.leer(req, PeticionSeccion.class);
                Json.escribir(resp, 200, servicio.editar(id, datos));
            } else if (partes.length == 3 && partes[2].equals("activar")) {
                Json.escribir(resp, 200, servicio.activar(id));
            } else if (partes.length == 3 && partes[2].equals("desactivar")) {
                Json.escribir(resp, 200, servicio.desactivar(id));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private int leerId(String texto) throws DatosInvalidosException {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("El id no es válido");
        }
    }

    private int numero(String texto, int porDefecto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}