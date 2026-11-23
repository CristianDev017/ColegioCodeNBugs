
package colegio.servlet;

import colegio.dto.PeticionCiclo;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.services.CicloEscolarService;
import colegio.services.Permisos;
import colegio.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/ciclos/*")
public class CicloEscolarServlet extends HttpServlet {

    private final CicloEscolarService service =
            new CicloEscolarService();

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {

        if (!verificarPermiso(req, resp)) {
            return;
        }

        try {
            String ruta = req.getPathInfo();

            if (ruta == null || ruta.equals("/")) {

                int pagina = leerParametro(req, "pagina", 1);
                int tamano = leerParametro(req, "tamano", 10);

                if (pagina < 1 || tamano < 1 || tamano > 50) {
                    throw new DatosInvalidosException(
                            "Pagina o tamaño invalido"
                    );
                }

                Json.escribir(resp, 200, Map.of(
                        "datos", service.listar(pagina, tamano),
                        "total", service.contar(),
                        "pagina", pagina,
                        "tamano", tamano
                ));
                return;
            }

            if (ruta.endsWith("/grados")) {
                int id = obtenerId(
                        ruta.substring(0, ruta.length() - 7)
                );

                Json.escribir(
                        resp, 200, service.listarGrados(id)
                );
                return;
            }

            int id = obtenerId(ruta);

            Json.escribir(
                    resp, 200, service.buscarPorId(id)
            );

        } catch (DatosInvalidosException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(resp, 404, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500,
                    "Error al consultar años lectivos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws IOException {

        if (!verificarPermiso(req, resp)) {
            return;
        }

        try {
            String ruta = req.getPathInfo();

            if (ruta == null || ruta.equals("/")) {
                PeticionCiclo datos = gson.fromJson(
                        req.getReader(), PeticionCiclo.class
                );

                service.crear(datos);

                Json.escribir(resp, 201, Map.of(
                        "mensaje", "Año lectivo creado correctamente"
                ));
                return;
            }

            if (ruta.endsWith("/grados")) {
                int idCiclo = obtenerId(
                        ruta.substring(0, ruta.length() - 7)
                );

                PeticionGradoCiclo datos = gson.fromJson(
                        req.getReader(), PeticionGradoCiclo.class
                );

                if (datos == null || datos.idGrado == null) {
                    throw new DatosInvalidosException(
                            "Selecciona un grado"
                    );
                }

                service.agregarGrado(
                        idCiclo, datos.idGrado
                );

                Json.escribir(resp, 201, Map.of(
                        "mensaje", "Grado asignado correctamente"
                ));
                return;
            }

            throw new DatosInvalidosException(
                    "Ruta no valida"
            );

        } catch (JsonSyntaxException |
                 DatosInvalidosException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(resp, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(resp, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500,
                    "No se pudo guardar la informacion");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {

        if (!verificarPermiso(req, resp)) {
            return;
        }

        try {
            String ruta = req.getPathInfo();

            if (ruta == null || !ruta.endsWith("/estado")) {
                throw new DatosInvalidosException(
                        "Ruta no valida"
                );
            }

            int id = obtenerId(
                    ruta.substring(0, ruta.length() - 7)
            );

            EstadoCiclo datos = gson.fromJson(
                    req.getReader(), EstadoCiclo.class
            );

            if (datos == null || datos.estado == null) {
                throw new DatosInvalidosException(
                        "Indica el estado del año lectivo"
                );
            }

            service.cambiarEstado(id, datos.estado);

            Json.escribir(resp, 200, Map.of(
                    "mensaje", "Estado actualizado correctamente"
            ));

        } catch (JsonSyntaxException |
                 DatosInvalidosException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(resp, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(resp, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500,
                    "Error al cambiar estado del año lectivo");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req,
                            HttpServletResponse resp) throws IOException {

        if (!verificarPermiso(req, resp)) {
            return;
        }

        try {
            String ruta = req.getPathInfo();

            if (ruta == null) {
                throw new DatosInvalidosException(
                        "Ruta no valida"
                );
            }

            String[] partes = ruta.split("/");

            if (partes.length != 4 ||
                    !partes[2].equals("grados")) {
                throw new DatosInvalidosException(
                        "Ruta no valida"
                );
            }

            int idCiclo = obtenerNumero(partes[1]);
            int idGrado = obtenerNumero(partes[3]);

            service.quitarGrado(idCiclo, idGrado);

            Json.escribir(resp, 200, Map.of(
                    "mensaje", "Grado quitado del año lectivo"
            ));

        } catch (DatosInvalidosException e) {
            Json.error(resp, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(resp, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(resp, 409, e.getMessage());
        } catch (SQLException e) {
            Json.error(resp, 500,
                    "No se pudo quitar el grado");
        }
    }

    private int leerParametro(HttpServletRequest req,
                              String nombre, int defecto)
            throws DatosInvalidosException {

        String valor = req.getParameter(nombre);

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

    private int obtenerId(String ruta)
            throws DatosInvalidosException {

        if (ruta == null || !ruta.startsWith("/")) {
            throw new DatosInvalidosException(
                    "ID de año lectivo invalido"
            );
        }

        return obtenerNumero(ruta.substring(1));
    }

    private int obtenerNumero(String valor)
            throws DatosInvalidosException {

        try {
            int numero = Integer.parseInt(valor);

            if (numero <= 0) {
                throw new DatosInvalidosException(
                        "ID invalido"
                );
            }

            return numero;

        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(
                    "ID invalido"
            );
        }
    }

    private boolean verificarPermiso(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        String rol = (String) req.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "SUPER_ADMIN")) {
            Json.error(resp, 403,
                    "No tienes permiso para administrar años lectivos");
            return false;
        }

        return true;
    }

    private static class EstadoCiclo {
        String estado;
    }

    private static class PeticionGradoCiclo {
        Integer idGrado;
    }
}
