package service.academico;

import dao.academico.GradoDao;
import dao.academico.SeccionDao;
import dto.academico.PeticionSeccion;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.academico.Grado;
import model.academico.Seccion;

import java.sql.SQLException;
import java.util.List;

public class SeccionService {

    private final SeccionDao seccionDao = new SeccionDao();
    private final GradoDao gradoDao = new GradoDao();

    public List<Seccion> listar(int idGrado, int pagina, int tamano) throws SQLException {
        return seccionDao.listar(idGrado, pagina, tamano);
    }

    public int contar(int idGrado) throws SQLException {
        return seccionDao.contar(idGrado);
    }

    public Seccion obtener(int id) throws SQLException, NoEncontradoException {
        Seccion seccion = seccionDao.buscarPorId(id);
        if (seccion == null) {
            throw new NoEncontradoException("La sección no existe");
        }
        return seccion;
    }

    public Seccion crear(PeticionSeccion datos) throws SQLException, ServicioException {
        Grado grado = revisar(datos, 0);
        int cupo = datos.getCupo() != null ? datos.getCupo() : grado.getCupoDefault();
        int id = seccionDao.crear(datos.getIdGrado(), datos.getNombre(), cupo);
        return obtener(id);
    }

    public Seccion editar(int id, PeticionSeccion datos) throws SQLException, ServicioException {
        Seccion actual = obtener(id);
        Grado grado = revisar(datos, id);
        if (datos.getIdGrado() != actual.getIdGrado()) {
            throw new ConflictoException("No se puede cambiar el grado de una sección");
        }
        int cupo = datos.getCupo() != null ? datos.getCupo() : grado.getCupoDefault();
        int inscritos = seccionDao.contarInscritosActivos(id);
        if (cupo < inscritos) {
            throw new ConflictoException("El cupo no puede ser menor a los " + inscritos
                    + " estudiantes inscritos");
        }
        seccionDao.actualizar(id, datos.getNombre(), cupo);
        return obtener(id);
    }

    public Seccion activar(int id) throws SQLException, ServicioException {
        Seccion seccion = obtener(id);
        Grado grado = gradoDao.buscarPorId(seccion.getIdGrado());
        if (grado == null || !grado.isActivo()) {
            throw new ConflictoException("No se puede activar: el grado está inactivo");
        }
        seccionDao.cambiarActivo(id, true);
        return obtener(id);
    }

    public Seccion desactivar(int id) throws SQLException, ServicioException {
        obtener(id);
        int inscritos = seccionDao.contarInscritosActivos(id);
        if (inscritos > 0) {
            throw new ConflictoException("No se puede desactivar: tiene " + inscritos
                    + " estudiantes inscritos en el ciclo activo");
        }
        seccionDao.cambiarActivo(id, false);
        return obtener(id);
    }

    private Grado revisar(PeticionSeccion datos, int excluirId) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la sección");
        }
        datos.validar();

        Grado grado = gradoDao.buscarPorId(datos.getIdGrado());
        if (grado == null) {
            throw new DatosInvalidosException("El grado no existe");
        }
        if (!grado.isActivo()) {
            throw new DatosInvalidosException("El grado está inactivo");
        }
        if (seccionDao.existeDuplicado(datos.getIdGrado(), datos.getNombre(), excluirId)) {
            throw new ConflictoException("Ya existe una sección con ese nombre en el grado");
        }
        return grado;
    }
}