package dao;

import config.DBConnectionSingleton;
import model.UsuarioAdmin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioAdminDao {

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.username, u.correo, r.nombre AS rol, u.estado, u.fecha_creacion, u.ultimo_acceso "
                    + "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol ";

    private static final String SOLO_ADMINS = "r.nombre IN ('SUPER_ADMIN','ADMIN') ";

    public List<UsuarioAdmin> listar(String buscar, int pagina, int tamano) throws SQLException {
        String sql = SELECT_BASE
                + "WHERE " + SOLO_ADMINS + "AND (u.username LIKE ? OR u.correo LIKE ?) "
                + "ORDER BY u.id_usuario LIMIT ? OFFSET ?";
        List<UsuarioAdmin> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + buscar + "%");
            ps.setString(2, "%" + buscar + "%");
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

    public int contar(String buscar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
                + "WHERE " + SOLO_ADMINS + "AND (u.username LIKE ? OR u.correo LIKE ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + buscar + "%");
            ps.setString(2, "%" + buscar + "%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public UsuarioAdmin buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE " + SOLO_ADMINS + "AND u.id_usuario = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeDuplicado(String username, String correo, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE (username = ? OR correo = ?) AND id_usuario <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, correo);
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int crear(String username, String correo, String passwordEncriptada, String rol) throws SQLException {
        String sql = "INSERT INTO usuario (id_rol, username, correo, password) "
                + "VALUES ((SELECT id_rol FROM rol WHERE nombre = ?), ?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rol);
            ps.setString(2, username);
            ps.setString(3, correo);
            ps.setString(4, passwordEncriptada);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, String username, String correo, String rol, String passwordEncriptada)
            throws SQLException {
        String sql = "UPDATE usuario SET username = ?, correo = ?, "
                + "id_rol = (SELECT id_rol FROM rol WHERE nombre = ?)"
                + (passwordEncriptada != null ? ", password = ?" : "")
                + " WHERE id_usuario = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, username);
            ps.setString(i++, correo);
            ps.setString(i++, rol);
            if (passwordEncriptada != null) {
                ps.setString(i++, passwordEncriptada);
            }
            ps.setInt(i, id);
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public int contarSuperAdminsActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
                + "WHERE r.nombre = 'SUPER_ADMIN' AND u.estado = 'ACTIVO'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private UsuarioAdmin leer(ResultSet rs) throws SQLException {
        return new UsuarioAdmin(
                rs.getInt("id_usuario"),
                rs.getString("username"),
                rs.getString("correo"),
                rs.getString("rol"),
                rs.getString("estado"),
                rs.getString("fecha_creacion"),
                rs.getString("ultimo_acceso"));
    }
}