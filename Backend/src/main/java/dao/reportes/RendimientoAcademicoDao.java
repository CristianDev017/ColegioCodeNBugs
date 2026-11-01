
package dao.reportes;

import config.DBConnectionSingleton;
import model.reportes.RendimientoAcademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RendimientoAcademicoDao {

    public List<RendimientoAcademico> listar(
            int idCiclo,
            int idGrado,
            int idSeccion) throws SQLException {

        List<RendimientoAcademico> lista = new ArrayList<>();

        String sql =
                "SELECT c.nombre AS curso, " +
                        "g.nombre AS grado, " +
                        "s.nombre AS seccion, " +
                        "COUNT(DISTINCT i.id_inscripcion) AS alumnos_evaluados, " +
                        "ROUND(AVG(CASE WHEN i.id_inscripcion IS NOT NULL " +
                        "THEN resultado.nota_final END), 2) AS promedio " +
                        "FROM asignacion_maestro am " +
                        "INNER JOIN curriculo_curso cc " +
                        "ON cc.id_curriculo_curso = am.id_curriculo_curso " +
                        "INNER JOIN curriculo cu " +
                        "ON cu.id_curriculo = cc.id_curriculo " +
                        "INNER JOIN curso c " +
                        "ON c.id_curso = cc.id_curso " +
                        "INNER JOIN seccion s " +
                        "ON s.id_seccion = am.id_seccion " +
                        "INNER JOIN grado g " +
                        "ON g.id_grado = s.id_grado " +
                        "LEFT JOIN ( " +
                        "SELECT p.id_asignacion, n.id_inscripcion, " +
                        "100 * SUM(n.valor / p.nota_maxima * p.porcentaje) " +
                        "/ SUM(p.porcentaje) AS nota_final " +
                        "FROM ponderacion p " +
                        "INNER JOIN nota n " +
                        "ON n.id_ponderacion = p.id_ponderacion " +
                        "GROUP BY p.id_asignacion, n.id_inscripcion " +
                        ") resultado " +
                        "ON resultado.id_asignacion = am.id_asignacion " +
                        "LEFT JOIN inscripcion i " +
                        "ON i.id_inscripcion = resultado.id_inscripcion " +
                        "AND i.id_seccion = am.id_seccion " +
                        "AND i.id_ciclo = cu.id_ciclo " +
                        "AND i.estado = 'ACTIVA' " +
                        "WHERE cu.id_ciclo = ? " +
                        "AND cu.id_grado = s.id_grado " +
                        "AND (? = 0 OR g.id_grado = ?) " +
                        "AND (? = 0 OR s.id_seccion = ?) " +
                        "GROUP BY am.id_asignacion, c.nombre, " +
                        "g.nombre, s.nombre " +
                        "ORDER BY promedio DESC, c.nombre";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);
            ps.setInt(3, idGrado);
            ps.setInt(4, idSeccion);
            ps.setInt(5, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    lista.add(new RendimientoAcademico(
                            rs.getString("curso"),
                            rs.getString("grado"),
                            rs.getString("seccion"),
                            rs.getInt("alumnos_evaluados"),
                            rs.getBigDecimal("promedio")
                    ));
                }
            }
        }

        return lista;
    }
}
