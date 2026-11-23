
package colegio.servlet;

import colegio.dto.PeticionEditarUsuario;
import colegio.dto.PeticionUsuario;
import colegio.dto.RespuestaUsuario;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.services.Permisos;
import colegio.services.UsuarioService;
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
import java.util.List;
import java.util.Map;

@WebServlet("/api/usuarios/*")
public class UsuarioServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final Gson gson = new Gson();

    private boolean verificarPermiso(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String rol = (String) request.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "SUPER_ADMIN")) {
            Json.error(response, 403, "No tiene permiso para esta accion");
            return false;
        }

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        String ruta = request.getPathInfo();

        try {
            if (ruta == null || ruta.equals("/")) {
                int pagina = leerParametro(request, "pagina", 1);
                int tamano = leerParametro(request, "tamano", 10);
                if (pagina < 1 || tamano < 1 || tamano > 50) {
                    throw new DatosInvalidosException("Pagina o tamaño invalido");
                }
                List<RespuestaUsuario> usuarios = service.listar(pagina, tamano);
                Json.escribir(response, 200, Map.of(
                        "datos", usuarios, "total", service.contar(),
                        "pagina", pagina, "tamano", tamano));
                return;
            }

            if (!ruta.matches("/\\d+")) {
                Json.error(response, 404, "Ruta no encontrada");
                return;
            }

            int idUsuario = Integer.parseInt(ruta.substring(1));
            RespuestaUsuario usuario = service.buscarPorId(idUsuario);

            Json.escribir(response, 200, usuario);

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());

        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());

        } catch (NumberFormatException e) {
            Json.error(response, 400, "Id de usuario invalido");

        } catch (SQLException e) {
            Json.error(response, 500, "Error al consultar usuarios");
        }
    }

    private int leerParametro(HttpServletRequest request, String nombre, int defecto)
            throws DatosInvalidosException {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.isBlank()) return defecto;
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("Parametro invalido: " + nombre);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        String ruta = request.getPathInfo();

        if (ruta != null && !ruta.equals("/")) {
            Json.error(response, 404, "Ruta no encontrada");
            return;
        }

        try {
            PeticionUsuario datos = gson.fromJson(request.getReader(), PeticionUsuario.class);

            if (datos == null) {
                Json.error(response, 400, "Debe enviar los datos del usuario");
                return;
            }

            service.crear(datos);

            Json.escribir(response, 201, Map.of("mensaje", "Usuario creado correctamente"));

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());

        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());

        } catch (JsonParseException e) {
            Json.error(response, 400, "JSON invalido");

        } catch (SQLException e) {
            Json.error(response, 500, "Error al crear usuario");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        String ruta = request.getPathInfo();

        if (ruta == null) {
            Json.error(response, 404, "Ruta no encontrada");
            return;
        }

        try {
            if (ruta.matches("/\\d+/estado")) {

                int idUsuario = Integer.parseInt(ruta.split("/")[1]);
                JsonObject datos = gson.fromJson(request.getReader(), JsonObject.class);

                if (datos == null || !datos.has("estado")
                        || !datos.get("estado").isJsonPrimitive()
                        || !datos.get("estado").getAsJsonPrimitive().isString()) {
                    Json.error(response, 400, "Debe enviar el estado del usuario");
                    return;
                }

                String estado = datos.get("estado").getAsString();

                service.cambiarEstado(idUsuario, estado);

                Json.escribir(response, 200, Map.of("mensaje", "Estado actualizado correctamente"));
                return;
            }

            if (ruta.matches("/\\d+")) {

                int idUsuario = Integer.parseInt(ruta.substring(1));
                PeticionEditarUsuario datos = gson.fromJson(request.getReader(), PeticionEditarUsuario.class);

                if (datos == null) {
                    Json.error(response, 400, "Debe enviar los datos del usuario");
                    return;
                }

                service.editar(idUsuario, datos);

                Json.escribir(response, 200, Map.of("mensaje", "Usuario actualizado correctamente"));
                return;
            }

            Json.error(response, 404, "Ruta no encontrada");

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());

        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());

        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());

        } catch (JsonParseException | IllegalStateException | NumberFormatException e) {
            Json.error(response, 400, "Datos invalidos");

        } catch (SQLException e) {
            Json.error(response, 500, "Error al actualizar usuario");
        }
    }
}
