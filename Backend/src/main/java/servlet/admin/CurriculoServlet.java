package servlet.admin;

import dto.PeticionCurriculoCurso;
import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurriculoService;
import util.Auth;
import util.Errores;
import util.Json;
import util.Roles;

import java.io.IOException;

@WebServlet("/api/curriculos/*")
public class CurriculoServlet extends HttpServlet {

    private final CurriculoService servicio = new CurriculoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.ADMIN)) {
            return;
        }
        try {
            int ciclo = leerNumero(req.getParameter("ciclo"), "Falta el año lectivo");
            int grado = leerNumero(req.getParameter("grado"), "Falta el grado");
            Json.escribir(resp, 200, servicio.consultar(ciclo, grado));
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
            if (!"/cursos".equals(req.getPathInfo())) {
                Json.error(resp, 404, "Ruta no encontrada");
                return;
            }
            PeticionCurriculoCurso datos = Json.leer(req, PeticionCurriculoCurso.class);
            Json.escribir(resp, 201, servicio.agregarCurso(datos));
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
            if (partes.length != 4 || !partes[2].equals("cursos")) {
                Json.error(resp, 404, "Ruta no encontrada");
                return;
            }
            int idCurriculo = leerNumero(partes[1], "El id del currículo no es válido");
            int idCurso = leerNumero(partes[3], "El id del curso no es válido");
            Json.escribir(resp, 200, servicio.quitarCurso(idCurriculo, idCurso));
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private int leerNumero(String texto, String mensaje) throws DatosInvalidosException {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(mensaje);
        }
    }
}