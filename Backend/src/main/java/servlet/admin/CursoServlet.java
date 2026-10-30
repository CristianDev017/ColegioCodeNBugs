package servlet.admin;

import dto.academico.PeticionCurso;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.academico.CursoService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/cursos/*")
public class CursoServlet extends HttpServlet {

    private final CursoService servicio = new CursoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN)) {
            return;
        }
        try {
            if ("/activos".equals(req.getPathInfo())) {
                Json.escribir(resp, 200, servicio.listarActivos());
                return;
            }
            int pagina = numero(req.getParameter("pagina"), 1);
            int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
            Json.escribir(resp, 200, Map.of(
                    "datos", servicio.listar(pagina, tamano),
                    "total", servicio.contar(),
                    "pagina", pagina,
                    "tamano", tamano));
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
            PeticionCurso datos = Json.leer(req, PeticionCurso.class);
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
            if (partes.length < 2) {
                throw new DatosInvalidosException("Falta el id del curso");
            }
            int id = leerId(partes[1]);

            if (partes.length == 2) {
                PeticionCurso datos = Json.leer(req, PeticionCurso.class);
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
            int valor = Integer.parseInt(texto);
            return valor < 1 ? porDefecto : valor;
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}