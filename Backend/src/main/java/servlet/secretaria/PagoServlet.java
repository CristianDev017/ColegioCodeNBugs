package servlet.secretaria;

import dto.finanzas.PeticionPago;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.finanzas.PagoService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/pagos/*")
public class PagoServlet extends HttpServlet {

    private final PagoService servicio = new PagoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SECRETARIA, Roles.ADMIN)) {
            return;
        }
        try {
            String ruta = req.getPathInfo();
            if (ruta == null || ruta.equals("/")) {
                String buscar = req.getParameter("buscar") == null ? "" : req.getParameter("buscar").trim();
                int idCiclo = numero(req.getParameter("idCiclo"), 0);
                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listar(buscar, idCiclo, pagina, tamano),
                        "total", servicio.contar(buscar, idCiclo),
                        "pagina", pagina,
                        "tamano", tamano));
                return;
            }
            if (ruta.equals("/estado-cuenta")) {
                int idEstudiante = numero(req.getParameter("idEstudiante"), 0);
                int idCiclo = numero(req.getParameter("idCiclo"), 0);
                if (idEstudiante == 0 || idCiclo == 0) {
                    throw new DatosInvalidosException("Faltan el estudiante y el año lectivo");
                }
                Json.escribir(resp, 200, servicio.estadoCuenta(idEstudiante, idCiclo));
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
        if (!Auth.exigirRol(req, resp, Roles.SECRETARIA)) {
            return;
        }
        try {
            PeticionPago datos = Json.leer(req, PeticionPago.class);
            Json.escribir(resp, 201, servicio.registrar(datos, Auth.idUsuario(req)));
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