
package colegio.servlet;

import colegio.dto.PeticionCurriculo;
import colegio.dto.PeticionCursoCurriculo;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.services.CurriculoService;
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

@WebServlet("/api/curriculos/*")
public class CurriculoServlet extends HttpServlet {

    private final CurriculoService service = new CurriculoService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        try {
            String ruta = request.getPathInfo();

            if (ruta == null || ruta.equals("/")) {
                int pagina = leerParametro(request, "pagina", 1);
                int tamano = leerParametro(request, "tamano", 10);

                validarPaginacion(pagina, tamano);

                Json.escribir(response, 200, Map.of(
                        "datos", service.listar(pagina, tamano),
                        "total", service.contar(),
                        "pagina", pagina,
                        "tamano", tamano
                ));
                return;
            }

            String[] partes = ruta.split("/");

            if (partes.length == 3 && partes[2].equals("cursos")) {
                int id = obtenerId(partes[1]);
                int pagina = leerParametro(request, "pagina", 1);
                int tamano = leerParametro(request, "tamano", 10);

                validarPaginacion(pagina, tamano);

                Json.escribir(response, 200, Map.of(
                        "datos", service.listarCursos(id, pagina, tamano),
                        "total", service.contarCursos(id),
                        "pagina", pagina,
                        "tamano", tamano
                ));
                return;
            }

            Json.error(response, 404, "Ruta no encontrada");

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500,
                    "Error al consultar los curriculos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        try {
            String ruta = request.getPathInfo();

            if (ruta == null || ruta.equals("/")) {
                PeticionCurriculo datos = gson.fromJson(
                        request.getReader(), PeticionCurriculo.class
                );

                service.crear(datos);

                Json.escribir(response, 201, Map.of(
                        "mensaje", "Curriculo creado correctamente"
                ));
                return;
            }

            String[] partes = ruta.split("/");

            if (partes.length == 3 && partes[2].equals("cursos")) {
                int id = obtenerId(partes[1]);

                PeticionCursoCurriculo datos = gson.fromJson(
                        request.getReader(), PeticionCursoCurriculo.class
                );

                service.agregarCurso(id, datos);

                Json.escribir(response, 201, Map.of(
                        "mensaje", "Curso agregado al curriculo"
                ));
                return;
            }

            Json.error(response, 404, "Ruta no encontrada");

        } catch (DatosInvalidosException | JsonParseException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (ConflictoException e) {
            Json.error(response, 409, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500,
                    "Error al guardar el curriculo");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        if (!verificarPermiso(request, response)) {
            return;
        }

        try {
            String ruta = request.getPathInfo();

            if (ruta == null) {
                Json.error(response, 404, "Ruta no encontrada");
                return;
            }

            String[] partes = ruta.split("/");

            if (partes.length == 4 && partes[2].equals("cursos")) {
                int idCurriculo = obtenerId(partes[1]);
                int idCurso = obtenerId(partes[3]);

                service.quitarCurso(idCurriculo, idCurso);

                Json.escribir(response, 200, Map.of(
                        "mensaje", "Curso retirado del curriculo"
                ));
                return;
            }

            Json.error(response, 404, "Ruta no encontrada");

        } catch (DatosInvalidosException e) {
            Json.error(response, 400, e.getMessage());
        } catch (NoEncontradoException e) {
            Json.error(response, 404, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            Json.error(response, 500,
                    "Error al retirar el curso");
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

    private void validarPaginacion(int pagina, int tamano)
            throws DatosInvalidosException {

        if (pagina < 1 || tamano < 1 || tamano > 50) {
            throw new DatosInvalidosException(
                    "Pagina o tamaño invalido"
            );
        }
    }

    private int obtenerId(String valor)
            throws DatosInvalidosException {

        try {
            int id = Integer.parseInt(valor);

            if (id <= 0) {
                throw new DatosInvalidosException("ID invalido");
            }

            return id;

        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("ID invalido");
        }
    }

    private boolean verificarPermiso(HttpServletRequest request,
                                     HttpServletResponse response)
            throws IOException {

        String rol = (String) request.getAttribute("rol");

        if (!Permisos.tienePermiso(rol, "ADMIN")) {
            Json.error(response, 403,
                    "No tienes permiso para administrar curriculos");
            return false;
        }

        return true;
    }
}
