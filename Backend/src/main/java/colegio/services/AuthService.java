package colegio.services;

import colegio.dao.UsuarioDAO;
import colegio.model.Usuario;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;

public class AuthService {

    public Usuario login(String usuario, String password) throws SQLException {
        UsuarioDAO dao = new UsuarioDAO();

        Usuario usuarioEncontrado = dao.buscarPorUsernameOCorreo(usuario);

        if (usuarioEncontrado == null){
            System.out.println("Usarion no encotrado");
            return null;
        }

        if (!usuarioEncontrado.getEstado().equals("ACTIVO")){
            System.out.println("El usuario no esta activo");
            return null;

        }

        String passwordCodificada = Base64.getEncoder()
                .encodeToString(password.getBytes(StandardCharsets.UTF_8));

        if (!passwordCodificada.equals(usuarioEncontrado.getPassword())){
            System.out.println("La contrasena no coincide");
            return null;
        }
        return usuarioEncontrado;
    }

}
