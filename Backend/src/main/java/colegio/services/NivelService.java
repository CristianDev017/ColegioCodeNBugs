package colegio.services;

import colegio.dao.NivelDAO;
import colegio.model.Nivel;

import java.sql.SQLException;
import java.util.List;

public class NivelService {

    private NivelDAO dao = new NivelDAO();

    public List<Nivel> listar() throws SQLException {
        return dao.listar();
    }
}
