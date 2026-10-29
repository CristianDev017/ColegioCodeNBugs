package service;

import dao.UsuarioOperativoDao;
import dto.PeticionUsuarioOperativo;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.OpcionEmpleado;
import model.UsuarioOperativo;
import util.PasswordService;

import java.sql.SQLException;
import java.util.List;

public class UsuarioOperativoService {

    private final UsuarioOperativoDao usuarioDao = new UsuarioOperativoDao();

    public List<UsuarioOperativo> listar(String buscar, String rol, String estado, int pagina, int tamano)
            throws SQLException {
        return usuarioDao.listar(buscar, rol, estado, pagina, tamano);
    }

    public int contar(String buscar, String rol, String estado) throws SQLException {
        return usuarioDao.contar(buscar, rol, estado);
    }

    public UsuarioOperativo obtener(int id) throws SQLException, NoEncontradoException {
        UsuarioOperativo usuario = usuarioDao.buscarPorId(id);
        if (usuario == null) {
            throw new NoEncontradoException("El usuario no existe");
        }
        return usuario;
    }

    public List<OpcionEmpleado> empleadosDisponibles(String rol) throws SQLException, ServicioException {
        if (rol == null || !PeticionUsuarioOperativo.ROLES.contains(rol)) {
            throw new DatosInvalidosException("Debes elegir un rol válido");
        }
        return usuarioDao.empleadosDisponibles(puestoA(rol), puestoB(rol));
    }

    public UsuarioOperativo crear(PeticionUsuarioOperativo datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del usuario");
        }
        datos.validar();

        if (usuarioDao.existeUsername(datos.getUsername())) {
            throw new ConflictoException("Ese nombre de usuario ya está en uso");
        }
        if (usuarioDao.existeCorreo(datos.getCorreo(), 0)) {
            throw new ConflictoException("Ese correo ya está en uso");
        }

        Integer idRol = usuarioDao.idRol(datos.getRol());
        if (idRol == null) {
            throw new ConflictoException("El rol " + datos.getRol() + " no existe en la base de datos");
        }

        String[] empleado = usuarioDao.datosEmpleado(datos.getIdEmpleado());
        if (empleado == null) {
            throw new NoEncontradoException("El empleado no existe");
        }
        if (!empleado[1].equals("ACTIVO")) {
            throw new ConflictoException("El empleado está inactivo");
        }
        if (empleado[2].equals("1")) {
            throw new ConflictoException("El empleado ya tiene una cuenta de usuario");
        }
        if (!empleado[0].equals(puestoA(datos.getRol())) && !empleado[0].equals(puestoB(datos.getRol()))) {
            throw new ConflictoException("El puesto del empleado no corresponde al rol " + datos.getRol());
        }

        int id = usuarioDao.crear(datos, idRol, PasswordService.encriptar(datos.getPassword()));
        if (id == -1) {
            throw new ConflictoException("El empleado ya tiene una cuenta de usuario");
        }
        return obtener(id);
    }

    public UsuarioOperativo editar(int id, PeticionUsuarioOperativo datos) throws SQLException, ServicioException {
        obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del usuario");
        }
        datos.validarEdicion();
        if (usuarioDao.existeCorreo(datos.getCorreo(), id)) {
            throw new ConflictoException("Ese correo ya está en uso");
        }
        String password = datos.traePassword() ? PasswordService.encriptar(datos.getPassword()) : null;
        usuarioDao.actualizar(id, datos.getCorreo(), password);
        return obtener(id);
    }

    public UsuarioOperativo cambiarEstado(int id, boolean activar) throws SQLException, ServicioException {
        UsuarioOperativo actual = obtener(id);
        String nuevo = activar ? "ACTIVO" : "INACTIVO";
        if (actual.getEstado().equals(nuevo)) {
            throw new ConflictoException("El usuario ya está " + nuevo.toLowerCase());
        }
        usuarioDao.cambiarEstado(id, nuevo);
        return obtener(id);
    }

    private String puestoA(String rol) {
        switch (rol) {
            case "SECRETARIA":
                return "Administrativo";
            case "MAESTRO":
                return "Maestro";
            case "BIBLIOTECARIO":
                return "Biblioteca";
            default:
                return "Mantenimiento";
        }
    }

    private String puestoB(String rol) {
        return rol.equals("MANTENIMIENTO") ? "Limpieza" : puestoA(rol);
    }
}