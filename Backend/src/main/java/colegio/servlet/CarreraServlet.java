
package colegio.servlet;

import colegio.dto.PeticionCarrera;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Carrera;
import colegio.services.CarreraService;
import colegio.services.Permisos;
import colegio.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/carreras/*")
public class CarreraServlet extends HttpServlet {

    private final CarreraService service = new CarreraService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String ruta = request.getPathInfo();

        try {
            if (ruta == null || ruta.equals("/")) {
                int pagina = leerParametro(request, "pagina", 1);
                int tamano = leerParametro(request, "tamano", 10);

                if (pagina < 1 || tamano < 1 || tamano > 50) {
                    throw new DatosInvalidosException(
                            "Pagina o tamaño invalido"
                    );
                }

                Json.escribir(response, 200, Map.of(
                        "datos", service.listar(pagina, tamano),
                        "total", service.contar(),
                        "pagina", pagina,
                        "tamano", tamano
                ));
                return;
            }

            if (ruta.equals("/catalogo")) {
                Json.escribir(response, 200, service.listar());
                return;
            }

            if (!ruta.matches("/\\d+")) {
                Json.error(response, 404, "Ruta no encontrada");
                return;
            }

            int idCarrera = Integer.parseInt(ruta.substring(1));
            Carrera carrera = service.buscarPorId(idCarrera);

            Json.escribir(response, 200, carrera);

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());

        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());

        } catch (NumberFormatException e) {
            Json.error(response, 400, "Id de carrera invalido");

        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500, "Error al consultar carreras");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String rol = (String) request.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "SUPER_ADMIN")) {
            Json.error(response, 403, "No tiene permiso para esta accion");
            return;
        }

        String ruta = request.getPathInfo();

        if (ruta != null && !ruta.equals("/")) {
            Json.error(response, 404, "Ruta no encontrada");
            return;
        }

        try {
            PeticionCarrera datos = gson.fromJson(
                    request.getReader(), PeticionCarrera.class
            );

            if (datos == null) {
                Json.error(response, 400,
                        "Debe enviar los datos de la carrera");
                return;
            }

            service.crear(datos);

            Json.escribir(response, 201,
                    Map.of("mensaje", "Carrera creada correctamente"));

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());

        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());

        } catch (JsonParseException e) {
            Json.error(response, 400, "JSON invalido");

        } catch (SQLException e) {
            Json.error(response, 500, "Error al crear carrera");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String rol = (String) request.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "SUPER_ADMIN")) {
            Json.error(response, 403, "No tiene permiso para esta accion");
            return;
        }

        String ruta = request.getPathInfo();

        if (ruta == null) {
            Json.error(response, 404, "Ruta no encontrada");
            return;
        }

        try {
            if (ruta.matches("/\\d+/estado")) {
                int idCarrera = Integer.parseInt(ruta.split("/")[1]);

                JsonObject datos = gson.fromJson(
                        request.getReader(), JsonObject.class
                );

                if (datos == null || !datos.has("activo")
                        || !datos.get("activo").isJsonPrimitive()
                        || !datos.get("activo").getAsJsonPrimitive().isBoolean()) {

                    Json.error(response, 400,
                            "Debe enviar el estado de la carrera");
                    return;
                }

                boolean activo = datos.get("activo").getAsBoolean();

                service.cambiarEstado(idCarrera, activo);

                Json.escribir(response, 200,
                        Map.of("mensaje", "Estado actualizado correctamente"));
                return;
            }

            if (ruta.matches("/\\d+")) {
                int idCarrera = Integer.parseInt(ruta.substring(1));

                PeticionCarrera datos = gson.fromJson(
                        request.getReader(), PeticionCarrera.class
                );

                if (datos == null) {
                    Json.error(response, 400,
                            "Debe enviar los datos de la carrera");
                    return;
                }

                service.editar(idCarrera, datos);

                Json.escribir(response, 200,
                        Map.of("mensaje", "Carrera actualizada correctamente"));
                return;
            }

            Json.error(response, 404, "Ruta no encontrada");

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());

        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());

        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());

        } catch (JsonParseException | IllegalStateException |
                 NumberFormatException e) {
            Json.error(response, 400, "Datos invalidos");

        } catch (SQLException e) {
            Json.error(response, 500, "Error al actualizar carrera");
        }
    }

    private int leerParametro(HttpServletRequest request,
                              String nombre, int defecto)
            throws DatosInvalidosException {

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
}
