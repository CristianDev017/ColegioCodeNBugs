
package colegio.servlet;

import colegio.dto.PeticionSeccion;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.services.Permisos;
import colegio.services.SeccionService;
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

@WebServlet("/api/secciones/*")
public class SeccionServlet extends HttpServlet {

    private final SeccionService service = new SeccionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        try {
            String ruta = request.getPathInfo();

            if (ruta == null || ruta.equals("/")) {

                int pagina = leerParametro(request, "pagina", 1);
                int tamano = leerParametro(request, "tamano", 10);

                if (pagina < 1 || tamano < 1 || tamano > 50) {
                    throw new DatosInvalidosException(
                            "Pagina o tamaño invalido"
                    );
                }

                int total = service.contar();

                Json.escribir(response, 200, Map.of(
                        "datos", service.listar(pagina, tamano),
                        "total", total,
                        "pagina", pagina,
                        "tamano", tamano
                ));
                return;
            }

            if (ruta.equals("/opciones")) {
                Json.escribir(response, 200, service.listarGradosDisponibles());
                return;
            }

            if (ruta.equals("/catalogo")) {
                Json.escribir(response, 200, service.listarCatalogo());
                return;
            }

            int id = obtenerId(ruta);

            Json.escribir(response, 200, service.buscarPorId(id));

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500, "Error al consultar las secciones");
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
            PeticionSeccion datos = gson.fromJson(
                    request.getReader(), PeticionSeccion.class
            );

            service.crear(datos);

            Json.escribir(response, 201, Map.of(
                    "mensaje", "Seccion creada correctamente"
            ));

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(response, 400, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500, "Error al crear la seccion");
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
                throw new DatosInvalidosException("Indica la seccion");
            }

            if (ruta.endsWith("/estado")) {

                int id = obtenerId(ruta.substring(0, ruta.length() - 7));

                EstadoSeccion datos = gson.fromJson(
                        request.getReader(), EstadoSeccion.class
                );

                if (datos == null || datos.activo == null) {
                    throw new DatosInvalidosException(
                            "Indica el estado de la seccion"
                    );
                }

                service.cambiarEstado(id, datos.activo);

                Json.escribir(response, 200, Map.of(
                        "mensaje", "Estado actualizado correctamente"
                ));
                return;
            }

            int id = obtenerId(ruta);

            PeticionSeccion datos = gson.fromJson(
                    request.getReader(), PeticionSeccion.class
            );

            service.editar(id, datos);

            Json.escribir(response, 200, Map.of(
                    "mensaje", "Seccion actualizada correctamente"
            ));

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500, "Error al actualizar la seccion");
        }
    }

    private int leerParametro(HttpServletRequest request, String nombre,
                              int defecto) throws DatosInvalidosException {

        String valor = request.getParameter(nombre);

        if (valor == null || valor.isBlank()) {
            return defecto;
        }

        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(
                    "Parametro invalido: " + nombre
            );
        }
    }

    private int obtenerId(String ruta) throws DatosInvalidosException {

        if (ruta == null || !ruta.startsWith("/")) {
            throw new DatosInvalidosException("ID de seccion invalido");
        }

        String numero = ruta.substring(1);

        if (numero.isEmpty() || numero.contains("/")) {
            throw new DatosInvalidosException("ID de seccion invalido");
        }

        try {
            int id = Integer.parseInt(numero);

            if (id <= 0) {
                throw new DatosInvalidosException("ID de seccion invalido");
            }

            return id;

        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("ID de seccion invalido");
        }
    }

    private boolean verificarPermiso(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {

        String rol = (String) request.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "ADMIN")) {
            Json.error(response, 403,
                    "No tienes permiso para administrar secciones");
            return false;
        }

        return true;
    }

    private static class EstadoSeccion {
        Boolean activo;
    }
}
