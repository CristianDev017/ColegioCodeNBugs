
package servlet.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reportes.HistorialPago;
import service.reportes.HistorialPagoService;
import service.reportes.SaldoPendienteService;
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

@WebServlet("/api/reportes/historial-pagos/*")
public class ReporteHistorialPagoServlet extends HttpServlet {

    private final HistorialPagoService servicio = new HistorialPagoService();
    private final SaldoPendienteService saldoService = new SaldoPendienteService();

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
            int idEstudiante = numero(req.getParameter("idEstudiante"), 0);

            LocalDate desde = fecha(req.getParameter("desde"));
            LocalDate hasta = fecha(req.getParameter("hasta"));

            String ruta = req.getPathInfo();

            List<HistorialPago> filas = servicio.listar(
                    idEstudiante,
                    desde,
                    hasta
            );

            BigDecimal totalPagado = servicio.totalPagado(
                    idEstudiante,
                    desde,
                    hasta
            );

            BigDecimal saldoPendiente = saldoService.calcular(idEstudiante);

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
                        "totalPagado", totalPagado,
                        "saldoPendiente", saldoPendiente
                ));

            } else if (ruta.equals("/pdf")) {

                String filtros = describirFiltros(desde, hasta);

                Map<String, Object> parametros = ReporteUtil.parametros(
                        "Historial de Pagos por Estudiante",
                        filtros
                );

                parametros.put("TOTAL_PAGADO", totalPagado);
                parametros.put("SALDO_PENDIENTE", saldoPendiente);

                if (!filas.isEmpty()) {
                    parametros.put(
                            "ESTUDIANTE",
                            filas.get(0).getEstudiante()
                    );

                    parametros.put(
                            "CARNET",
                            filas.get(0).getCarnet()
                    );
                } else {
                    parametros.put("ESTUDIANTE", "Sin pagos registrados");
                    parametros.put("CARNET", "");
                }

                byte[] pdf = ReporteUtil.generarPdf(
                        getServletContext(),
                        "historial_pagos.jrxml",
                        parametros,
                        filas
                );

                resp.setContentType("application/pdf");

                resp.setHeader(
                        "Content-Disposition",
                        "inline; filename=historial_pagos.pdf"
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
            return "Historial completo";
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
