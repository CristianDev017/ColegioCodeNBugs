package servlet;

import dao.UsuarioAdminDao;
import model.UsuarioAdmin;
import util.Auth;
import util.Json;
import util.PasswordService;
import util.Roles;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/admin/usuarios/*")
public class UsuarioAdminServlet extends HttpServlet {

    private final UsuarioAdminDao dao = new UsuarioAdminDao();

    private static class PeticionUsuario {
        String username;
        String correo;
        String password;
        String rol;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN)) {
            return;
        }
        int pagina = numero(req.getParameter("pagina"), 1);
        int tamano = Math.min(numero(req.getParameter("tamano"), 10), 50);
        String buscar = req.getParameter("buscar") == null ? "" : req.getParameter("buscar").trim();

        try {
            List<UsuarioAdmin> datos = dao.listar(buscar, pagina, tamano);
            int total = dao.contar(buscar);
            Json.escribir(resp, 200, Map.of("datos", datos, "total", total, "pagina", pagina, "tamano", tamano));
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN)) {
            return;
        }
        PeticionUsuario datos = Json.leer(req, PeticionUsuario.class);
        String error = validar(datos, true);
        if (error != null) {
            Json.error(resp, 400, error);
            return;
        }

        try {
            String username = datos.username.trim();
            String correo = datos.correo.trim();
            if (dao.existeDuplicado(username, correo, 0)) {
                Json.error(resp, 409, "El usuario o el correo ya están registrados");
                return;
            }
            int id = dao.crear(username, correo, PasswordService.encriptar(datos.password), datos.rol);
            Json.escribir(resp, 201, dao.buscarPorId(id));
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!Auth.exigirRol(req, resp, Roles.SUPER_ADMIN)) {
            return;
        }
        String[] partes = req.getPathInfo() == null ? new String[0] : req.getPathInfo().split("/");
        if (partes.length < 2) {
            Json.error(resp, 400, "Falta el id del usuario");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            Json.error(resp, 400, "El id no es válido");
            return;
        }

        try {
            UsuarioAdmin actual = dao.buscarPorId(id);
            if (actual == null) {
                Json.error(resp, 404, "El usuario no existe");
                return;
            }

            if (partes.length == 3 && partes[2].equals("desactivar")) {
                desactivar(resp, actual);
            } else if (partes.length == 3 && partes[2].equals("activar")) {
                dao.cambiarEstado(id, "ACTIVO");
                Json.escribir(resp, 200, dao.buscarPorId(id));
            } else if (partes.length == 2) {
                editar(req, resp, actual);
            } else {
                Json.error(resp, 404, "Ruta no encontrada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json.error(resp, 500, "Error interno del servidor");
        }
    }

    private void desactivar(HttpServletResponse resp, UsuarioAdmin actual) throws Exception {
        if ("INACTIVO".equals(actual.getEstado())) {
            Json.error(resp, 400, "El usuario ya está inactivo");
            return;
        }
        if (esUltimoSuperAdminActivo(actual)) {
            Json.error(resp, 400, "No se puede desactivar al último Super Admin activo");
            return;
        }
        dao.cambiarEstado(actual.getId(), "INACTIVO");
        Json.escribir(resp, 200, dao.buscarPorId(actual.getId()));
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp, UsuarioAdmin actual) throws Exception {
        PeticionUsuario datos = Json.leer(req, PeticionUsuario.class);
        String error = validar(datos, false);
        if (error != null) {
            Json.error(resp, 400, error);
            return;
        }

        String username = datos.username.trim();
        String correo = datos.correo.trim();
        if (dao.existeDuplicado(username, correo, actual.getId())) {
            Json.error(resp, 409, "El usuario o el correo ya están registrados");
            return;
        }
        if (esUltimoSuperAdminActivo(actual) && !Roles.SUPER_ADMIN.equals(datos.rol)) {
            Json.error(resp, 400, "Debe existir al menos un Super Admin activo");
            return;
        }

        String password = datos.password == null || datos.password.isEmpty()
                ? null : PasswordService.encriptar(datos.password);
        dao.actualizar(actual.getId(), username, correo, datos.rol, password);
        Json.escribir(resp, 200, dao.buscarPorId(actual.getId()));
    }

    private boolean esUltimoSuperAdminActivo(UsuarioAdmin usuario) throws Exception {
        return Roles.SUPER_ADMIN.equals(usuario.getRol())
                && "ACTIVO".equals(usuario.getEstado())
                && dao.contarSuperAdminsActivos() <= 1;
    }

    private String validar(PeticionUsuario d, boolean passwordObligatoria) {
        if (d == null) {
            return "Faltan los datos del usuario";
        }
        if (d.username == null || d.username.trim().length() < 3 || d.username.trim().length() > 50) {
            return "El usuario debe tener entre 3 y 50 caracteres";
        }
        if (d.correo == null || !d.correo.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            return "El correo no es válido";
        }
        if (!Roles.SUPER_ADMIN.equals(d.rol) && !Roles.ADMIN.equals(d.rol)) {
            return "El rol debe ser SUPER_ADMIN o ADMIN";
        }
        boolean traePassword = d.password != null && !d.password.isEmpty();
        if ((passwordObligatoria || traePassword) && (d.password == null || d.password.length() < 6)) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        return null;
    }

    private int numero(String texto, int porDefecto) {
        try {
            int valor = Integer.parseInt(texto);
            return valor < 1 ? porDefecto : valor;
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}