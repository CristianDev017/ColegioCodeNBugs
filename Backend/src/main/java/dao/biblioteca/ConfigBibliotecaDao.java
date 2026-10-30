package dao.biblioteca;

import config.DBConnectionSingleton;
import dto.biblioteca.PeticionConfigBiblioteca;
import model.biblioteca.ConfigBiblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigBibliotecaDao {

    public ConfigBiblioteca obtener() throws SQLException {
        String sql = "SELECT dias_prestamo, multa_por_dia FROM config_biblioteca ORDER BY id_config LIMIT 1";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new ConfigBiblioteca(rs.getInt("dias_prestamo"), rs.getBigDecimal("multa_por_dia"));
            }
            return null;
        }
    }

    public void actualizar(PeticionConfigBiblioteca d) throws SQLException {
        String sql = "UPDATE config_biblioteca SET dias_prestamo = ?, multa_por_dia = ?, "
                + "fecha_actualizacion = NOW()";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getDiasPrestamo());
            ps.setBigDecimal(2, d.getMultaPorDia());
            ps.executeUpdate();
        }
    }
}
