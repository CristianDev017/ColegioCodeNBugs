package dao;

import config.DBConnectionSingleton;
import model.Seccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SeccionDao {

    private static final String SELECT_BASE =
            "SELECT s.id_seccion, s.id_grado, g.nombre AS grado, n.nombre AS nivel, "
                    + "s.nombre, s.cupo, s.activo "
                    + "FROM seccion s JOIN grado g ON g.id_grado = s.id_grado "
                    + "JOIN nivel n ON n.id_nivel = g.id_nivel ";

    public List<Seccion> listar(int idGrado, int pagina, int tamano) throws SQLException {
        String sql = SELECT_BASE + "WHERE (? = 0 OR s.id_grado = ?) "
                + "ORDER BY n.id_nivel, g.nombre, s.nombre LIMIT ? OFFSET ?";
        List<Seccion> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrado);
            ps.setInt(2, idGrado);
            ps.setInt(3, tamano);
            ps.setInt(4, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(int idGrado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM seccion s WHERE (? = 0 OR s.id_grado = ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrado);
            ps.setInt(2, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Seccion buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE s.id_seccion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeDuplicado(int idGrado, String nombre, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM seccion WHERE id_grado = ? AND nombre = ? AND id_seccion <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrado);
            ps.setString(2, nombre);
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int contarInscritosActivos(int idSeccion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion i "
                + "JOIN ciclo_escolar c ON c.id_ciclo = i.id_ciclo "
                + "WHERE i.id_seccion = ? AND i.estado = 'ACTIVA' AND c.estado = 'ACTIVO'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int crear(int idGrado, String nombre, int cupo) throws SQLException {
        String sql = "INSERT INTO seccion (id_grado, nombre, cupo) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idGrado);
            ps.setString(2, nombre);
            ps.setInt(3, cupo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, String nombre, int cupo) throws SQLException {
        String sql = "UPDATE seccion SET nombre = ?, cupo = ? WHERE id_seccion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, cupo);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void cambiarActivo(int id, boolean activo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE seccion SET activo = ? WHERE id_seccion = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Seccion leer(ResultSet rs) throws SQLException {
        return new Seccion(
                rs.getInt("id_seccion"),
                rs.getInt("id_grado"),
                rs.getString("grado"),
                rs.getString("nivel"),
                rs.getString("nombre"),
                rs.getInt("cupo"),
                rs.getBoolean("activo"));
    }
}