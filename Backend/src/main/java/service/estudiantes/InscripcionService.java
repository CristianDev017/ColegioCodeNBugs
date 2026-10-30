package service.estudiantes;

import dao.estudiantes.InscripcionDao;
import dto.estudiantes.PeticionInscripcion;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.estudiantes.InscripcionDetalle;
import model.academico.SeccionDisponible;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscripcionService {

    private final InscripcionDao inscripcionDao = new InscripcionDao();

    public List<InscripcionDetalle> listar(String buscar, int idSeccion, int pagina, int tamano) throws SQLException {
        int idCiclo = inscripcionDao.cicloActivo();
        if (idCiclo == 0) {
            return new ArrayList<>();
        }
        return inscripcionDao.listar(idCiclo, buscar, idSeccion, pagina, tamano);
    }

    public int contar(String buscar, int idSeccion) throws SQLException {
        int idCiclo = inscripcionDao.cicloActivo();
        if (idCiclo == 0) {
            return 0;
        }
        return inscripcionDao.contar(idCiclo, buscar, idSeccion);
    }

    public List<SeccionDisponible> seccionesDisponibles() throws SQLException {
        int idCiclo = inscripcionDao.cicloActivo();
        if (idCiclo == 0) {
            return new ArrayList<>();
        }
        return inscripcionDao.seccionesDisponibles(idCiclo);
    }

    public InscripcionDetalle inscribir(PeticionInscripcion datos, int idUsuario) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la inscripción");
        }
        datos.validar();

        int idCiclo = inscripcionDao.cicloActivo();
        if (idCiclo == 0) {
            throw new ConflictoException("No hay un año lectivo activo para inscribir");
        }

        String estado = inscripcionDao.estadoEstudiante(datos.getIdEstudiante());
        if (estado == null) {
            throw new NoEncontradoException("El estudiante no existe");
        }
        if (!estado.equals("ACTIVO")) {
            throw new ConflictoException("Solo se puede inscribir a estudiantes activos");
        }

        int[] seccion = inscripcionDao.infoSeccion(datos.getIdSeccion());
        if (seccion == null) {
            throw new NoEncontradoException("La sección no existe");
        }
        if (seccion[1] == 0 || seccion[2] == 0) {
            throw new ConflictoException("La sección o su grado están inactivos");
        }
        if (!inscripcionDao.tieneCurriculo(idCiclo, seccion[3])) {
            throw new ConflictoException("El grado todavía no tiene currículo definido para este año lectivo");
        }
        if (inscripcionDao.existeInscripcion(datos.getIdEstudiante(), idCiclo)) {
            throw new ConflictoException("El estudiante ya está inscrito en este año lectivo");
        }
        if (inscripcionDao.contarInscritos(datos.getIdSeccion(), idCiclo) >= seccion[0]) {
            throw new ConflictoException("La sección ya no tiene cupo disponible");
        }

        int id = inscripcionDao.crear(datos.getIdEstudiante(), idCiclo, datos.getIdSeccion(), idUsuario);
        return inscripcionDao.buscarPorId(id);
    }
}