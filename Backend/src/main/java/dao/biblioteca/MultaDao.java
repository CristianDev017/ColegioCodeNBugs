package dao.biblioteca;

import config.DBConnectionSingleton;
import model.biblioteca.Multa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MultaDao {

    private static final String DESDE = "FROM multa m "
            + "JOIN prestamo p ON p.id_prestamo = m.id_prestamo "
            + "JOIN libro l ON l.id_libro = p.id_libro "
            + "LEFT JOIN estudiante es ON es.id_estudiante = p.id_estudiante "
            + "LEFT JOIN empleado em ON em.id_empleado = p.id_empleado ";

    private static final String SELECT_BASE = "SELECT m.id_multa, m.id_prestamo, l.titulo, "
            + "COALESCE(CONCAT(es.nombres, ' ', es.apellidos), CONCAT(em.nombres, ' ', em.apellidos)) AS solicitante, "
            + "m.dias_atraso, m.monto, m.estado, m.fecha_generacion " + DESDE;

    private static final String FILTRO = "WHERE (? = '' OR l.titulo LIKE ? "
            + "OR CONCAT(es.nombres, ' ', es.apellidos) LIKE ? OR CONCAT(em.nombres, ' ', em.apellidos) LIKE ?) "
            + "AND (? = '' OR m.estado = ?) ";

    public List<Multa> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        List<Multa> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY m.id_multa DESC LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = poner(ps, buscar, estado);
            ps.setInt(i++, tamano);
            ps.setInt(i, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(String buscar, String estado) throws SQLException {
        String sql = "SELECT COUNT(*) " + DESDE + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            poner(ps, buscar, estado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Multa buscarPorId(int id) throws SQLException {
        return buscarUna("WHERE m.id_multa = ?", id);
    }

    public Multa buscarPorPrestamo(int idPrestamo) throws SQLException {
        return buscarUna("WHERE m.id_prestamo = ?", idPrestamo);
    }

    public boolean marcarPagada(int id) throws SQLException {
        String sql = "UPDATE multa SET estado = 'PAGADA' WHERE id_multa = ? AND estado = 'PENDIENTE'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Multa buscarUna(String condicion, int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + condicion)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    private int poner(PreparedStatement ps, String buscar, String estado) throws SQLException {
        String patron = "%" + buscar + "%";
        ps.setString(1, buscar);
        ps.setString(2, patron);
        ps.setString(3, patron);
        ps.setString(4, patron);
        ps.setString(5, estado);
        ps.setString(6, estado);
        return 7;
    }

    private Multa leer(ResultSet rs) throws SQLException {
        return new Multa(
                rs.getInt("id_multa"),
                rs.getInt("id_prestamo"),
                rs.getString("titulo"),
                rs.getString("solicitante"),
                rs.getInt("dias_atraso"),
                rs.getBigDecimal("monto"),
                rs.getString("estado"),
                rs.getString("fecha_generacion"));
    }
}