package service;

import dao.EmpleadoDao;
import dto.PeticionEmpleado;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Empleado;

import java.sql.SQLException;
import java.util.List;

public class EmpleadoService {

    private final EmpleadoDao empleadoDao = new EmpleadoDao();

    public List<Empleado> listar(String buscar, String puesto, String estado, int pagina, int tamano)
            throws SQLException {
        return empleadoDao.listar(buscar, puesto, estado, pagina, tamano);
    }

    public int contar(String buscar, String puesto, String estado) throws SQLException {
        return empleadoDao.contar(buscar, puesto, estado);
    }

    public Empleado obtener(int id) throws SQLException, NoEncontradoException {
        Empleado empleado = empleadoDao.buscarPorId(id);
        if (empleado == null) {
            throw new NoEncontradoException("El empleado no existe");
        }
        return empleado;
    }

    public Empleado crear(PeticionEmpleado datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del empleado");
        }
        datos.validar();
        if (empleadoDao.existeDpi(datos.getDpi(), 0)) {
            throw new ConflictoException("Ya existe un empleado con ese DPI");
        }
        int id = empleadoDao.crear(datos);
        return obtener(id);
    }

    public Empleado editar(int id, PeticionEmpleado datos) throws SQLException, ServicioException {
        obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del empleado");
        }
        datos.validar();
        if (empleadoDao.existeDpi(datos.getDpi(), id)) {
            throw new ConflictoException("Ya existe otro empleado con ese DPI");
        }
        empleadoDao.actualizar(id, datos);
        return obtener(id);
    }

    public Empleado cambiarEstado(int id, boolean activar) throws SQLException, ServicioException {
        Empleado actual = obtener(id);
        String nuevo = activar ? "ACTIVO" : "INACTIVO";
        if (actual.getEstado().equals(nuevo)) {
            throw new ConflictoException("El empleado ya está " + nuevo.toLowerCase());
        }
        empleadoDao.cambiarEstado(id, nuevo);
        return obtener(id);
    }
}