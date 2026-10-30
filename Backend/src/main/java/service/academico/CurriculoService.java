package service.academico;

import dao.academico.CurriculoDao;
import dao.academico.CursoDao;
import dto.academico.PeticionCurriculoCurso;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.academico.Curriculo;
import model.academico.Curso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurriculoService {

    private final CurriculoDao curriculoDao = new CurriculoDao();
    private final CursoDao cursoDao = new CursoDao();

    public Curriculo consultar(int idCiclo, int idGrado) throws SQLException, ServicioException {
        if (idCiclo < 1 || idGrado < 1) {
            throw new DatosInvalidosException("Debes elegir un año lectivo y un grado");
        }
        if (curriculoDao.estadoCiclo(idCiclo) == null) {
            throw new NoEncontradoException("El año lectivo no existe");
        }
        if (curriculoDao.gradoActivo(idGrado) == null) {
            throw new NoEncontradoException("El grado no existe");
        }
        int id = curriculoDao.buscarId(idCiclo, idGrado);
        List<Curso> cursos = id == 0 ? new ArrayList<>() : curriculoDao.listarCursos(id);
        return new Curriculo(id, idCiclo, idGrado, cursos);
    }

    public Curriculo agregarCurso(PeticionCurriculoCurso datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del currículo");
        }
        datos.validar();

        String estado = curriculoDao.estadoCiclo(datos.getIdCiclo());
        if (estado == null) {
            throw new NoEncontradoException("El año lectivo no existe");
        }
        if (estado.equals("CERRADO")) {
            throw new ConflictoException("El año lectivo está cerrado, no se puede modificar el currículo");
        }

        Boolean gradoActivo = curriculoDao.gradoActivo(datos.getIdGrado());
        if (gradoActivo == null) {
            throw new NoEncontradoException("El grado no existe");
        }
        if (!gradoActivo) {
            throw new ConflictoException("El grado está inactivo");
        }

        Curso curso = cursoDao.buscarPorId(datos.getIdCurso());
        if (curso == null) {
            throw new NoEncontradoException("El curso no existe");
        }
        if (!curso.isActivo()) {
            throw new ConflictoException("El curso está inactivo");
        }

        int idCurriculo = curriculoDao.buscarId(datos.getIdCiclo(), datos.getIdGrado());
        if (idCurriculo == 0) {
            idCurriculo = curriculoDao.crear(datos.getIdCiclo(), datos.getIdGrado());
        }
        if (curriculoDao.existeCurso(idCurriculo, datos.getIdCurso())) {
            throw new ConflictoException("El curso ya está en el currículo");
        }
        curriculoDao.agregarCurso(idCurriculo, datos.getIdCurso());
        return consultar(datos.getIdCiclo(), datos.getIdGrado());
    }

    public Curriculo quitarCurso(int idCurriculo, int idCurso) throws SQLException, ServicioException {
        int[] ubicacion = curriculoDao.buscarCicloGrado(idCurriculo);
        if (ubicacion == null) {
            throw new NoEncontradoException("El currículo no existe");
        }
        String estado = curriculoDao.estadoCiclo(ubicacion[0]);
        if ("CERRADO".equals(estado)) {
            throw new ConflictoException("El año lectivo está cerrado, no se puede modificar el currículo");
        }
        if (!curriculoDao.existeCurso(idCurriculo, idCurso)) {
            throw new NoEncontradoException("El curso no está en el currículo");
        }
        curriculoDao.quitarCurso(idCurriculo, idCurso);
        return consultar(ubicacion[0], ubicacion[1]);
    }
}