package util;

import jakarta.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReporteUtil {

    private static final Map<String, JasperReport> PLANTILLAS = new HashMap<>();

    public static Map<String, Object> parametros(String titulo, String filtros) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO", titulo);
        parametros.put("FILTROS", filtros);
        parametros.put("GENERADO", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        return parametros;
    }

    public static byte[] generarPdf(ServletContext contexto, String plantilla, Map<String, Object> parametros,
                                    Collection<?> filas) throws JRException, IOException {
        JasperReport reporte = compilar(contexto, plantilla);
        JasperPrint impreso = JasperFillManager.fillReport(reporte, parametros, new JRBeanCollectionDataSource(filas));
        return JasperExportManager.exportReportToPdf(impreso);
    }

    private static synchronized JasperReport compilar(ServletContext contexto, String plantilla)
            throws JRException, IOException {
        JasperReport reporte = PLANTILLAS.get(plantilla);
        if (reporte == null) {
            String ruta = "/WEB-INF/reports/" + plantilla;
            try (InputStream in = contexto.getResourceAsStream(ruta)) {
                if (in == null) {
                    throw new IOException("No se encontró la plantilla: " + ruta);
                }
                JasperDesign diseno = JRXmlLoader.load(in);
                reporte = JasperCompileManager.compileReport(diseno);
            }
            PLANTILLAS.put(plantilla, reporte);
        }
        return reporte;
    }
}