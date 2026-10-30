package dao.reportes;

import config.DBConnectionSingleton;
import model.reportes.CuentaEstudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolventesDao {

    public List<CuentaEstudiante> cuentas(int idCiclo, int idGrado, int idSeccion) throws SQLException {
        List<CuentaEstudiante> lista = new ArrayList<>();
        String sql = "SELECT es.carnet, CONCAT(es.apellidos, ', ', es.nombres) AS estudiante, g.nombre AS grado, "
                + "s.nombre AS seccion, t.monto_inscripcion, t.monto_mensualidad, t.dia_vencimiento, "
                + "c.fecha_inicio, c.fecha_fin, "
                + "EXISTS (SELECT 1 FROM pago p WHERE p.id_estudiante = es.id_estudiante "
                + "AND p.id_ciclo = i.id_ciclo AND p.concepto = 'INSCRIPCION') AS inscripcion_pagada, "
                + "(SELECT GROUP_CONCAT(p.mes) FROM pago p WHERE p.id_estudiante = es.id_estudiante "
                + "AND p.id_ciclo = i.id_ciclo AND p.concepto = 'MENSUALIDAD') AS meses_pagados "
                + "FROM inscripcion i "
                + "JOIN estudiante es ON es.id_estudiante = i.id_estudiante "
                + "JOIN seccion s ON s.id_seccion = i.id_seccion "
                + "JOIN grado g ON g.id_grado = s.id_grado "
                + "JOIN ciclo_escolar c ON c.id_ciclo = i.id_ciclo "
                + "JOIN tarifa t ON t.id_grado = s.id_grado AND t.id_ciclo = i.id_ciclo "
                + "WHERE i.estado = 'ACTIVA' AND i.id_ciclo = ? AND (? = 0 OR s.id_grado = ?) "
                + "AND (? = 0 OR i.id_seccion = ?) "
                + "ORDER BY g.nombre, s.nombre, es.apellidos, es.nombres";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);
            ps.setInt(3, idGrado);
            ps.setInt(4, idSeccion);
            ps.setInt(5, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new CuentaEstudiante(
                            rs.getString("carnet"),
                            rs.getString("estudiante"),
                            rs.getString("grado"),
                            rs.getString("seccion"),
                            rs.getBigDecimal("monto_inscripcion"),
                            rs.getBigDecimal("monto_mensualidad"),
                            rs.getInt("dia_vencimiento"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin").toLocalDate(),
                            rs.getBoolean("inscripcion_pagada"),
                            rs.getString("meses_pagados")));
                }
            }
        }
        return lista;
    }
}