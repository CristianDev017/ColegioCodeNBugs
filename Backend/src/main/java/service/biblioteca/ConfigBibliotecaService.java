package service.biblioteca;

import dao.biblioteca.ConfigBibliotecaDao;
import dto.biblioteca.PeticionConfigBiblioteca;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.biblioteca.ConfigBiblioteca;

import java.sql.SQLException;

public class ConfigBibliotecaService {

    private final ConfigBibliotecaDao configDao = new ConfigBibliotecaDao();

    public ConfigBiblioteca obtener() throws SQLException, NoEncontradoException {
        ConfigBiblioteca config = configDao.obtener();
        if (config == null) {
            throw new NoEncontradoException("No hay configuración de biblioteca");
        }
        return config;
    }

    public ConfigBiblioteca guardar(PeticionConfigBiblioteca datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan los datos de la configuración");
        }
        datos.validar();
        obtener();
        configDao.actualizar(datos);
        return obtener();
    }
}
