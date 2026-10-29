package dao;

import config.DBConnectionSingleton;
import dto.PeticionPonderacion;
import model.Ponderacion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PonderacionDao {

    private static final String SELECT_BASE = "SELECT id_ponderacion, nombre, porcentaje, nota_maxima FROM ponderacion ";

    public List<Ponderacion> listar(int idAsignacion, int pagina, int tamano) throws SQLException {
        List<Ponderacion> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE id_asignacion = ? ORDER BY id_ponderacion LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.setInt(2, tamano);
            ps.setInt(3, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(int idAsignacion) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ponderacion WHERE id_asignacion = ?")) {
            ps.setInt(1, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Ponderacion buscarPorId(int idPonderacion, int idAsignacion) throws SQLException {
        String sql = SELECT_BASE + "WHERE id_ponderacion = ? AND id_asignacion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPonderacion);
            ps.setInt(2, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public BigDecimal suma(int idAsignacion, int excluirId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(porcentaje), 0) FROM ponderacion WHERE id_asignacion = ? AND id_ponderacion <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    public boolean existeNombre(int idAsignacion, String nombre, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ponderacion WHERE id_asignacion = ? AND nombre = ? AND id_ponderacion <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.setString(2, nombre);
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean tieneNotas(int idPonderacion) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM nota WHERE id_ponderacion = ?")) {
            ps.setInt(1, idPonderacion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int crear(int idAsignacion, PeticionPonderacion d) throws SQLException {
        String sql = "INSERT INTO ponderacion (id_asignacion, nombre, porcentaje, nota_maxima) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idAsignacion);
            ps.setString(2, d.getNombre());
            ps.setBigDecimal(3, d.getPorcentaje());
            ps.setBigDecimal(4, d.getNotaMaxima());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int idPonderacion, PeticionPonderacion d) throws SQLException {
        String sql = "UPDATE ponderacion SET nombre = ?, porcentaje = ?, nota_maxima = ? WHERE id_ponderacion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setBigDecimal(2, d.getPorcentaje());
            ps.setBigDecimal(3, d.getNotaMaxima());
            ps.setInt(4, idPonderacion);
            ps.executeUpdate();
        }
    }

    public void eliminar(int idPonderacion) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM ponderacion WHERE id_ponderacion = ?")) {
            ps.setInt(1, idPonderacion);
            ps.executeUpdate();
        }
    }

    private Ponderacion leer(ResultSet rs) throws SQLException {
        return new Ponderacion(
                rs.getInt("id_ponderacion"),
                rs.getString("nombre"),
                rs.getBigDecimal("porcentaje"),
                rs.getBigDecimal("nota_maxima"));
    }
}