
package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String CONSULTA_USUARIOS =
            "SELECT u.id_usuario, u.id_rol, r.nombre AS rol, u.username, u.correo, u.password, u.estado "
                    + "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol ";

    public Usuario buscarPorUsernameOCorreo(String valor) throws SQLException {
        String sql = CONSULTA_USUARIOS + "WHERE u.username = ? OR u.correo = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setString(2, valor);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return convertirUsuario(rs);
            }
        }
        return null;
    }

    public void actualizarUltimoAcceso(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET ultimo_acceso = NOW() WHERE id_usuario = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    public List<Usuario> listar(int pagina, int tamano) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = CONSULTA_USUARIOS + "ORDER BY u.id_usuario LIMIT ? OFFSET ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(convertirUsuario(rs));
            }
        }
        return usuarios;
    }

    public int contar() throws SQLException {
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM usuario");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public Usuario buscarPorId(int idUsuario) throws SQLException {
        String sql = CONSULTA_USUARIOS + "WHERE u.id_usuario = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return convertirUsuario(rs);
            }
        }
        return null;
    }

    public Usuario buscarPorId(Connection con, int idUsuario) throws SQLException {
        String sql = CONSULTA_USUARIOS + "WHERE u.id_usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return convertirUsuario(rs);
            }
        }
        return null;
    }

    public boolean existeUsername(String username, int idUsuario) throws SQLException {
        String sql = "SELECT id_usuario FROM usuario WHERE username = ? AND id_usuario <> ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, idUsuario);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public boolean existeCorreo(String correo, int idUsuario) throws SQLException {
        String sql = "SELECT id_usuario FROM usuario WHERE correo = ? AND id_usuario <> ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setInt(2, idUsuario);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public boolean existeRol(int idRol) throws SQLException {
        String sql = "SELECT id_rol FROM rol WHERE id_rol = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public void crear(int idRol, String username, String correo, String password) throws SQLException {
        String sql = "INSERT INTO usuario (id_rol, username, correo, password) VALUES (?, ?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ps.setString(2, username);
            ps.setString(3, correo);
            ps.setString(4, password);
            ps.executeUpdate();
        }
    }

    public boolean editar(int idUsuario, int idRol, String username, String correo) throws SQLException {
        String sql = "UPDATE usuario SET id_rol = ?, username = ?, correo = ? WHERE id_usuario = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ps.setString(2, username);
            ps.setString(3, correo);
            ps.setInt(4, idUsuario);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean editar(Connection con, int idUsuario, int idRol, String username, String correo)
            throws SQLException {
        String sql = "UPDATE usuario SET id_rol = ?, username = ?, correo = ? WHERE id_usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ps.setString(2, username);
            ps.setString(3, correo);
            ps.setInt(4, idUsuario);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idUsuario, String estado) throws SQLException {
        String sql = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(Connection con, int idUsuario, String estado) throws SQLException {
        String sql = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;
        }
    }

    public int contarSuperAdminsActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario u "
                + "JOIN rol r ON r.id_rol = u.id_rol "
                + "WHERE r.nombre = 'SUPER_ADMIN' AND u.estado = 'ACTIVO'";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int contarSuperAdminsActivos(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario u "
                + "JOIN rol r ON r.id_rol = u.id_rol "
                + "WHERE r.nombre = 'SUPER_ADMIN' AND u.estado = 'ACTIVO'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void bloquearSuperAdmin(Connection con) throws SQLException {
        String sql = "SELECT id_rol FROM rol WHERE nombre = 'SUPER_ADMIN' FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new SQLException("No existe el rol SUPER_ADMIN");
            }
        }
    }

    private Usuario convertirUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getInt("id_rol"),
                rs.getString("rol"),
                rs.getString("username"),
                rs.getString("correo"),
                rs.getString("password"),
                rs.getString("estado")
        );
    }
}
