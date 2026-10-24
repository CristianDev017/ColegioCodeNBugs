package dao;

import config.DBConnectionSingleton;
import dto.PeticionGrado;
import model.Grado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GradoDao {

    private static final String SELECT_BASE =
            "SELECT g.id_grado, g.id_nivel, n.nombre AS nivel, g.id_carrera, c.nombre AS carrera, "
                    + "g.nombre, g.cupo_default, g.activo "
                    + "FROM grado g JOIN nivel n ON n.id_nivel = g.id_nivel "
                    + "LEFT JOIN carrera c ON c.id_carrera = g.id_carrera ";

    public List<Grado> listar(String buscar, int idNivel, int pagina, int tamano) throws SQLException {
        String sql = SELECT_BASE + "WHERE g.nombre LIKE ? AND (? = 0 OR g.id_nivel = ?) "
                + "ORDER BY g.id_nivel, g.nombre LIMIT ? OFFSET ?";
        List<Grado> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + buscar + "%");
            ps.setInt(2, idNivel);
            ps.setInt(3, idNivel);
            ps.setInt(4, tamano);
            ps.setInt(5, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(String buscar, int idNivel) throws SQLException {
        String sql = "SELECT COUNT(*) FROM grado g WHERE g.nombre LIKE ? AND (? = 0 OR g.id_nivel = ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + buscar + "%");
            ps.setInt(2, idNivel);
            ps.setInt(3, idNivel);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Grado buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE g.id_grado = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeDuplicado(int idNivel, Integer idCarrera, String nombre, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM grado WHERE id_nivel = ? AND id_carrera <=> ? "
                + "AND nombre = ? AND id_grado <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idNivel);
            ps.setObject(2, idCarrera, Types.INTEGER);
            ps.setString(3, nombre);
            ps.setInt(4, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int contarInscritosActivos(int idGrado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion i "
                + "JOIN seccion s ON s.id_seccion = i.id_seccion "
                + "JOIN ciclo_escolar c ON c.id_ciclo = i.id_ciclo "
                + "WHERE s.id_grado = ? AND i.estado = 'ACTIVA' AND c.estado = 'ACTIVO'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int crear(PeticionGrado d) throws SQLException {
        String sql = "INSERT INTO grado (id_nivel, id_carrera, nombre, cupo_default) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdNivel());
            ps.setObject(2, d.getIdCarrera(), Types.INTEGER);
            ps.setString(3, d.getNombre());
            ps.setInt(4, d.getCupoDefault());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, PeticionGrado d) throws SQLException {
        String sql = "UPDATE grado SET id_nivel = ?, id_carrera = ?, nombre = ?, cupo_default = ? WHERE id_grado = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getIdNivel());
            ps.setObject(2, d.getIdCarrera(), Types.INTEGER);
            ps.setString(3, d.getNombre());
            ps.setInt(4, d.getCupoDefault());
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void cambiarActivo(int id, boolean activo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE grado SET activo = ? WHERE id_grado = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void cambiarCupoDefault(int id, int cupo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE grado SET cupo_default = ? WHERE id_grado = ?")) {
            ps.setInt(1, cupo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Grado leer(ResultSet rs) throws SQLException {
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
                rs.getBoolean("activo"));
    }
}