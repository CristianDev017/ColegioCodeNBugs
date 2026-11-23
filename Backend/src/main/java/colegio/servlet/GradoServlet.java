package colegio.servlet;

import colegio.dto.PeticionGrado;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.services.GradoService;
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

@WebServlet("/api/grados/*")
public class GradoServlet extends HttpServlet {

    private GradoService service = new GradoService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (!verificarPermiso(req, resp, "ADMIN", "SUPER_ADMIN")) {
            return;
        }

        try {
            String ruta = req.getPathInfo();

            if (ruta == null || ruta.equals("/")) {
                Json.escribir(resp, 200, service.listar());
                return;
            }

            int id = obtenerId(ruta);
            Json.escribir(resp, 200, service.buscarPorId(id));

        } catch (DatosInvalidosException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(resp, 404, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500, "Error al consultar los grados");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (!verificarPermiso(req, resp, "SUPER_ADMIN")) {
            return;
        }

        String ruta = req.getPathInfo();

        if (ruta != null && !ruta.equals("/")) {
            Json.error(resp, 404, "Ruta no encontrada");
            return;
        }

        try {
            PeticionGrado datos = gson.fromJson(req.getReader(), PeticionGrado.class);

            service.crear(datos);

            Json.escribir(resp, 201, Map.of(
                    "mensaje", "Grado creado correctamente"
            ));

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(resp, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500, "Error al crear el grado");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (!verificarPermiso(req, resp, "SUPER_ADMIN")) {
            return;
        }

        try {
            String ruta = req.getPathInfo();

            if (ruta == null) {
                throw new DatosInvalidosException("Indica el grado");
            }

            if (ruta.endsWith("/estado")) {
                int id = obtenerId(ruta.substring(0, ruta.length() - 7));

                EstadoGrado datos = gson.fromJson(req.getReader(), EstadoGrado.class);

                if (datos == null || datos.activo == null) {
                    throw new DatosInvalidosException("Indica el estado del grado");
                }

                service.cambiarEstado(id, datos.activo);

                Json.escribir(resp, 200, Map.of(
                        "mensaje", "Estado actualizado correctamente"
                ));
                return;
            }

            int id = obtenerId(ruta);
            PeticionGrado datos = gson.fromJson(req.getReader(), PeticionGrado.class);

            service.editar(id, datos);

            Json.escribir(resp, 200, Map.of(
                    "mensaje", "Grado actualizado correctamente"
            ));

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(resp, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(resp, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500, "Error al actualizar el grado");
        }
    }

    private int obtenerId(String ruta) throws DatosInvalidosException {

        if (ruta == null || !ruta.startsWith("/")) {
            throw new DatosInvalidosException("ID de grado invalido");
        }

        String numero = ruta.substring(1);

        if (numero.isEmpty() || numero.contains("/")) {
            throw new DatosInvalidosException("ID de grado invalido");
        }

        try {
            int id = Integer.parseInt(numero);

            if (id <= 0) {
                throw new DatosInvalidosException("ID de grado invalido");
            }

            return id;

        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("ID de grado invalido");
        }
    }

    private boolean verificarPermiso(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     String... rolesPermitidos) throws IOException {

        String rol = (String) req.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, rolesPermitidos)) {
            Json.error(resp, 403, "No tienes permiso para administrar grados");
            return false;
        }

        return true;
    }

    private static class EstadoGrado {
        Boolean activo;
    }
}
