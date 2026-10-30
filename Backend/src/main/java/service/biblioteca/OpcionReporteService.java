package service.reportes;

import dao.reportes.OpcionReporteDao;
import model.reportes.OpcionReporte;

import java.sql.SQLException;
import java.util.List;

public class OpcionReporteService {

    private final OpcionReporteDao dao = new OpcionReporteDao();

    public List<OpcionReporte> ciclos() throws SQLException {
        return dao.ciclos();
    }

    public List<OpcionReporte> grados() throws SQLException {
        return dao.grados();
    }

    public List<OpcionReporte> secciones(int idGrado) throws SQLException {
        return dao.secciones(idGrado);
    }

    public List<OpcionReporte> estudiantes() throws SQLException {
        return dao.estudiantes();
    }

    public String textoCiclo(int id) throws SQLException {
        return dao.textoCiclo(id);
    }

    public String textoGrado(int id) throws SQLException {
        return dao.textoGrado(id);
    }

    public String textoSeccion(int id) throws SQLException {
        return dao.textoSeccion(id);
    }

    public String textoEstudiante(int id) throws SQLException {
        return dao.textoEstudiante(id);
    }
}