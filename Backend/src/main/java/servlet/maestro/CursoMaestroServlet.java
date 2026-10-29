package servlet.maestro;

import dto.PeticionAsistencia;
import dto.PeticionPonderacion;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.MaestroService;
import service.PonderacionService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/maestro/cursos/*")
public class CursoMaestroServlet extends HttpServlet {

    private final MaestroService servicio = new MaestroService();
    private final PonderacionService ponderaciones = new PonderacionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.MAESTRO)) {
            return;
        }
        try {
            int idUsuario = Auth.idUsuario(req);
            int pagina = numero(req.getParameter("pagina"), 1);
            int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
            String ruta = req.getPathInfo();

            if (ruta == null || ruta.equals("/")) {
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listarCursos(idUsuario, pagina, tamano),
                        "total", servicio.contarCursos(idUsuario),
                        "pagina", pagina,
                        "tamano", tamano));
                return;
            }

            String[] partes = ruta.split("/");
            int idAsignacion = leerId(partes[1]);
            if (partes.length == 2) {
                Json.escribir(resp, 200, servicio.obtenerCurso(idUsuario, idAsignacion));
            } else if (partes.length == 3 && partes[2].equals("nomina")) {
                String fecha = req.getParameter("fecha");
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.nomina(idUsuario, idAsignacion, fecha, pagina, tamano),
                        "total", servicio.contarNomina(idUsuario, idAsignacion),
                        "pagina", pagina,
                        "tamano", tamano));
            } else if (partes.length == 3 && partes[2].equals("ponderaciones")) {
                Json.escribir(resp, 200, Map.of(
                        "datos", ponderaciones.listar(idUsuario, idAsignacion, pagina, tamano),
                        "total", ponderaciones.contar(idUsuario, idAsignacion),
                        "pagina", pagina,
                        "tamano", tamano,
                        "suma", ponderaciones.suma(idUsuario, idAsignacion)));
            } else if (partes.length == 4 && partes[2].equals("ponderaciones")) {
                Json.escribir(resp, 200, ponderaciones.obtener(idUsuario, idAsignacion, leerId(partes[3])));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.MAESTRO)) {
            return;
        }
        try {
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length == 3 && partes[2].equals("asistencia")) {
                PeticionAsistencia datos = Json.leer(req, PeticionAsistencia.class);
                servicio.registrarAsistencia(Auth.idUsuario(req), leerId(partes[1]), datos);
                Json.escribir(resp, 200, Map.of("mensaje", "Asistencia guardada"));
            } else if (partes.length == 3 && partes[2].equals("ponderaciones")) {
                PeticionPonderacion datos = Json.leer(req, PeticionPonderacion.class);
                Json.escribir(resp, 201, ponderaciones.crear(Auth.idUsuario(req), leerId(partes[1]), datos));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.MAESTRO)) {
            return;
        }
        try {
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length == 4 && partes[2].equals("ponderaciones")) {
                PeticionPonderacion datos = Json.leer(req, PeticionPonderacion.class);
                Json.escribir(resp, 200,
                        ponderaciones.editar(Auth.idUsuario(req), leerId(partes[1]), leerId(partes[3]), datos));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.MAESTRO)) {
            return;
        }
        try {
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length == 4 && partes[2].equals("ponderaciones")) {
                ponderaciones.eliminar(Auth.idUsuario(req), leerId(partes[1]), leerId(partes[3]));
                Json.escribir(resp, 200, Map.of("mensaje", "Ponderación eliminada"));
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