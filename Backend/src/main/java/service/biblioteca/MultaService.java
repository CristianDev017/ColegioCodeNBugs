package service.biblioteca;

import dao.biblioteca.MultaDao;
import exception.ConflictoException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.biblioteca.Multa;

import java.sql.SQLException;
import java.util.List;

public class MultaService {

    private final MultaDao multaDao = new MultaDao();

    public List<Multa> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        return multaDao.listar(buscar, estado, pagina, tamano);
    }

    public int contar(String buscar, String estado) throws SQLException {
        return multaDao.contar(buscar, estado);
    }

    public Multa obtener(int id) throws SQLException, NoEncontradoException {
        Multa multa = multaDao.buscarPorId(id);
        if (multa == null) {
            throw new NoEncontradoException("La multa no existe");
        }
        return multa;
    }

    public Multa pagar(int id) throws SQLException, ServicioException {
        Multa multa = obtener(id);
        if (!multa.getEstado().equals("PENDIENTE")) {
            throw new ConflictoException("La multa ya fue pagada");
        }
        multaDao.marcarPagada(id);
        return obtener(id);
    }
}