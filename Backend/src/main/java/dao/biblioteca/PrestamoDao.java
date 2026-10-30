package dao.biblioteca;

import config.DBConnectionSingleton;
import model.biblioteca.OpcionBiblioteca;
import model.biblioteca.Prestamo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDao {

    private static final String DESDE = "FROM prestamo p "
            + "JOIN libro l ON l.id_libro = p.id_libro "
            + "LEFT JOIN estudiante es ON es.id_estudiante = p.id_estudiante "
            + "LEFT JOIN empleado em ON em.id_empleado = p.id_empleado ";

    private static final String SELECT_BASE = "SELECT p.id_prestamo, p.id_libro, l.titulo, "
            + "CASE WHEN p.id_estudiante IS NOT NULL THEN 'ESTUDIANTE' ELSE 'EMPLEADO' END AS tipo, "
            + "COALESCE(CONCAT(es.nombres, ' ', es.apellidos), CONCAT(em.nombres, ' ', em.apellidos)) AS solicitante, "
            + "p.fecha_prestamo, p.fecha_esperada, p.fecha_devolucion, p.estado, p.observacion, "
            + "(p.estado = 'ACTIVO' AND p.fecha_esperada < CURDATE()) AS atrasado, "
            + "GREATEST(DATEDIFF(COALESCE(p.fecha_devolucion, CURDATE()), p.fecha_esperada), 0) AS dias_atraso "
            + DESDE;

    private static final String FILTRO = "WHERE (? = '' OR l.titulo LIKE ? "
            + "OR CONCAT(es.nombres, ' ', es.apellidos) LIKE ? OR CONCAT(em.nombres, ' ', em.apellidos) LIKE ?) "
            + "AND (? = '' OR (? = 'ATRASADO' AND p.estado = 'ACTIVO' AND p.fecha_esperada < CURDATE()) "
            + "OR (? <> 'ATRASADO' AND p.estado = ?)) ";

    public List<Prestamo> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY p.id_prestamo DESC LIMIT ? OFFSET ?";
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

    public Prestamo buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE p.id_prestamo = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public List<OpcionBiblioteca> opcionesLibros(String buscar) throws SQLException {
        String sql = "SELECT id_libro, CONCAT(titulo, ' - ', autor, ' (', cantidad_disponible, ' disponibles)') "
                + "FROM libro WHERE activo = 1 AND cantidad_disponible > 0 AND (titulo LIKE ? OR autor LIKE ?) "
                + "ORDER BY titulo LIMIT 200";
        return opciones(sql, buscar, 2);
    }

    public List<OpcionBiblioteca> opcionesEstudiantes(String buscar) throws SQLException {
        String sql = "SELECT id_estudiante, CONCAT(apellidos, ', ', nombres, ' (', carnet, ')') FROM estudiante "
                + "WHERE estado = 'ACTIVO' AND (carnet LIKE ? OR CONCAT(nombres, ' ', apellidos) LIKE ?) "
                + "ORDER BY apellidos, nombres LIMIT 200";
        return opciones(sql, buscar, 2);
    }

    public List<OpcionBiblioteca> opcionesEmpleados(String buscar, boolean maestros) throws SQLException {
        String condicion = maestros ? "puesto = 'Maestro'" : "puesto <> 'Maestro'";
        String sql = "SELECT id_empleado, CONCAT(apellidos, ', ', nombres, ' (', puesto, ')') FROM empleado "
                + "WHERE estado = 'ACTIVO' AND " + condicion
                + " AND (CONCAT(nombres, ' ', apellidos) LIKE ? OR puesto LIKE ?) "
                + "ORDER BY apellidos, nombres LIMIT 200";
        return opciones(sql, buscar, 2);
    }

    public boolean estudianteActivo(int id) throws SQLException {
        return existe("SELECT COUNT(*) FROM estudiante WHERE id_estudiante = ? AND estado = 'ACTIVO'", id);
    }

    public boolean empleadoActivo(int id) throws SQLException {
        return existe("SELECT COUNT(*) FROM empleado WHERE id_empleado = ? AND estado = 'ACTIVO'", id);
    }

    public int registrar(int idLibro, Integer idEstudiante, Integer idEmpleado, int idUsuario, int dias)
            throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                String descontar = "UPDATE libro SET cantidad_disponible = cantidad_disponible - 1 "
                        + "WHERE id_libro = ? AND activo = 1 AND cantidad_disponible > 0";
                try (PreparedStatement ps = con.prepareStatement(descontar)) {
                    ps.setInt(1, idLibro);
                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return -1;
                    }
                }
                String insertar = "INSERT INTO prestamo (id_libro, id_estudiante, id_empleado, id_usuario, "
                        + "fecha_prestamo, fecha_esperada) VALUES (?, ?, ?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY))";
                int id;
                try (PreparedStatement ps = con.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idLibro);
                    poner(ps, 2, idEstudiante);
                    poner(ps, 3, idEmpleado);
                    ps.setInt(4, idUsuario);
                    ps.setInt(5, dias);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getInt(1);
                    }
                }
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

    public boolean devolver(int idPrestamo, int idLibro, String observacion, int dias, BigDecimal monto)
            throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                String cerrar = "UPDATE prestamo SET estado = 'DEVUELTO', fecha_devolucion = CURDATE(), "
                        + "observacion = ? WHERE id_prestamo = ? AND estado = 'ACTIVO'";
                try (PreparedStatement ps = con.prepareStatement(cerrar)) {
                    if (observacion == null) {
                        ps.setNull(1, Types.VARCHAR);
                    } else {
                        ps.setString(1, observacion);
                    }
                    ps.setInt(2, idPrestamo);
                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE libro SET cantidad_disponible = cantidad_disponible + 1 WHERE id_libro = ?")) {
                    ps.setInt(1, idLibro);
                    ps.executeUpdate();
                }
                if (dias > 0) {
                    String multa = "INSERT INTO multa (id_prestamo, dias_atraso, monto, fecha_generacion) "
                            + "VALUES (?, ?, ?, CURDATE())";
                    try (PreparedStatement ps = con.prepareStatement(multa)) {
                        ps.setInt(1, idPrestamo);
                        ps.setInt(2, dias);
                        ps.setBigDecimal(3, monto);
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private List<OpcionBiblioteca> opciones(String sql, String buscar, int cantidad) throws SQLException {
        List<OpcionBiblioteca> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= cantidad; i++) {
                ps.setString(i, "%" + buscar + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new OpcionBiblioteca(rs.getInt(1), rs.getString(2)));
                }
            }
        }
        return lista;
    }

    private boolean existe(String sql, int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void poner(PreparedStatement ps, int posicion, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.INTEGER);
        } else {
            ps.setInt(posicion, valor);
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
        ps.setString(7, estado);
        ps.setString(8, estado);
        return 9;
    }

    private Prestamo leer(ResultSet rs) throws SQLException {
        return new Prestamo(
                rs.getInt("id_prestamo"),
                rs.getInt("id_libro"),
                rs.getString("titulo"),
                rs.getString("tipo"),
                rs.getString("solicitante"),
                rs.getString("fecha_prestamo"),
                rs.getString("fecha_esperada"),
                rs.getString("fecha_devolucion"),
                rs.getString("estado"),
                rs.getBoolean("atrasado"),
                rs.getInt("dias_atraso"),
                rs.getString("observacion"));
    }
}
