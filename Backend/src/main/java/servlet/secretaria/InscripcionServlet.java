package servlet.secretaria;

import dto.estudiantes.PeticionInscripcion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.estudiantes.InscripcionService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/inscripciones/*")
public class InscripcionServlet extends HttpServlet {

    private final InscripcionService servicio = new InscripcionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SECRETARIA, Roles.ADMIN)) {
            return;
        }
        try {
            String ruta = req.getPathInfo();
            if ("/secciones".equals(ruta)) {
                Json.escribir(resp, 200, servicio.seccionesDisponibles());
                return;
            }
            if (ruta == null || ruta.equals("/")) {
                String buscar = texto(req.getParameter("buscar"));
                int idSeccion = numero(req.getParameter("idSeccion"), 0);
                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listar(buscar, idSeccion, pagina, tamano),
                        "total", servicio.contar(buscar, idSeccion),
                        "pagina", pagina,
                        "tamano", tamano));
                return;
            }
            Json.error(resp, 404, "Ruta no encontrada");
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SECRETARIA)) {
            return;
        }
        try {
            PeticionInscripcion datos = Json.leer(req, PeticionInscripcion.class);
            Json.escribir(resp, 201, servicio.inscribir(datos, Auth.idUsuario(req)));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
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