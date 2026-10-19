package dao;

import config.DBConnectionSingleton;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    public Usuario buscarPorUsernameOCorreo(String valor) throws SQLException {
        String sql = "SELECT u.id_usuario, u.id_rol, r.nombre AS rol, u.username, u.correo, u.password, u.estado "
                + "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
                + "WHERE u.username = ? OR u.correo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setString(2, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getInt("id_rol"),
                        rs.getString("rol"),
                        rs.getString("username"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("estado"));
            }
        }
    }

    public void actualizarUltimoAcceso(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET ultimo_acceso = NOW() WHERE id_usuario = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }
}