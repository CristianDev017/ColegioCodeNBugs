package servlet;

import dao.CicloEscolarDao;
import model.CicloEscolar;
import util.Auth;
import util.Json;
import util.Roles;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/ciclos/*")
public class CicloEscolarServlet extends HttpServlet {

    private final CicloEscolarDao dao = new CicloEscolarDao();

    private static class PeticionCiclo {
        Integer anio;
        String fechaInicio;
        String fechaFin;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN, Roles.ADMIN, Roles.SECRETARIA)) {
            return;
        }
        int pagina = numero(req.getParameter("pagina"), 1);
        int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);

        try {
            List<CicloEscolar> datos = dao.listar(pagina, tamano);
            Json.escribir(resp, 200, Map.of("datos", datos, "total", dao.contar(), "pagina", pagina, "tamano", tamano));
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN)) {
            return;
        }
        PeticionCiclo datos = Json.leer(req, PeticionCiclo.class);
        String error = validar(datos);
        if (error != null) {
            Json.error(resp, 400, error);
            return;
        }

        try {
            if (dao.existeAnio(datos.anio, 0)) {
                Json.error(resp, 409, "Ya existe un ciclo para ese año");
                return;
            }
            int id = dao.crear(datos.anio, datos.fechaInicio, datos.fechaFin);
            Json.escribir(resp, 201, dao.buscarPorId(id));
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN)) {
            return;
        }
        String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
        if (partes.length < 2) {
            Json.error(resp, 400, "Falta el id del ciclo");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            Json.error(resp, 400, "El id no es válido");
            return;
        }

        try {
            CicloEscolar actual = dao.buscarPorId(id);
            if (actual == null) {
                Json.error(resp, 404, "El ciclo no existe");
                return;
            }

            if (partes.length == 3 && partes[2].equals("activar")) {
                activar(resp, actual);
            } else if (partes.length == 3 && partes[2].equals("cerrar")) {
                cerrar(resp, actual);
            } else if (partes.length == 2) {
                editar(req, resp, actual);
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }

    private void activar(HttpServletResponse resp, CicloEscolar actual) throws Exception {
        if (!"PLANIFICADO".equals(actual.getEstado())) {
            Json.error(resp, 400, "Solo se puede activar un ciclo planificado");
            return;
        }
        if (dao.contarActivos() > 0) {
            Json.error(resp, 409, "Ya existe un ciclo activo. Ciérrelo antes de activar otro");
            return;
        }
        dao.cambiarEstado(actual.getId(), "ACTIVO");
        Json.escribir(resp, 200, dao.buscarPorId(actual.getId()));
    }

    private void cerrar(HttpServletResponse resp, CicloEscolar actual) throws Exception {
        if (!"ACTIVO".equals(actual.getEstado())) {
            Json.error(resp, 400, "Solo se puede cerrar el ciclo activo");
            return;
        }
        dao.cambiarEstado(actual.getId(), "CERRADO");
        Json.escribir(resp, 200, dao.buscarPorId(actual.getId()));
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp, CicloEscolar actual) throws Exception {
        if (!"PLANIFICADO".equals(actual.getEstado())) {
            Json.error(resp, 400, "Solo se puede editar un ciclo planificado");
            return;
        }
        PeticionCiclo datos = Json.leer(req, PeticionCiclo.class);
        String error = validar(datos);
        if (error != null) {
            Json.error(resp, 400, error);
            return;
        }
        if (dao.existeAnio(datos.anio, actual.getId())) {
            Json.error(resp, 409, "Ya existe un ciclo para ese año");
            return;
        }
        dao.actualizar(actual.getId(), datos.anio, datos.fechaInicio, datos.fechaFin);
        Json.escribir(resp, 200, dao.buscarPorId(actual.getId()));
    }

    private String validar(PeticionCiclo d) {
        if (d == null || d.anio == null || d.fechaInicio == null || d.fechaFin == null) {
            return "Faltan datos del ciclo";
        }
        if (d.anio < 2000 || d.anio > 2100) {
            return "El año no es válido";
        }
        try {
            LocalDate inicio = LocalDate.parse(d.fechaInicio);
            LocalDate fin = LocalDate.parse(d.fechaFin);
            if (!fin.isAfter(inicio)) {
                return "La fecha de fin debe ser posterior a la de inicio";
            }
        } catch (DateTimeParseException e) {
            return "Las fechas deben tener el formato AAAA-MM-DD";
        }
        return null;
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