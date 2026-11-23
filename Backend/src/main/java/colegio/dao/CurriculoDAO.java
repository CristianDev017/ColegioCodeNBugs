
package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Curriculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CurriculoDAO {

    public List<Curriculo> listar(int pagina, int tamano)
            throws SQLException {

        List<Curriculo> lista = new ArrayList<>();

        String sql =
                "SELECT cu.id_curriculo, cu.id_grado, cu.id_ciclo, " +
                        "g.nombre AS grado, n.nombre AS nivel, " +
                        "ca.nombre AS carrera, ce.anio, ce.estado AS estado_ciclo " +
                        "FROM curriculo cu " +
                        "JOIN grado g ON cu.id_grado = g.id_grado " +
                        "JOIN nivel n ON g.id_nivel = n.id_nivel " +
                        "LEFT JOIN carrera ca ON g.id_carrera = ca.id_carrera " +
                        "JOIN ciclo_escolar ce ON cu.id_ciclo = ce.id_ciclo " +
                        "ORDER BY ce.anio DESC, n.nombre, g.nombre, cu.id_curriculo " +
                        "LIMIT ? OFFSET ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Curriculo(
                            rs.getInt("id_curriculo"),
                            rs.getInt("id_grado"),
                            rs.getInt("id_ciclo"),
                            rs.getString("grado"),
                            rs.getString("nivel"),
                            rs.getString("carrera"),
                            rs.getInt("anio"),
                            rs.getString("estado_ciclo")
                    ));
                }
            }
        }

        return lista;
    }

    public int contar() throws SQLException {

        String sql = "SELECT COUNT(*) FROM curriculo";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public List<Map<String, Object>> listarCursos(
            int idCurriculo, int pagina, int tamano)
            throws SQLException {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql =
                "SELECT c.id_curso, c.nombre, c.descripcion, c.activo " +
                        "FROM curriculo_curso cc " +
                        "JOIN curso c ON c.id_curso = cc.id_curso " +
                        "WHERE cc.id_curriculo = ? " +
                        "ORDER BY c.nombre, c.id_curso " +
                        "LIMIT ? OFFSET ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);
            ps.setInt(2, tamano);
            ps.setInt(3, (pagina - 1) * tamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();

                    item.put("idCurso", rs.getInt("id_curso"));
                    item.put("nombre", rs.getString("nombre"));
                    item.put("descripcion", rs.getString("descripcion"));
                    item.put("activo", rs.getBoolean("activo"));

                    lista.add(item);
                }
            }
        }

        return lista;
    }

    public int contarCursos(int idCurriculo) throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM curriculo_curso " +
                        "WHERE id_curriculo = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public boolean existeCurriculo(int idCurriculo)
            throws SQLException {

        return existe(
                "SELECT 1 FROM curriculo WHERE id_curriculo = ?",
                idCurriculo
        );
    }

    public boolean existeGrado(int idGrado) throws SQLException {

        return existe(
                "SELECT 1 FROM grado WHERE id_grado = ?",
                idGrado
        );
    }

    public boolean existeCiclo(int idCiclo) throws SQLException {

        return existe(
                "SELECT 1 FROM ciclo_escolar WHERE id_ciclo = ?",
                idCiclo
        );
    }

    public boolean existeCurso(int idCurso) throws SQLException {

        return existe(
                "SELECT 1 FROM curso WHERE id_curso = ?",
                idCurso
        );
    }

    public boolean existeCombinacion(int idGrado, int idCiclo)
            throws SQLException {

        String sql =
                "SELECT 1 FROM curriculo WHERE id_grado = ? AND id_ciclo = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeCursoEnCurriculo(int idCurriculo, int idCurso)
            throws SQLException {

        String sql =
                "SELECT 1 FROM curriculo_curso " +
                        "WHERE id_curriculo = ? AND id_curso = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);
            ps.setInt(2, idCurso);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void crear(int idGrado, int idCiclo) throws SQLException {

        String sql =
                "INSERT INTO curriculo (id_grado, id_ciclo) VALUES (?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);
            ps.executeUpdate();
        }
    }

    public void agregarCurso(int idCurriculo, int idCurso)
            throws SQLException {

        String sql =
                "INSERT INTO curriculo_curso (id_curriculo, id_curso) " +
                        "VALUES (?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);
            ps.setInt(2, idCurso);
            ps.executeUpdate();
        }
    }

    public boolean quitarCurso(int idCurriculo, int idCurso)
            throws SQLException {

        String sql =
                "DELETE FROM curriculo_curso " +
                        "WHERE id_curriculo = ? AND id_curso = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);
            ps.setInt(2, idCurso);

            return ps.executeUpdate() > 0;
        }
    }

    private boolean existe(String sql, int id) throws SQLException {

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
