package servlet.admin;

import dto.finanzas.PeticionTarifa;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.finanzas.TarifaService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/tarifas/*")
public class TarifaServlet extends HttpServlet {

    private final TarifaService servicio = new TarifaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN, Roles.SECRETARIA)) {
            return;
        }
        try {
            String ruta = req.getPathInfo();
            if (ruta == null || ruta.equals("/")) {
                int idCiclo = numero(req.getParameter("idCiclo"), 0);
                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listar(idCiclo, pagina, tamano),
                        "total", servicio.contar(idCiclo),
                        "pagina", pagina,
                        "tamano", tamano));
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
            PeticionTarifa datos = Json.leer(req, PeticionTarifa.class);
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
                throw new DatosInvalidosException("Falta el id de la tarifa");
            }
            PeticionTarifa datos = Json.leer(req, PeticionTarifa.class);
            Json.escribir(resp, 200, servicio.editar(leerId(partes[1]), datos));
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