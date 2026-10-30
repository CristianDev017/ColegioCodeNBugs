package dao.academico;

import config.DBConnectionSingleton;
import model.academico.CicloEscolar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CicloEscolarDao {

    public List<CicloEscolar> listar(int pagina, int tamano) throws SQLException {
        String sql = "SELECT id_ciclo, anio, fecha_inicio, fecha_fin, estado FROM ciclo_escolar "
                + "ORDER BY anio DESC LIMIT ? OFFSET ?";
        List<CicloEscolar> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar() throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ciclo_escolar");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public CicloEscolar buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_ciclo, anio, fecha_inicio, fecha_fin, estado FROM ciclo_escolar WHERE id_ciclo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeAnio(int anio, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ciclo_escolar WHERE anio = ? AND id_ciclo <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, anio);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int contarActivos() throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ciclo_escolar WHERE estado = 'ACTIVO'");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int crear(int anio, String fechaInicio, String fechaFin) throws SQLException {
        String sql = "INSERT INTO ciclo_escolar (anio, fecha_inicio, fecha_fin) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, anio);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, int anio, String fechaInicio, String fechaFin) throws SQLException {
        String sql = "UPDATE ciclo_escolar SET anio = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_ciclo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, anio);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE ciclo_escolar SET estado = ? WHERE id_ciclo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private CicloEscolar leer(ResultSet rs) throws SQLException {
        return new CicloEscolar(
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                rs.getString("fecha_inicio"),
                rs.getString("fecha_fin"),
                rs.getString("estado"));
    }
}