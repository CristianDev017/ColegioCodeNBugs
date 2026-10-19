package servlet;

import dao.UsuarioDao;
import model.Usuario;
import util.Json;
import util.JwtUtil;
import util.PasswordService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDao usuarioDao = new UsuarioDao();

    private static class PeticionLogin {
        String username;
        String password;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PeticionLogin datos = Json.leer(req, PeticionLogin.class);

        if (datos == null || datos.username == null || datos.username.isBlank()
                || datos.password == null || datos.password.isEmpty()) {
            Json.error(resp, 400, "Ingrese usuario y contraseña");
            return;
        }

        try {
            Usuario usuario = usuarioDao.buscarPorUsernameOCorreo(datos.username.trim());

            if (usuario == null || !PasswordService.coincide(datos.password, usuario.password())) {
                Json.error(resp, 401, "Usuario o contraseña incorrectos");
                return;
            }

            if (!"ACTIVO".equals(usuario.estado())) {
                Json.error(resp, 403, "El usuario está inactivo");
                return;
            }

            usuarioDao.actualizarUltimoAcceso(usuario.id());
            String token = JwtUtil.crear(usuario.id(), usuario.username(), usuario.rol());

            Json.escribir(resp, 200, Map.of(
                    "token", token,
                    "expiraEnMinutos", JwtUtil.MINUTOS,
                    "usuario", Map.of(
                            "id", usuario.id(),
                            "username", usuario.username(),
                            "rol", usuario.rol())));
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }
}