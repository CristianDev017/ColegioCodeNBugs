package dao.reportes;

import config.DBConnectionSingleton;
import model.reportes.FilaMora;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoraDao {

    private static final String DESDE = "FROM prestamo p "
            + "JOIN libro l ON l.id_libro = p.id_libro "
            + "LEFT JOIN estudiante es ON es.id_estudiante = p.id_estudiante "
            + "LEFT JOIN empleado em ON em.id_empleado = p.id_empleado "
            + "WHERE p.estado = 'ACTIVO' AND p.fecha_esperada < CURDATE() "
            + "AND (? = '' OR l.titulo LIKE ? OR CONCAT(es.nombres, ' ', es.apellidos) LIKE ? "
            + "OR CONCAT(em.nombres, ' ', em.apellidos) LIKE ?) "
            + "AND (? = '' OR (? = 'ESTUDIANTE' AND p.id_estudiante IS NOT NULL) "
            + "OR (? = 'EMPLEADO' AND p.id_empleado IS NOT NULL)) ";

    private static final String SELECT_BASE = "SELECT l.titulo AS libro, "
            + "COALESCE(CONCAT(es.nombres, ' ', es.apellidos), CONCAT(em.nombres, ' ', em.apellidos)) AS solicitante, "
            + "CASE WHEN p.id_estudiante IS NOT NULL THEN 'Estudiante' ELSE 'Empleado' END AS tipo, "
            + "p.fecha_prestamo, p.fecha_esperada, "
            + "DATEDIFF(CURDATE(), p.fecha_esperada) AS dias, "
            + "DATEDIFF(CURDATE(), p.fecha_esperada) * "
            + "(SELECT multa_por_dia FROM config_biblioteca ORDER BY id_config LIMIT 1) AS multa "
            + DESDE + "ORDER BY dias DESC, p.id_prestamo ";

    public List<FilaMora> listar(String buscar, String tipo, int pagina, int tamano) throws SQLException {
        List<FilaMora> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "LIMIT ? OFFSET ?")) {
            int i = poner(ps, buscar, tipo);
            ps.setInt(i++, tamano);
            ps.setInt(i, (pagina - 1) * tamano);
            leerFilas(ps, lista);
        }
        return lista;
    }

    public List<FilaMora> todos(String buscar, String tipo) throws SQLException {
        List<FilaMora> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "LIMIT 5000")) {
            poner(ps, buscar, tipo);
            leerFilas(ps, lista);
        }
        return lista;
    }

    public int contar(String buscar, String tipo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) " + DESDE)) {
            poner(ps, buscar, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void leerFilas(PreparedStatement ps, List<FilaMora> lista) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new FilaMora(
                        rs.getString("libro"),
                        rs.getString("solicitante"),
                        rs.getString("tipo"),
                        rs.getString("fecha_prestamo"),
                        rs.getString("fecha_esperada"),
                        rs.getInt("dias"),
                        rs.getBigDecimal("multa")));
            }
        }
    }

    private int poner(PreparedStatement ps, String buscar, String tipo) throws SQLException {
        String patron = "%" + buscar + "%";
        ps.setString(1, buscar);
        ps.setString(2, patron);
        ps.setString(3, patron);
        ps.setString(4, patron);
        ps.setString(5, tipo);
        ps.setString(6, tipo);
        ps.setString(7, tipo);
        return 8;
    }
}