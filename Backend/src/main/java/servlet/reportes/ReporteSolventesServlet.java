package servlet.reportes;

import exception.DatosInvalidosException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reportes.FilaSolvencia;
import service.reportes.OpcionReporteService;
import service.reportes.SolventesService;
import util.Auth;
import util.Errores;
import util.Json;
import util.ReporteUtil;
import util.Roles;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reportes/solventes/*")
public class ReporteSolventesServlet extends HttpServlet {

    private final SolventesService servicio = new SolventesService();
    private final OpcionReporteService opciones = new OpcionReporteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SECRETARIA)) {
            return;
        }
        try {
            int idCiclo = entero(req.getParameter("idCiclo"));
            int idGrado = entero(req.getParameter("idGrado"));
            int idSeccion = entero(req.getParameter("idSeccion"));
            String estado = texto(req.getParameter("estado"));
            LocalDate desde = fecha(req.getParameter("desde"));
            LocalDate hasta = fecha(req.getParameter("hasta"));
            List<FilaSolvencia> filas = servicio.filas(idCiclo, idGrado, idSeccion, estado, desde, hasta);

            String ruta = req.getPathInfo();
            if (ruta == null || ruta.equals("/")) {
                int pagina = Math.max(1, entero(req.getParameter("pagina")));
                int tamano = Math.min(Math.max(1, entero(req.getParameter("tamano"))), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.pagina(filas, pagina, tamano),
                        "total", filas.size(),
                        "pagina", pagina,
                        "tamano", tamano));
            } else if (ruta.equals("/pdf")) {
                String filtros = describirFiltros(idCiclo, idGrado, idSeccion, estado, desde, hasta);
                Map<String, Object> parametros = ReporteUtil.parametros("Solventes y morosos", filtros);
                byte[] pdf = ReporteUtil.generarPdf(getServletContext(), "solventes.jrxml", parametros, filas);
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "inline; filename=solventes_y_morosos.pdf");
                resp.setContentLength(pdf.length);
                resp.getOutputStream().write(pdf);
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private String describirFiltros(int idCiclo, int idGrado, int idSeccion, String estado,
                                    LocalDate desde, LocalDate hasta) throws SQLException {
        String filtros = "Año lectivo: " + opciones.textoCiclo(idCiclo);
        if (idGrado > 0) {
            filtros += "   Grado: " + opciones.textoGrado(idGrado);
        }
        if (idSeccion > 0) {
            filtros += "   Sección: " + opciones.textoSeccion(idSeccion);
        }
        if (!estado.isEmpty()) {
            filtros += "   Estado: " + (estado.equalsIgnoreCase("MOROSO") ? "morosos" : "solventes");
        }
        if (desde != null) {
            filtros += "   Desde: " + desde;
        }
        if (hasta != null) {
            filtros += "   Hasta: " + hasta;
        }
        return filtros;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private int entero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private LocalDate fecha(String texto) throws DatosInvalidosException {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("La fecha no es válida");
        }
    }
}