package colegio.services;

import colegio.conexion.Conexion;
import colegio.dao.UsuarioDAO;
import colegio.dto.PeticionUsuario;
import colegio.dto.PeticionEditarUsuario;
import colegio.dto.RespuestaUsuario;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Usuario;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public List<RespuestaUsuario> listar() throws SQLException {
        List<Usuario> usuarios = dao.listar();
        List<RespuestaUsuario> respuestas = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            respuestas.add(new RespuestaUsuario(usuario));
        }
        return respuestas;
    }

    public RespuestaUsuario buscarPorId(int idUsuario)
            throws SQLException, DatosInvalidosException, NoEncontradoException {
        if (idUsuario <= 0) {
            throw new DatosInvalidosException("Id invalido");
        }

        Usuario usuario = dao.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new NoEncontradoException("Usuario no encontrado");
        }

        return new RespuestaUsuario(usuario);
    }

    public void crear(PeticionUsuario datos)
            throws SQLException, DatosInvalidosException, ConflictoException {
        datos.validar();

        if (!dao.existeRol(datos.getIdRol())) {
            throw new DatosInvalidosException("El rol no existe");
        }

        if (dao.existeUsername(datos.getUsername(), 0)) {
            throw new ConflictoException("El usuario ya existe");
        }
        if (dao.existeCorreo(datos.getCorreo(), 0)) {
            throw new ConflictoException("El correo ya existe");
        }

        String password = Base64.getEncoder().encodeToString(
                datos.getPassword().getBytes(StandardCharsets.UTF_8)
        );

        dao.crear(datos.getIdRol(), datos.getUsername(), datos.getCorreo(), password);
    }

    public void editar(int idUsuario, PeticionEditarUsuario datos)
            throws SQLException, DatosInvalidosException, NoEncontradoException, ConflictoException {
        if (idUsuario <= 0) {
            throw new DatosInvalidosException("Id invalido");
        }

        datos.validar();

        try (Connection con = Conexion.obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                dao.bloquearSuperAdmin(con);

                Usuario usuario = dao.buscarPorId(con, idUsuario);
                if (usuario == null) {
                    throw new NoEncontradoException("Usuario no encontrado");
                }

                if (!dao.existeRol(datos.getIdRol())) {
                    throw new DatosInvalidosException("El rol no existe");
                }

                if (dao.existeUsername(datos.getUsername(), idUsuario)) {
                    throw new ConflictoException("El usuario ya existe");
                }
                if (dao.existeCorreo(datos.getCorreo(), idUsuario)) {
                    throw new ConflictoException("El correo ya existe");
                }

                if (usuario.getRol().equals("SUPER_ADMIN")
                        && usuario.getEstado().equals("ACTIVO")
                        && usuario.getIdRol() != datos.getIdRol()
                        && dao.contarSuperAdminsActivos(con) <= 1) {
                    throw new ConflictoException("No puedes quitar el ultimo super admin");
                }

                dao.editar(con, idUsuario, datos.getIdRol(),
                        datos.getUsername(), datos.getCorreo());

                con.commit();

            } catch (SQLException | DatosInvalidosException
                     | NoEncontradoException | ConflictoException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void cambiarEstado(int idUsuario, String estado)
            throws SQLException, DatosInvalidosException, NoEncontradoException, ConflictoException {
        if (idUsuario <= 0) {
            throw new DatosInvalidosException("Id invalido");
        }

        if (estado == null || (!estado.equals("ACTIVO") && !estado.equals("INACTIVO"))) {
            throw new DatosInvalidosException("Estado invalido");
        }

        try (Connection con = Conexion.obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                dao.bloquearSuperAdmin(con);

                Usuario usuario = dao.buscarPorId(con, idUsuario);
                if (usuario == null) {
                    throw new NoEncontradoException("Usuario no encontrado");
                }

                if (usuario.getRol().equals("SUPER_ADMIN")
                        && usuario.getEstado().equals("ACTIVO")
                        && estado.equals("INACTIVO")
                        && dao.contarSuperAdminsActivos(con) <= 1) {
                    throw new ConflictoException("No puedes desactivar el ultimo super admin");
                }

                dao.cambiarEstado(con, idUsuario, estado);
                con.commit();

            } catch (SQLException | NoEncontradoException | ConflictoException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
