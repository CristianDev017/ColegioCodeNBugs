package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Grado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradoDAO {

    private static final String CONSULTA =
            "SELECT g.id_grado, g.id_nivel, n.nombre AS nivel, " +
                    "g.id_carrera, c.nombre AS carrera, g.nombre, " +
                    "g.cupo_default, g.activo " +
                    "FROM grado g " +
                    "JOIN nivel n ON g.id_nivel = n.id_nivel " +
                    "LEFT JOIN carrera c ON g.id_carrera = c.id_carrera ";

    public List<Grado> listar() throws SQLException {
        List<Grado> grados = new ArrayList<>();

        String sql = CONSULTA + "ORDER BY n.nombre, g.nombre";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grados.add(convertir(rs));
                }
            }
        }

        return grados;
    }

    public Grado buscarPorId(int id) throws SQLException {
        String sql = CONSULTA + "WHERE g.id_grado = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertir(rs);
                }
            }
        }

        return null;
    }

    public String buscarNombreNivel(int idNivel) throws SQLException {
        String sql = "SELECT nombre FROM nivel WHERE id_nivel = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNivel);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        }

        return null;
    }

    public boolean existeCarreraActiva(int idCarrera) throws SQLException {
        String sql = "SELECT 1 FROM carrera WHERE id_carrera = ? AND activo = 1";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCarrera);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeDuplicado(int idNivel, Integer idCarrera,
                                   String nombre, int idExcluir) throws SQLException {

        String sql = "SELECT 1 FROM grado " +
                "WHERE id_nivel = ? AND id_carrera <=> ? " +
                "AND LOWER(nombre) = LOWER(?) AND id_grado <> ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNivel);

            if (idCarrera == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, idCarrera);
            }

            ps.setString(3, nombre);
            ps.setInt(4, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void crear(int idNivel, Integer idCarrera, String nombre,
                      int cupoDefault) throws SQLException {

        String sql = "INSERT INTO grado " +
                "(id_nivel, id_carrera, nombre, cupo_default, activo) " +
                "VALUES (?, ?, ?, ?, 1)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNivel);

            if (idCarrera == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, idCarrera);
            }

            ps.setString(3, nombre);
            ps.setInt(4, cupoDefault);
            ps.executeUpdate();
        }
    }

    public boolean editar(int id, int idNivel, Integer idCarrera,
                          String nombre, int cupoDefault) throws SQLException {

        String sql = "UPDATE grado SET id_nivel = ?, id_carrera = ?, " +
                "nombre = ?, cupo_default = ? WHERE id_grado = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNivel);

            if (idCarrera == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, idCarrera);
            }

            ps.setString(3, nombre);
            ps.setInt(4, cupoDefault);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE grado SET activo = ? WHERE id_grado = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Grado convertir(ResultSet rs) throws SQLException {
        int idCarrera = rs.getInt("id_carrera");
        Integer carreraId = rs.wasNull() ? null : idCarrera;

        return new Grado(
                rs.getInt("id_grado"),
                rs.getInt("id_nivel"),
                rs.getString("nivel"),
                carreraId,
                rs.getString("carrera"),
                rs.getString("nombre"),
                rs.getInt("cupo_default"),
                rs.getBoolean("activo")
        );
    }
}
