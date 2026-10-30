package service.biblioteca;

import dao.biblioteca.LibroDao;
import dto.biblioteca.PeticionLibro;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.biblioteca.Libro;

import java.sql.SQLException;
import java.util.List;

public class LibroService {

    private final LibroDao libroDao = new LibroDao();

    public List<Libro> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        return libroDao.listar(buscar, estado, pagina, tamano);
    }

    public int contar(String buscar, String estado) throws SQLException {
        return libroDao.contar(buscar, estado);
    }

    public Libro obtener(int id) throws SQLException, NoEncontradoException {
        Libro libro = libroDao.buscarPorId(id);
        if (libro == null) {
            throw new NoEncontradoException("El libro no existe");
        }
        return libro;
    }

    public Libro crear(PeticionLibro datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del libro");
        }
        datos.validar();
        if (libroDao.existe(datos.getTitulo(), datos.getAutor(), datos.getEditorial(), 0)) {
            throw new ConflictoException("Ya existe un libro con ese título, autor y editorial");
        }
        int id = libroDao.crear(datos);
        return obtener(id);
    }

    public Libro editar(int id, PeticionLibro datos) throws SQLException, ServicioException {
        Libro actual = obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del libro");
        }
        datos.validar();
        if (libroDao.existe(datos.getTitulo(), datos.getAutor(), datos.getEditorial(), id)) {
            throw new ConflictoException("Ya existe otro libro con ese título, autor y editorial");
        }
        int prestados = actual.getCantidadTotal() - actual.getCantidadDisponible();
        if (datos.getCantidadTotal() < prestados) {
            throw new ConflictoException("Hay " + prestados + " ejemplares prestados; la cantidad total no puede ser menor");
        }
        libroDao.actualizar(id, datos, datos.getCantidadTotal() - prestados);
        return obtener(id);
    }

    public Libro cambiarEstado(int id, boolean activar) throws SQLException, ServicioException {
        Libro actual = obtener(id);
        if (actual.isActivo() == activar) {
            throw new ConflictoException("El libro ya está " + (activar ? "activo" : "inactivo"));
        }
        if (!activar && libroDao.prestamosActivos(id) > 0) {
            throw new ConflictoException("No se puede desactivar: tiene préstamos activos");
        }
        libroDao.cambiarActivo(id, activar);
        return obtener(id);
    }
}