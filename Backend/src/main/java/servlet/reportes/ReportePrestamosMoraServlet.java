package servlet.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reportes.FilaMora;
import service.reportes.MoraService;
import util.Auth;
import util.Errores;
import util.Json;
import util.ReporteUtil;
import util.Roles;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reportes/prestamos-mora/*")
public class ReportePrestamosMoraServlet extends HttpServlet {

    private final MoraService servicio = new MoraService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.BIBLIOTECARIO)) {
            return;
        }
        try {
            String buscar = texto(req.getParameter("buscar"));
            String tipo = texto(req.getParameter("tipo"));
            String ruta = req.getPathInfo();
            if (ruta == null || ruta.equals("/")) {
                int pagina = numero(req.getParameter("pagina"), 1);
                int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
                Json.escribir(resp, 200, Map.of(
                        "datos", servicio.listar(buscar, tipo, pagina, tamano),
                        "total", servicio.contar(buscar, tipo),
                        "pagina", pagina,
                        "tamano", tamano));
            } else if (ruta.equals("/pdf")) {
                List<FilaMora> filas = servicio.todos(buscar, tipo);
                Map<String, Object> parametros = ReporteUtil.parametros("Préstamos en mora", describirFiltros(buscar, tipo));
                byte[] pdf = ReporteUtil.generarPdf(getServletContext(), "prestamos_mora.jrxml", parametros, filas);
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "inline; filename=prestamos_en_mora.pdf");
                resp.setContentLength(pdf.length);
                resp.getOutputStream().write(pdf);
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            Errores.responder(resp, e);
        }
    }

    private String describirFiltros(String buscar, String tipo) {
        String filtros = "";
        if (!buscar.isEmpty()) {
            filtros += "Búsqueda: " + buscar + "   ";
        }
        if (tipo.equals("ESTUDIANTE")) {
            filtros += "Solicitante: estudiantes";
        } else if (tipo.equals("EMPLEADO")) {
            filtros += "Solicitante: maestros y personal";
        }
        return filtros.isEmpty() ? "Sin filtros" : filtros.trim();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private int numero(String texto, int porDefecto) {
        try {
            int valor = Integer.parseInt(texto);
            return valor < 1 ? porDefecto : valor;
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}