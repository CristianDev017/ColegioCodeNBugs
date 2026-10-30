package dao.reportes;

import config.DBConnectionSingleton;
import model.reportes.OpcionReporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpcionReporteDao {

    private static final String CICLOS = "SELECT id_ciclo, CONCAT(anio, IF(estado = 'ACTIVO', ' (activo)', '')) "
            + "FROM ciclo_escolar ";

    private static final String GRADOS = "SELECT g.id_grado, CONCAT(n.nombre, ' - ', g.nombre, "
            + "IFNULL(CONCAT(' (', c.nombre, ')'), '')) FROM grado g "
            + "JOIN nivel n ON n.id_nivel = g.id_nivel LEFT JOIN carrera c ON c.id_carrera = g.id_carrera ";

    public List<OpcionReporte> ciclos() throws SQLException {
        return lista(CICLOS + "ORDER BY (estado = 'ACTIVO') DESC, anio DESC", null);
    }

    public List<OpcionReporte> grados() throws SQLException {
        return lista(GRADOS + "WHERE g.activo = 1 ORDER BY n.id_nivel, g.nombre", null);
    }

    public List<OpcionReporte> secciones(int idGrado) throws SQLException {
        return lista("SELECT id_seccion, nombre FROM seccion WHERE id_grado = ? AND activo = 1 ORDER BY nombre", idGrado);
    }

    public List<OpcionReporte> estudiantes() throws SQLException {
        return lista("SELECT id_estudiante, CONCAT(apellidos, ', ', nombres, ' (', carnet, ')') FROM estudiante "
                + "ORDER BY apellidos, nombres LIMIT 500", null);
    }

    public String textoCiclo(int id) throws SQLException {
        return uno(CICLOS + "WHERE id_ciclo = ?", id);
    }

    public String textoGrado(int id) throws SQLException {
        return uno(GRADOS + "WHERE g.id_grado = ?", id);
    }

    public String textoSeccion(int id) throws SQLException {
        return uno("SELECT id_seccion, nombre FROM seccion WHERE id_seccion = ?", id);
    }

    public String textoEstudiante(int id) throws SQLException {
        return uno("SELECT id_estudiante, CONCAT(apellidos, ', ', nombres, ' (', carnet, ')') FROM estudiante "
                + "WHERE id_estudiante = ?", id);
    }

    private List<OpcionReporte> lista(String sql, Integer parametro) throws SQLException {
        List<OpcionReporte> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new OpcionReporte(rs.getInt(1), rs.getString(2)));
                }
            }
        }
        return lista;
    }

    private String uno(String sql, int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(2) : "";
            }
        }
    }
}