
package servlet.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reportes.IngresoConcepto;
import service.reportes.IngresoConceptoService;
import util.Auth;
import util.Errores;
import util.Json;
import util.ReporteUtil;
import util.Roles;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reportes/ingresos/*")
public class ReporteIngresosServlet extends HttpServlet {

    private final IngresoConceptoService servicio =
            new IngresoConceptoService();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN)) {
            return;
        }

        try {
            LocalDate desde = fecha(req.getParameter("desde"));
            LocalDate hasta = fecha(req.getParameter("hasta"));

            String ruta = req.getPathInfo();

            List<IngresoConcepto> filas = servicio.listar(
                    desde,
                    hasta
            );

            BigDecimal totalIngresos = servicio.totalIngresos(
                    desde,
                    hasta
            );

            if (ruta == null || ruta.equals("/")) {

                int pagina = Math.max(
                        1,
                        numero(req.getParameter("pagina"), 1)
                );

                int tamano = Math.min(
                        50,
                        Math.max(1, numero(req.getParameter("tamano"), 10))
                );

                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.pagina(filas, pagina, tamano),
                        "total", filas.size(),
                        "pagina", pagina,
                        "tamano", tamano,
                        "totalIngresos", totalIngresos
                ));

            } else if (ruta.equals("/pdf")) {

                String filtros = describirFiltros(desde, hasta);

                Map<String, Object> parametros = ReporteUtil.parametros(
                        "Reporte de Ingresos",
                        filtros
                );

                parametros.put("TOTAL_INGRESOS", totalIngresos);

                byte[] pdf = ReporteUtil.generarPdf(
                        getServletContext(),
                        "ingresos.jrxml",
                        parametros,
                        filas
                );

                resp.setContentType("application/pdf");

                resp.setHeader(
                        "Content-Disposition",
                        "inline; filename=reporte_ingresos.pdf"
                );

                resp.setContentLength(pdf.length);
                resp.getOutputStream().write(pdf);

            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }

        } catch (DateTimeParseException e) {
            Json.error(resp, 400, "El formato de fecha no es válido");
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private String describirFiltros(
            LocalDate desde,
            LocalDate hasta) {

        if (desde == null && hasta == null) {
            return "Histórico completo";
        }

        String filtros = "";

        if (desde != null) {
            filtros += "Desde: " + desde;
        }

        if (hasta != null) {
            if (!filtros.isEmpty()) {
                filtros += "   ";
            }

            filtros += "Hasta: " + hasta;
        }

        return filtros;
    }

    private LocalDate fecha(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return LocalDate.parse(valor.trim());
    }

    private int numero(String valor, int porDefecto) {

        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
