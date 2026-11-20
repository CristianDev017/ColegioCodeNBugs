package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Nivel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NivelDAO {

    public List<Nivel> listar() throws SQLException {
        List<Nivel> niveles = new ArrayList<>();

        String sql = "SELECT id_nivel, nombre FROM nivel ORDER BY id_nivel";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    niveles.add(new Nivel(
                            rs.getInt("id_nivel"),
                            rs.getString("nombre")
                    ));
                }
            }
        }

        return niveles;
    }
}
