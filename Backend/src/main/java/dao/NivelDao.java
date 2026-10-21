package dao;

import config.DBConnectionSingleton;
import model.Nivel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NivelDao {

    public List<Nivel> listar() throws SQLException {
        List<Nivel> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id_nivel, nombre FROM nivel ORDER BY id_nivel");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Nivel(rs.getInt("id_nivel"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    public Nivel buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id_nivel, nombre FROM nivel WHERE id_nivel = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Nivel(rs.getInt("id_nivel"), rs.getString("nombre")) : null;
            }
        }
    }
}