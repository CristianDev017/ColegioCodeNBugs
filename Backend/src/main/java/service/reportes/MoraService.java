package service.reportes;

import dao.reportes.MoraDao;
import model.reportes.FilaMora;

import java.sql.SQLException;
import java.util.List;

public class MoraService {

    private final MoraDao moraDao = new MoraDao();

    public List<FilaMora> listar(String buscar, String tipo, int pagina, int tamano) throws SQLException {
        return moraDao.listar(buscar, tipo, pagina, tamano);
    }

    public int contar(String buscar, String tipo) throws SQLException {
        return moraDao.contar(buscar, tipo);
    }

    public List<FilaMora> todos(String buscar, String tipo) throws SQLException {
        return moraDao.todos(buscar, tipo);
    }
}