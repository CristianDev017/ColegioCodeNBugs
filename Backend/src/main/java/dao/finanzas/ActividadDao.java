package dao.finanzas;

import config.DBConnectionSingleton;
import dto.finanzas.PeticionActividad;
import model.finanzas.Actividad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActividadDao {

    private static final String SELECT_BASE = "SELECT a.id_actividad, a.nombre, a.costo, a.cupo_total, a.activo, "
            + "(SELECT COUNT(*) FROM pago p WHERE p.id_actividad = a.id_actividad) AS inscritos "
            + "FROM actividad_extra a ";

    public List<Actividad> listar(String buscar, int pagina, int tamano) throws SQLException {
        List<Actividad> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE a.nombre LIKE ? ORDER BY a.nombre LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + buscar + "%");
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

    public int contar(String buscar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM actividad_extra WHERE nombre LIKE ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + buscar + "%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public List<Actividad> listarActivas() throws SQLException {
        List<Actividad> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE a.activo = 1 ORDER BY a.nombre";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leer(rs));
            }
        }
        return lista;
    }

    public Actividad buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE a.id_actividad = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeNombre(String nombre, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM actividad_extra WHERE nombre = ? AND id_actividad <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int crear(PeticionActividad d) throws SQLException {
        String sql = "INSERT INTO actividad_extra (nombre, costo, cupo_total) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNombre());
            ps.setBigDecimal(2, d.getCosto());
            ps.setInt(3, d.getCupoTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, PeticionActividad d) throws SQLException {
        String sql = "UPDATE actividad_extra SET nombre = ?, costo = ?, cupo_total = ? WHERE id_actividad = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setBigDecimal(2, d.getCosto());
            ps.setInt(3, d.getCupoTotal());
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, boolean activo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE actividad_extra SET activo = ? WHERE id_actividad = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Actividad leer(ResultSet rs) throws SQLException {
        return new Actividad(
                rs.getInt("id_actividad"),
                rs.getString("nombre"),
                rs.getBigDecimal("costo"),
                rs.getInt("cupo_total"),
                rs.getInt("inscritos"),
                rs.getBoolean("activo"));
    }
}
