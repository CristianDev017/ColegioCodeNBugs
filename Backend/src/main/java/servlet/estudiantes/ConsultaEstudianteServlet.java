package servlet.estudiantes;

import dao.estudiantes.ConsultaEstudianteDao;
import dao.reportes.HistorialPagoDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.reportes.HistorialPago;
import service.reportes.SaldoPendienteService;
import util.Auth;
import util.Json;
import util.Roles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/estudiante/consulta")
public class ConsultaEstudianteServlet extends HttpServlet {

    private final ConsultaEstudianteDao estudianteDao = new ConsultaEstudianteDao();
    private final HistorialPagoDao pagoDao = new HistorialPagoDao();
    private final SaldoPendienteService saldoService = new SaldoPendienteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (!Auth.exigirRol(req, resp, Roles.ESTUDIANTE)) {
            return;
        }

        try {
            int idUsuario = Auth.idUsuario(req);

            Map<String, Object> perfil = estudianteDao.perfil(idUsuario);

            if (perfil == null) {
                Json.error(resp, 404, "No se encontro el perfil del estudiante");
                return;
            }

            int idEstudiante = (Integer) perfil.get("idEstudiante");

            List<Map<String, Object>> notas = estudianteDao.notas(idEstudiante);

            List<HistorialPago> pagos = pagoDao.listar(idEstudiante, null, null);

            BigDecimal totalPagado = pagoDao.totalPagado(idEstudiante, null, null);

            BigDecimal saldoPendiente = saldoService.calcular(idEstudiante);

            Map<String, Object> respuesta = new LinkedHashMap<>();

            respuesta.put("perfil", perfil);
            respuesta.put("notas", notas);
            respuesta.put("pagos", pagos);
            respuesta.put("totalPagado", totalPagado);
            respuesta.put("saldoPendiente", saldoPendiente);

            Json.escribir(resp, 200, respuesta);

        } catch (Exception e) {
            log("Error al consultar los datos del estudiante", e);
            Json.error(resp, 500, "No se pudo cargar la informacion del estudiante");
        }
    }
}