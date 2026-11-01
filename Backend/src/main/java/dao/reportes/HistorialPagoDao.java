
package dao.reportes;

import config.DBConnectionSingleton;
import model.reportes.HistorialPago;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialPagoDao {

    public List<HistorialPago> listar(
            int idEstudiante,
            LocalDate desde,
            LocalDate hasta) throws SQLException {

        List<HistorialPago> lista = new ArrayList<>();

        String sql =
                "SELECT p.id_pago, " +
                        "e.carnet, " +
                        "CONCAT(e.nombres, ' ', e.apellidos) AS estudiante, " +
                        "ce.anio, " +
                        "p.concepto, " +
                        "CASE " +
                        "WHEN p.concepto = 'MENSUALIDAD' THEN CONCAT('Mes ', p.mes) " +
                        "WHEN p.concepto = 'INSCRIPCION' THEN 'Inscripción' " +
                        "WHEN p.id_actividad IS NOT NULL THEN COALESCE(a.nombre, 'Actividad') " +
                        "ELSE p.concepto " +
                        "END AS detalle, " +
                        "p.fecha_pago, " +
                        "p.monto, " +
                        "cm.numero AS comprobante " +
                        "FROM pago p " +
                        "INNER JOIN estudiante e " +
                        "ON e.id_estudiante = p.id_estudiante " +
                        "INNER JOIN ciclo_escolar ce " +
                        "ON ce.id_ciclo = p.id_ciclo " +
                        "LEFT JOIN actividad_extra a " +
                        "ON a.id_actividad = p.id_actividad " +
                        "LEFT JOIN comprobante cm " +
                        "ON cm.id_pago = p.id_pago " +
                        "WHERE p.id_estudiante = ? " +
                        "AND (? IS NULL OR p.fecha_pago >= ?) " +
                        "AND (? IS NULL OR p.fecha_pago <= ?) " +
                        "ORDER BY p.fecha_pago DESC, p.id_pago DESC";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            if (desde == null) {
                ps.setNull(2, java.sql.Types.DATE);
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setDate(2, Date.valueOf(desde));
                ps.setDate(3, Date.valueOf(desde));
            }

            if (hasta == null) {
                ps.setNull(4, java.sql.Types.DATE);
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(hasta));
                ps.setDate(5, Date.valueOf(hasta));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(new HistorialPago(
                            rs.getInt("id_pago"),
                            rs.getString("carnet"),
                            rs.getString("estudiante"),
                            rs.getInt("anio"),
                            rs.getString("concepto"),
                            rs.getString("detalle"),
                            rs.getString("fecha_pago"),
                            rs.getBigDecimal("monto"),
                            rs.getString("comprobante")
                    ));
                }
            }
        }

        return lista;
    }

    public BigDecimal totalPagado(
            int idEstudiante,
            LocalDate desde,
            LocalDate hasta) throws SQLException {

        String sql =
                "SELECT COALESCE(SUM(monto), 0) AS total " +
                        "FROM pago " +
                        "WHERE id_estudiante = ? " +
                        "AND (? IS NULL OR fecha_pago >= ?) " +
                        "AND (? IS NULL OR fecha_pago <= ?)";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            if (desde == null) {
                ps.setNull(2, java.sql.Types.DATE);
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setDate(2, Date.valueOf(desde));
                ps.setDate(3, Date.valueOf(desde));
            }

            if (hasta == null) {
                ps.setNull(4, java.sql.Types.DATE);
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(hasta));
                ps.setDate(5, Date.valueOf(hasta));
            }

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }

        return BigDecimal.ZERO;
    }
}
