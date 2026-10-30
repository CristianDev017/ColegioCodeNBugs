package dao.finanzas;

import config.DBConnectionSingleton;
import dto.finanzas.PeticionTarifa;
import model.finanzas.Tarifa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TarifaDao {

    private static final String SELECT_BASE = "SELECT t.id_tarifa, t.id_grado, "
            + "CONCAT(n.nombre, ' - ', g.nombre, IF(ca.nombre IS NULL, '', CONCAT(' (', ca.nombre, ')'))) AS grado, "
            + "t.id_ciclo, c.anio, t.monto_inscripcion, t.monto_mensualidad, t.dia_vencimiento "
            + "FROM tarifa t "
            + "JOIN grado g ON g.id_grado = t.id_grado "
            + "JOIN nivel n ON n.id_nivel = g.id_nivel "
            + "LEFT JOIN carrera ca ON ca.id_carrera = g.id_carrera "
            + "JOIN ciclo_escolar c ON c.id_ciclo = t.id_ciclo ";

    public List<Tarifa> listar(int idCiclo, int pagina, int tamano) throws SQLException {
        List<Tarifa> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE (? = 0 OR t.id_ciclo = ?) "
                + "ORDER BY c.anio DESC, n.id_nivel, g.nombre LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
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

    public int contar(int idCiclo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tarifa WHERE (? = 0 OR id_ciclo = ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Tarifa buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE t.id_tarifa = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existe(int idGrado, int idCiclo, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tarifa WHERE id_grado = ? AND id_ciclo = ? AND id_tarifa <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public String estadoCiclo(int idCiclo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT estado FROM ciclo_escolar WHERE id_ciclo = ?")) {
            ps.setInt(1, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public Boolean gradoActivo(int idGrado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT activo FROM grado WHERE id_grado = ?")) {
            ps.setInt(1, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBoolean(1) : null;
            }
        }
    }

    public int crear(PeticionTarifa d) throws SQLException {
        String sql = "INSERT INTO tarifa (id_grado, id_ciclo, monto_inscripcion, monto_mensualidad, dia_vencimiento) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdGrado());
            ps.setInt(2, d.getIdCiclo());
            ps.setBigDecimal(3, d.getMontoInscripcion());
            ps.setBigDecimal(4, d.getMontoMensualidad());
            ps.setInt(5, d.getDiaVencimiento());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, PeticionTarifa d) throws SQLException {
        String sql = "UPDATE tarifa SET monto_inscripcion = ?, monto_mensualidad = ?, dia_vencimiento = ? "
                + "WHERE id_tarifa = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, d.getMontoInscripcion());
            ps.setBigDecimal(2, d.getMontoMensualidad());
            ps.setInt(3, d.getDiaVencimiento());
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    private Tarifa leer(ResultSet rs) throws SQLException {
        return new Tarifa(
                rs.getInt("id_tarifa"),
                rs.getInt("id_grado"),
                rs.getString("grado"),
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                rs.getBigDecimal("monto_inscripcion"),
                rs.getBigDecimal("monto_mensualidad"),
                rs.getInt("dia_vencimiento"));
    }
}