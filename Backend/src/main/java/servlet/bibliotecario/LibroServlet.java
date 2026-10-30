package servlet.bibliotecario;

import dto.biblioteca.PeticionLibro;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.biblioteca.LibroService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/libros/*")
public class LibroServlet extends HttpServlet {

    private final LibroService servicio = new LibroService();

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
            PeticionLibro datos = Json.leer(req, PeticionLibro.class);
            Json.escribir(resp, 201, servicio.crear(datos));
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
            if (partes.length < 2) {
                throw new DatosInvalidosException("Falta el id del libro");
            }
            int id = leerId(partes[1]);
            if (partes.length == 2) {
                PeticionLibro datos = Json.leer(req, PeticionLibro.class);
                Json.escribir(resp, 200, servicio.editar(id, datos));
            } else if (partes.length == 3 && partes[2].equals("activar")) {
                Json.escribir(resp, 200, servicio.cambiarEstado(id, true));
            } else if (partes.length == 3 && partes[2].equals("desactivar")) {
                Json.escribir(resp, 200, servicio.cambiarEstado(id, false));
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