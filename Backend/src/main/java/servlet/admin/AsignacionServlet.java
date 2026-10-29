package servlet.admin;

import dto.PeticionAsignacion;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AsignacionService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/asignaciones/*")
public class AsignacionServlet extends HttpServlet {

    private final AsignacionService servicio = new AsignacionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN)) {
            return;
        }
        try {
            String ruta = req.getPathInfo();
            if (ruta == null || ruta.equals("/")) {
                int idEmpleado = numero(req.getParameter("idEmpleado"), 0);
                int idSeccion = numero(req.getParameter("idSeccion"), 0);
                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listar(idEmpleado, idSeccion, pagina, tamano),
                        "total", servicio.contar(idEmpleado, idSeccion),
                        "pagina", pagina,
                        "tamano", tamano));
                return;
            }
            if (ruta.equals("/maestros")) {
                Json.escribir(resp, 200, servicio.maestros());
                return;
            }
            if (ruta.equals("/secciones")) {
                Json.escribir(resp, 200, servicio.secciones());
                return;
            }
            if (ruta.equals("/cursos")) {
                Json.escribir(resp, 200, servicio.cursosDeSeccion(numero(req.getParameter("idSeccion"), 0)));
                return;
            }
            String[] partes = ruta.split("/");
            if (partes.length == 2) {
                Json.escribir(resp, 200, servicio.obtener(leerId(partes[1])));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN)) {
            return;
        }
        try {
            PeticionAsignacion datos = Json.leer(req, PeticionAsignacion.class);
            Json.escribir(resp, 201, servicio.crear(datos));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN)) {
            return;
        }
        try {
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length != 2) {
                throw new DatosInvalidosException("Falta el id de la asignación");
            }
            PeticionAsignacion datos = Json.leer(req, PeticionAsignacion.class);
            Json.escribir(resp, 200, servicio.cambiarMaestro(leerId(partes[1]), datos));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN)) {
            return;
        }
        try {
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length != 2) {
                throw new DatosInvalidosException("Falta el id de la asignación");
            }
            servicio.eliminar(leerId(partes[1]));
            Json.escribir(resp, 200, Map.of("mensaje", "Asignación eliminada"));
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
            int valor = Integer.parseInt(texto);
            return valor < 1 ? porDefecto : valor;
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}