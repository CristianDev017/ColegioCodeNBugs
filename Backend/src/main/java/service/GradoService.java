package service;

import dao.GradoDao;
import dao.NivelDao;
import dto.PeticionGrado;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Grado;
import model.Nivel;
import dto.PeticionCupo;

import java.sql.SQLException;
import java.util.List;

public class GradoService {

    private final GradoDao gradoDao = new GradoDao();
    private final NivelDao nivelDao = new NivelDao();

    public List<Grado> listar(String buscar, int idNivel, int pagina, int tamano) throws SQLException {
        return gradoDao.listar(buscar, idNivel, pagina, tamano);
    }

    public int contar(String buscar, int idNivel) throws SQLException {
        return gradoDao.contar(buscar, idNivel);
    }

    public Grado obtener(int id) throws SQLException, NoEncontradoException {
        Grado grado = gradoDao.buscarPorId(id);
        if (grado == null) {
            throw new NoEncontradoException("El grado no existe");
        }
        return grado;
    }

    public Grado crear(PeticionGrado datos) throws SQLException, ServicioException {
        revisar(datos, 0);
        int id = gradoDao.crear(datos);
        return obtener(id);
    }

    public Grado editar(int id, PeticionGrado datos) throws SQLException, ServicioException {
        obtener(id);
        revisar(datos, id);
        gradoDao.actualizar(id, datos);
        return obtener(id);
    }

    public Grado activar(int id) throws SQLException, ServicioException {
        obtener(id);
        gradoDao.cambiarActivo(id, true);
        return obtener(id);
    }

    public Grado desactivar(int id) throws SQLException, ServicioException {
        obtener(id);
        int inscritos = gradoDao.contarInscritosActivos(id);
        if (inscritos > 0) {
            throw new ConflictoException("No se puede desactivar: tiene " + inscritos
                    + " estudiantes inscritos en el ciclo activo");
        }
        gradoDao.cambiarActivo(id, false);
        return obtener(id);
    }

    private void revisar(PeticionGrado datos, int excluirId) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del grado");
        }
        datos.validar();

        Nivel nivel = nivelDao.buscarPorId(datos.getIdNivel());
        if (nivel == null) {
            throw new DatosInvalidosException("El nivel no existe");
        }

        boolean diversificado = nivel.getNombre().equals("Diversificado");
        if (diversificado && datos.getIdCarrera() == null) {
            throw new DatosInvalidosException("Un grado de Diversificado necesita una carrera");
        }
        if (!diversificado && datos.getIdCarrera() != null) {
            throw new DatosInvalidosException("Solo los grados de Diversificado llevan carrera");
        }

        if (gradoDao.existeDuplicado(datos.getIdNivel(), datos.getIdCarrera(), datos.getNombre(), excluirId)) {
            throw new ConflictoException("Ya existe un grado con ese nombre en el nivel");
        }
    }

    public Grado cambiarCupo(int id, PeticionCupo datos) throws SQLException, ServicioException {
        obtener(id);
        if (datos == null || datos.getCupoDefault() == null || datos.getCupoDefault() < 1) {
            throw new DatosInvalidosException("El cupo debe ser mayor a cero");
        }
        gradoDao.cambiarCupoDefault(id, datos.getCupoDefault());
        return obtener(id);
    }
}