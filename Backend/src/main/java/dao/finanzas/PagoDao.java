package dao.finanzas;

import config.DBConnectionSingleton;
import dto.finanzas.PeticionPago;
import model.finanzas.Pago;
import model.finanzas.Tarifa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PagoDao {

    private static final String SELECT_BASE = "SELECT p.id_pago, p.id_estudiante, e.carnet, "
            + "CONCAT(e.nombres, ' ', e.apellidos) AS estudiante, p.id_ciclo, c.anio, p.id_actividad, "
            + "a.nombre AS actividad, p.concepto, p.mes, p.monto, p.fecha_pago, u.username AS usuario, "
            + "cm.numero, cm.fecha_emision "
            + "FROM pago p "
            + "JOIN estudiante e ON e.id_estudiante = p.id_estudiante "
            + "JOIN ciclo_escolar c ON c.id_ciclo = p.id_ciclo "
            + "LEFT JOIN actividad_extra a ON a.id_actividad = p.id_actividad "
            + "JOIN usuario u ON u.id_usuario = p.id_usuario "
            + "LEFT JOIN comprobante cm ON cm.id_pago = p.id_pago ";

    public List<Pago> listar(String buscar, int idCiclo, int pagina, int tamano) throws SQLException {
        List<Pago> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE (? = 0 OR p.id_ciclo = ?) "
                + "AND (e.carnet LIKE ? OR CONCAT(e.nombres, ' ', e.apellidos) LIKE ?) "
                + "ORDER BY p.id_pago DESC LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
            ps.setString(3, "%" + buscar + "%");
            ps.setString(4, "%" + buscar + "%");
            ps.setInt(5, tamano);
            ps.setInt(6, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(String buscar, int idCiclo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pago p JOIN estudiante e ON e.id_estudiante = p.id_estudiante "
                + "WHERE (? = 0 OR p.id_ciclo = ?) "
                + "AND (e.carnet LIKE ? OR CONCAT(e.nombres, ' ', e.apellidos) LIKE ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
            ps.setString(3, "%" + buscar + "%");
            ps.setString(4, "%" + buscar + "%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Pago buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE p.id_pago = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public String[] datosEstudiante(int idEstudiante) throws SQLException {
        String sql = "SELECT carnet, CONCAT(nombres, ' ', apellidos), estado FROM estudiante WHERE id_estudiante = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
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

    public Tarifa tarifaDe(int idEstudiante, int idCiclo) throws SQLException {
        String sql = "SELECT t.id_tarifa, g.id_grado, "
                + "CONCAT(n.nombre, ' - ', g.nombre, IF(ca.nombre IS NULL, '', CONCAT(' (', ca.nombre, ')'))) AS grado, "
                + "i.id_ciclo, c.anio, t.monto_inscripcion, t.monto_mensualidad, t.dia_vencimiento "
                + "FROM inscripcion i "
                + "JOIN seccion s ON s.id_seccion = i.id_seccion "
                + "JOIN grado g ON g.id_grado = s.id_grado "
                + "JOIN nivel n ON n.id_nivel = g.id_nivel "
                + "LEFT JOIN carrera ca ON ca.id_carrera = g.id_carrera "
                + "JOIN ciclo_escolar c ON c.id_ciclo = i.id_ciclo "
                + "LEFT JOIN tarifa t ON t.id_grado = g.id_grado AND t.id_ciclo = i.id_ciclo "
                + "WHERE i.id_estudiante = ? AND i.id_ciclo = ? AND i.estado = 'ACTIVA'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
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
    }

    public boolean existePago(int idEstudiante, int idCiclo, String concepto, Integer mes, Integer idActividad)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM pago WHERE id_estudiante = ? AND concepto = ? "
                + "AND (? IS NULL OR id_ciclo = ?) "
                + "AND (? IS NULL OR mes = ?) AND (? IS NULL OR id_actividad = ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setString(2, concepto);
            if (idActividad == null) {
                ps.setInt(3, idCiclo);
                ps.setInt(4, idCiclo);
            } else {
                ps.setNull(3, Types.INTEGER);
                ps.setNull(4, Types.INTEGER);
            }
            if (mes == null) {
                ps.setNull(5, Types.INTEGER);
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(5, mes);
                ps.setInt(6, mes);
            }
            if (idActividad == null) {
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(7, idActividad);
                ps.setInt(8, idActividad);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Integer> mesesPagados(int idEstudiante, int idCiclo) throws SQLException {
        List<Integer> meses = new ArrayList<>();
        String sql = "SELECT mes FROM pago WHERE id_estudiante = ? AND id_ciclo = ? AND concepto = 'MENSUALIDAD' "
                + "ORDER BY mes";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    meses.add(rs.getInt(1));
                }
            }
        }
        return meses;
    }

    public int registrar(PeticionPago d, int idUsuario) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                if (d.getIdActividad() != null && !hayCupo(con, d.getIdActividad())) {
                    con.rollback();
                    return -1;
                }
                int id = insertarPago(con, d, idUsuario);
                insertarComprobante(con, id);
                con.commit();
                return id;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private boolean hayCupo(Connection con, int idActividad) throws SQLException {
        String sql = "SELECT a.cupo_total - (SELECT COUNT(*) FROM pago p WHERE p.id_actividad = a.id_actividad) "
                + "FROM actividad_extra a WHERE a.id_actividad = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idActividad);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private int insertarPago(Connection con, PeticionPago d, int idUsuario) throws SQLException {
        String sql = "INSERT INTO pago (id_estudiante, id_ciclo, id_actividad, id_usuario, concepto, mes, monto, fecha_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdEstudiante());
            ps.setInt(2, d.getIdCiclo());
            if (d.getIdActividad() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, d.getIdActividad());
            }
            ps.setInt(4, idUsuario);
            ps.setString(5, d.getConcepto());
            if (d.getMes() == null) {
                ps.setNull(6, Types.TINYINT);
            } else {
                ps.setInt(6, d.getMes());
            }
            ps.setBigDecimal(7, d.getMonto());
            ps.setDate(8, java.sql.Date.valueOf(d.getFecha()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void insertarComprobante(Connection con, int idPago) throws SQLException {
        String sql = "INSERT INTO comprobante (id_pago, numero) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            ps.setString(2, String.format("CP-%06d", idPago));
            ps.executeUpdate();
        }
    }

    private Pago leer(ResultSet rs) throws SQLException {
        Integer idActividad = rs.getObject("id_actividad") == null ? null : rs.getInt("id_actividad");
        Integer mes = rs.getObject("mes") == null ? null : rs.getInt("mes");
        return new Pago(
                rs.getInt("id_pago"),
                rs.getInt("id_estudiante"),
                rs.getString("carnet"),
                rs.getString("estudiante"),
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                idActividad,
                rs.getString("actividad"),
                rs.getString("concepto"),
                mes,
                rs.getBigDecimal("monto"),
                rs.getString("fecha_pago"),
                rs.getString("usuario"),
                rs.getString("numero"),
                rs.getString("fecha_emision"));
    }
}