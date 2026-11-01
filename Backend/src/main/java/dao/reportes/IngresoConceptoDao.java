
package dao.reportes;

import config.DBConnectionSingleton;
import model.reportes.IngresoConcepto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IngresoConceptoDao {

    public List<IngresoConcepto> listar(
            LocalDate desde,
            LocalDate hasta) throws SQLException {

        List<IngresoConcepto> lista = new ArrayList<>();

        String sql =
                "SELECT p.concepto, " +
                        "COUNT(p.id_pago) AS cantidad_pagos, " +
                        "COALESCE(SUM(p.monto), 0) AS total " +
                        "FROM pago p " +
                        "WHERE (? IS NULL OR p.fecha_pago >= ?) " +
                        "AND (? IS NULL OR p.fecha_pago <= ?) " +
                        "GROUP BY p.concepto " +
                        "ORDER BY total DESC, p.concepto";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            establecerFechas(ps, desde, hasta);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(new IngresoConcepto(
                            rs.getString("concepto"),
                            rs.getInt("cantidad_pagos"),
                            rs.getBigDecimal("total")
                    ));
                }
            }
        }

        return lista;
    }

    public BigDecimal totalIngresos(
            LocalDate desde,
            LocalDate hasta) throws SQLException {

        String sql =
                "SELECT COALESCE(SUM(p.monto), 0) AS total " +
                        "FROM pago p " +
                        "WHERE (? IS NULL OR p.fecha_pago >= ?) " +
                        "AND (? IS NULL OR p.fecha_pago <= ?)";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            establecerFechas(ps, desde, hasta);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    private void establecerFechas(
            PreparedStatement ps,
            LocalDate desde,
            LocalDate hasta) throws SQLException {

        if (desde == null) {
            ps.setNull(1, java.sql.Types.DATE);
            ps.setNull(2, java.sql.Types.DATE);
        } else {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(desde));
        }

        if (hasta == null) {
            ps.setNull(3, java.sql.Types.DATE);
            ps.setNull(4, java.sql.Types.DATE);
        } else {
            ps.setDate(3, Date.valueOf(hasta));
            ps.setDate(4, Date.valueOf(hasta));
        }
    }
}
