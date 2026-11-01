package dao.estudiantes;

import config.DBConnectionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsultaEstudianteDao {

    public Map<String, Object> perfil(int idUsuario) throws SQLException {

        String sql =
                "SELECT e.id_estudiante, e.carnet, e.nombres, e.apellidos, " +
                        "e.fecha_nacimiento, e.genero, e.direccion, e.estado, u.correo " +
                        "FROM estudiante e " +
                        "INNER JOIN usuario u ON u.id_usuario = e.id_usuario " +
                        "WHERE e.id_usuario = ?";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> datos = new LinkedHashMap<>();

                    datos.put("idEstudiante", rs.getInt("id_estudiante"));
                    datos.put("carnet", rs.getString("carnet"));
                    datos.put("nombres", rs.getString("nombres"));
                    datos.put("apellidos", rs.getString("apellidos"));
                    datos.put("correo", rs.getString("correo"));
                    datos.put("fechaNacimiento", rs.getString("fecha_nacimiento"));
                    datos.put("genero", rs.getString("genero"));
                    datos.put("direccion", rs.getString("direccion"));
                    datos.put("estado", rs.getString("estado"));

                    return datos;
                }
            }
        }

        return null;
    }

    public List<Map<String, Object>> notas(int idEstudiante) throws SQLException {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql =
                "SELECT ce.anio, c.nombre AS curso, p.nombre AS actividad, " +
                        "n.valor AS nota, p.nota_maxima " +
                        "FROM nota n " +
                        "INNER JOIN inscripcion i ON i.id_inscripcion = n.id_inscripcion " +
                        "INNER JOIN ciclo_escolar ce ON ce.id_ciclo = i.id_ciclo " +
                        "INNER JOIN ponderacion p ON p.id_ponderacion = n.id_ponderacion " +
                        "INNER JOIN asignacion_maestro am ON am.id_asignacion = p.id_asignacion " +
                        "INNER JOIN curriculo_curso cc ON cc.id_curriculo_curso = am.id_curriculo_curso " +
                        "INNER JOIN curriculo cu ON cu.id_curriculo = cc.id_curriculo " +
                        "INNER JOIN curso c ON c.id_curso = cc.id_curso " +
                        "WHERE i.id_estudiante = ? " +
                        "AND am.id_seccion = i.id_seccion " +
                        "AND cu.id_ciclo = i.id_ciclo " +
                        "ORDER BY ce.anio DESC, c.nombre, p.nombre";

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();

                    fila.put("anio", rs.getInt("anio"));
                    fila.put("curso", rs.getString("curso"));
                    fila.put("actividad", rs.getString("actividad"));
                    fila.put("nota", rs.getBigDecimal("nota"));
                    fila.put("notaMaxima", rs.getBigDecimal("nota_maxima"));

                    lista.add(fila);
                }
            }
        }

        return lista;
    }
}