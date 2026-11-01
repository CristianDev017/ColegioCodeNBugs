
package servlet.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reportes.RendimientoAcademico;
import service.reportes.RendimientoAcademicoService;
import util.Auth;
import util.Errores;
import util.Json;
import util.ReporteUtil;
import util.Roles;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reportes/rendimiento-academico/*")
public class ReporteRendimientoAcademicoServlet extends HttpServlet {

    private final RendimientoAcademicoService servicio =
            new RendimientoAcademicoService();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        if (!Auth.exigirRol(
                req,
                resp,
                Roles.ADMIN,
                Roles.SUPER_ADMIN)) {
            return;
        }

        try {
            int idCiclo = numero(req.getParameter("idCiclo"), 0);
            int idGrado = numero(req.getParameter("idGrado"), 0);
            int idSeccion = numero(req.getParameter("idSeccion"), 0);

            String ruta = req.getPathInfo();

            if (ruta == null || ruta.equals("/")) {

                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = numero(req.getParameter("tamano"), 10);

                pagina = Math.max(1, pagina);
                tamano = Math.min(Math.max(1, tamano), 50);

                List<RendimientoAcademico> filas = servicio.listar(
                        idCiclo,
                        idGrado,
                        idSeccion
                );

                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.pagina(filas, pagina, tamano),
                        "total", filas.size(),
                        "pagina", pagina,
                        "tamano", tamano
                ));

            } else if (ruta.equals("/pdf")) {

                List<RendimientoAcademico> filas = servicio.listar(
                        idCiclo,
                        idGrado,
                        idSeccion
                );

                String filtros = describirFiltros(
                        idCiclo,
                        idGrado,
                        idSeccion
                );

                Map<String, Object> parametros = ReporteUtil.parametros(
                        "Rendimiento Académico",
                        filtros
                );

                byte[] pdf = ReporteUtil.generarPdf(
                        getServletContext(),
                        "rendimiento_academico.jrxml",
                        parametros,
                        filas
                );

                resp.setContentType("application/pdf");

                resp.setHeader(
                        "Content-Disposition",
                        "inline; filename=rendimiento_academico.pdf"
                );

                resp.setContentLength(pdf.length);
                resp.getOutputStream().write(pdf);

            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }

        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private String describirFiltros(
            int idCiclo,
            int idGrado,
            int idSeccion) {

        String filtros = "Año lectivo ID: " + idCiclo;

        if (idGrado > 0) {
            filtros += "   Grado ID: " + idGrado;
        } else {
            filtros += "   Todos los grados";
        }

        if (idSeccion > 0) {
            filtros += "   Sección ID: " + idSeccion;
        } else {
            filtros += "   Todas las secciones";
        }

        return filtros;
    }

    private int numero(String texto, int porDefecto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
