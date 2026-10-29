package dao;

import config.DBConnectionSingleton;
import dto.PeticionUsuarioOperativo;
import model.OpcionEmpleado;
import model.UsuarioOperativo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioOperativoDao {

    private static final String DESDE = "FROM usuario u "
            + "JOIN rol r ON r.id_rol = u.id_rol "
            + "LEFT JOIN empleado e ON e.id_usuario = u.id_usuario "
            + "WHERE r.nombre IN ('SECRETARIA', 'MAESTRO', 'BIBLIOTECARIO', 'MANTENIMIENTO') ";

    private static final String SELECT_BASE = "SELECT u.id_usuario, u.id_rol, r.nombre AS rol, u.username, u.correo, "
            + "u.estado, u.fecha_creacion, u.ultimo_acceso, e.id_empleado, "
            + "CONCAT(e.apellidos, ', ', e.nombres) AS empleado " + DESDE;

    private static final String FILTRO = "AND (? = '' OR u.username LIKE ? OR u.correo LIKE ? "
            + "OR CONCAT(e.apellidos, ' ', e.nombres) LIKE ?) "
            + "AND (? = '' OR r.nombre = ?) AND (? = '' OR u.estado = ?) ";

    public List<UsuarioOperativo> listar(String buscar, String rol, String estado, int pagina, int tamano)
            throws SQLException {
        List<UsuarioOperativo> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY u.username LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = poner(ps, buscar, rol, estado);
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

    public int contar(String buscar, String rol, String estado) throws SQLException {
        String sql = "SELECT COUNT(*) " + DESDE + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            poner(ps, buscar, rol, estado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public UsuarioOperativo buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "AND u.id_usuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeUsername(String username) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM usuario WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean existeCorreo(String correo, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ? AND id_usuario <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public Integer idRol(String nombre) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id_rol FROM rol WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public String[] datosEmpleado(int idEmpleado) throws SQLException {
        String sql = "SELECT puesto, estado, IF(id_usuario IS NULL, '0', '1') FROM empleado WHERE id_empleado = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new String[]{rs.getString(1), rs.getString(2), rs.getString(3)} : null;
            }
        }
    }

    public List<OpcionEmpleado> empleadosDisponibles(String puestoA, String puestoB) throws SQLException {
        List<OpcionEmpleado> lista = new ArrayList<>();
        String sql = "SELECT id_empleado, CONCAT(apellidos, ', ', nombres, ' - DPI ', dpi) FROM empleado "
                + "WHERE estado = 'ACTIVO' AND id_usuario IS NULL AND puesto IN (?, ?) "
                + "ORDER BY apellidos, nombres";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, puestoA);
            ps.setString(2, puestoB);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new OpcionEmpleado(rs.getInt(1), rs.getString(2)));
                }
            }
        }
        return lista;
    }

    public int crear(PeticionUsuarioOperativo d, int idRol, String passwordEncriptada) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                int idUsuario;
                String sql = "INSERT INTO usuario (id_rol, username, correo, password) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idRol);
                    ps.setString(2, d.getUsername());
                    ps.setString(3, d.getCorreo());
                    ps.setString(4, passwordEncriptada);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        idUsuario = rs.getInt(1);
                    }
                }
                String vincular = "UPDATE empleado SET id_usuario = ? WHERE id_empleado = ? AND id_usuario IS NULL";
                try (PreparedStatement ps = con.prepareStatement(vincular)) {
                    ps.setInt(1, idUsuario);
                    ps.setInt(2, d.getIdEmpleado());
                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return -1;
                    }
                }
                con.commit();
                return idUsuario;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void actualizar(int id, String correo, String passwordEncriptada) throws SQLException {
        String sql = "UPDATE usuario SET correo = ?, password = COALESCE(?, password) WHERE id_usuario = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, passwordEncriptada);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE usuario SET estado = ? WHERE id_usuario = ?")) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private int poner(PreparedStatement ps, String buscar, String rol, String estado) throws SQLException {
        String patron = "%" + buscar + "%";
        ps.setString(1, buscar);
        ps.setString(2, patron);
        ps.setString(3, patron);
        ps.setString(4, patron);
        ps.setString(5, rol);
        ps.setString(6, rol);
        ps.setString(7, estado);
        ps.setString(8, estado);
        return 9;
    }

    private UsuarioOperativo leer(ResultSet rs) throws SQLException {
        int idEmpleado = rs.getInt("id_empleado");
        boolean hayEmpleado = !rs.wasNull();
        return new UsuarioOperativo(
                rs.getInt("id_usuario"),
                rs.getInt("id_rol"),
                rs.getString("rol"),
                rs.getString("username"),
                rs.getString("correo"),
                rs.getString("estado"),
                rs.getString("fecha_creacion"),
                rs.getString("ultimo_acceso"),
                hayEmpleado ? idEmpleado : null,
                rs.getString("empleado"));
    }
}