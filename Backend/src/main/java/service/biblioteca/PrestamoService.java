package service.biblioteca;

import dao.biblioteca.ConfigBibliotecaDao;
import dao.biblioteca.LibroDao;
import dao.biblioteca.MultaDao;
import dao.biblioteca.PrestamoDao;
import dto.biblioteca.PeticionDevolucion;
import dto.biblioteca.PeticionPrestamo;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.biblioteca.ConfigBiblioteca;
import model.biblioteca.DevolucionResultado;
import model.biblioteca.Libro;
import model.biblioteca.Multa;
import model.biblioteca.OpcionBiblioteca;
import model.biblioteca.Prestamo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PrestamoService {

    private final PrestamoDao prestamoDao = new PrestamoDao();
    private final LibroDao libroDao = new LibroDao();
    private final ConfigBibliotecaDao configDao = new ConfigBibliotecaDao();
    private final MultaDao multaDao = new MultaDao();

    public List<Prestamo> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        return prestamoDao.listar(buscar, estado, pagina, tamano);
    }

    public int contar(String buscar, String estado) throws SQLException {
        return prestamoDao.contar(buscar, estado);
    }

    public Prestamo obtener(int id) throws SQLException, NoEncontradoException {
        Prestamo prestamo = prestamoDao.buscarPorId(id);
        if (prestamo == null) {
            throw new NoEncontradoException("El préstamo no existe");
        }
        return prestamo;
    }

    public List<OpcionBiblioteca> opcionesLibros(String buscar) throws SQLException {
        return prestamoDao.opcionesLibros(buscar);
    }

    public List<OpcionBiblioteca> opcionesSolicitantes(String tipo, String buscar)
            throws SQLException, DatosInvalidosException {
        if ("ESTUDIANTE".equals(tipo)) {
            return prestamoDao.opcionesEstudiantes(buscar);
        }
        if ("MAESTRO".equals(tipo)) {
            return prestamoDao.opcionesEmpleados(buscar, true);
        }
        if ("PERSONAL".equals(tipo)) {
            return prestamoDao.opcionesEmpleados(buscar, false);
        }
        throw new DatosInvalidosException("El tipo de solicitante no es válido");
    }

    public Prestamo registrar(PeticionPrestamo datos, int idUsuario) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan los datos del préstamo");
        }
        datos.validar();

        Libro libro = libroDao.buscarPorId(datos.getIdLibro());
        if (libro == null) {
            throw new NoEncontradoException("El libro no existe");
        }
        if (!libro.isActivo()) {
            throw new ConflictoException("El libro está inactivo");
        }
        if (libro.getCantidadDisponible() <= 0) {
            throw new ConflictoException("No hay ejemplares disponibles de este libro");
        }

        Integer idEstudiante = null;
        Integer idEmpleado = null;
        if (datos.getTipoSolicitante().equals("ESTUDIANTE")) {
            if (!prestamoDao.estudianteActivo(datos.getIdSolicitante())) {
                throw new ConflictoException("El estudiante no existe o no está activo");
            }
            idEstudiante = datos.getIdSolicitante();
        } else {
            if (!prestamoDao.empleadoActivo(datos.getIdSolicitante())) {
                throw new ConflictoException("El empleado no existe o no está activo");
            }
            idEmpleado = datos.getIdSolicitante();
        }

        ConfigBiblioteca config = configDao.obtener();
        if (config == null) {
            throw new ConflictoException("Falta configurar los parámetros de biblioteca");
        }

        int id = prestamoDao.registrar(datos.getIdLibro(), idEstudiante, idEmpleado, idUsuario,
                config.getDiasPrestamo());
        if (id == -1) {
            throw new ConflictoException("No hay ejemplares disponibles de este libro");
        }
        return obtener(id);
    }

    public DevolucionResultado devolver(int id, PeticionDevolucion datos) throws SQLException, ServicioException {
        Prestamo prestamo = obtener(id);
        if (!prestamo.getEstado().equals("ACTIVO")) {
            throw new ConflictoException("El préstamo ya fue devuelto");
        }
        String observacion = null;
        if (datos != null) {
            datos.validar();
            observacion = datos.getObservacion();
        }

        int dias = prestamo.getDiasAtraso();
        BigDecimal monto = BigDecimal.ZERO;
        if (dias > 0) {
            ConfigBiblioteca config = configDao.obtener();
            if (config == null) {
                throw new ConflictoException("Falta configurar los parámetros de biblioteca");
            }
            monto = config.getMultaPorDia().multiply(BigDecimal.valueOf(dias));
        }

        boolean devuelto = prestamoDao.devolver(id, prestamo.getIdLibro(), observacion, dias, monto);
        if (!devuelto) {
            throw new ConflictoException("El préstamo ya fue devuelto");
        }
        Multa multa = dias > 0 ? multaDao.buscarPorPrestamo(id) : null;
        return new DevolucionResultado(obtener(id), multa);
    }
}
