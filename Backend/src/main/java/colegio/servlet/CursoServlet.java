package colegio.servlet;

import colegio.dto.PeticionCurso;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.services.CursoService;
import colegio.services.Permisos;
import colegio.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/cursos/*")
public class CursoServlet extends HttpServlet {

    private CursoService service = new CursoService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        try {
            String ruta = request.getPathInfo();

            if (ruta == null || ruta.equals("/")) {
                Json.escribir(response, 200, service.listar());
                return;
            }

            int id = obtenerId(ruta);
            Json.escribir(response, 200, service.buscarPorId(id));

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (SQLException e) {
            Json.error(response, 500, "Error al consultar los cursos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        String ruta = request.getPathInfo();

        if (ruta != null && !ruta.equals("/")) {
            Json.error(response, 404, "Ruta no encontrada");
            return;
        }

        try {
            PeticionCurso datos = gson.fromJson(
                    request.getReader(), PeticionCurso.class
            );

            service.crear(datos);

            Json.escribir(response, 201, Map.of(
                    "mensaje", "Curso creado correctamente"
            ));

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(response, 400, "Datos del curso invalidos: " + e.getMessage());
        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(response, 500, "Error al crear el curso");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        try {
            String ruta = request.getPathInfo();

            if (ruta == null) {
                throw new DatosInvalidosException("Indica el curso");
            }

            if (ruta.endsWith("/estado")) {
                int id = obtenerId(ruta.substring(0, ruta.length() - 7));

                EstadoCurso datos = gson.fromJson(
                        request.getReader(), EstadoCurso.class
                );

                if (datos == null || datos.activo == null) {
                    throw new DatosInvalidosException("Indica el estado del curso");
                }

                service.cambiarEstado(id, datos.activo);

                Json.escribir(response, 200, Map.of(
                        "mensaje", "Estado actualizado correctamente"
                ));
                return;
            }

            int id = obtenerId(ruta);

            PeticionCurso datos = gson.fromJson(
                    request.getReader(), PeticionCurso.class
            );

            service.editar(id, datos);

            Json.escribir(response, 200, Map.of(
                    "mensaje", "Curso actualizado correctamente"
            ));

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(response, 500, "Error al actualizar el curso");
        }
    }

    private int obtenerId(String ruta) throws DatosInvalidosException {

        if (ruta == null || !ruta.matches("/[0-9]+")) {
            throw new DatosInvalidosException("ID de curso invalido");
        }

        try {
            int id = Integer.parseInt(ruta.substring(1));

            if (id <= 0) {
                throw new DatosInvalidosException("ID de curso invalido");
            }

            return id;

        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("ID de curso invalido");
        }
    }

    private boolean verificarPermiso(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {

        String rol = (String) request.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "ADMIN")) {
            Json.error(response, 403, "No tienes permiso para administrar cursos");
            return false;
        }

        return true;
    }

    private static class EstadoCurso {
        Boolean activo;
    }
}
