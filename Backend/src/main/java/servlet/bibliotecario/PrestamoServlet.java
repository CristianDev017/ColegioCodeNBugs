package servlet.bibliotecario;

import dto.biblioteca.PeticionDevolucion;
import dto.biblioteca.PeticionPrestamo;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.biblioteca.PrestamoService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/prestamos/*")
public class PrestamoServlet extends HttpServlet {

    private final PrestamoService servicio = new PrestamoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.BIBLIOTECARIO)) {
            return;
        }
        try {
            String ruta = req.getPathInfo();
            if (ruta == null || ruta.equals("/")) {
                String buscar = texto(req.getParameter("buscar"));
                String estado = texto(req.getParameter("estado"));
                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listar(buscar, estado, pagina, tamano),
                        "total", servicio.contar(buscar, estado),
                        "pagina", pagina,
                        "tamano", tamano));
                return;
            }
            String[] partes = ruta.split("/");
            if (partes.length == 2) {
                Json.escribir(resp, 200, servicio.obtener(leerId(partes[1])));
            } else if (partes.length == 3 && partes[1].equals("opciones") && partes[2].equals("libros")) {
                Json.escribir(resp, 200, servicio.opcionesLibros(texto(req.getParameter("buscar"))));
            } else if (partes.length == 3 && partes[1].equals("opciones") && partes[2].equals("solicitantes")) {
                Json.escribir(resp, 200, servicio.opcionesSolicitantes(
                        texto(req.getParameter("tipo")), texto(req.getParameter("buscar"))));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.BIBLIOTECARIO)) {
            return;
        }
        try {
            PeticionPrestamo datos = Json.leer(req, PeticionPrestamo.class);
            Json.escribir(resp, 201, servicio.registrar(datos, Auth.idUsuario(req)));
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
            String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
            if (partes.length == 3 && partes[2].equals("devolver")) {
                PeticionDevolucion datos = Json.leer(req, PeticionDevolucion.class);
                Json.escribir(resp, 200, servicio.devolver(leerId(partes[1]), datos));
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
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
