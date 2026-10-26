package service;

import dao.TarifaDao;
import dto.PeticionTarifa;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Tarifa;

import java.sql.SQLException;
import java.util.List;

public class TarifaService {

    private final TarifaDao tarifaDao = new TarifaDao();

    public List<Tarifa> listar(int idCiclo, int pagina, int tamano) throws SQLException {
        return tarifaDao.listar(idCiclo, pagina, tamano);
    }

    public int contar(int idCiclo) throws SQLException {
        return tarifaDao.contar(idCiclo);
    }

    public Tarifa obtener(int id) throws SQLException, NoEncontradoException {
        Tarifa tarifa = tarifaDao.buscarPorId(id);
        if (tarifa == null) {
            throw new NoEncontradoException("La tarifa no existe");
        }
        return tarifa;
    }

    public Tarifa crear(PeticionTarifa datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la tarifa");
        }
        datos.validar();

        String estado = tarifaDao.estadoCiclo(datos.getIdCiclo());
        if (estado == null) {
            throw new NoEncontradoException("El año lectivo no existe");
        }
        if (estado.equals("CERRADO")) {
            throw new ConflictoException("El año lectivo está cerrado, no se pueden definir tarifas");
        }

        Boolean gradoActivo = tarifaDao.gradoActivo(datos.getIdGrado());
        if (gradoActivo == null) {
            throw new NoEncontradoException("El grado no existe");
        }
        if (!gradoActivo) {
            throw new ConflictoException("El grado está inactivo");
        }
        if (tarifaDao.existe(datos.getIdGrado(), datos.getIdCiclo(), 0)) {
            throw new ConflictoException("Ya existe una tarifa para ese grado en ese año lectivo");
        }

        int id = tarifaDao.crear(datos);
        return obtener(id);
    }

    public Tarifa editar(int id, PeticionTarifa datos) throws SQLException, ServicioException {
        Tarifa actual = obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la tarifa");
        }
        datos.validarMontos();
        if ("CERRADO".equals(tarifaDao.estadoCiclo(actual.getIdCiclo()))) {
            throw new ConflictoException("El año lectivo está cerrado, no se pueden modificar las tarifas");
        }
        tarifaDao.actualizar(id, datos);
        return obtener(id);
    }
}