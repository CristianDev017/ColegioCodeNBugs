package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Carrera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarreraDAO {

    public List<Carrera> listar() throws SQLException {

        List<Carrera> carreras = new ArrayList<>();

        String sql = "SELECT * FROM carrera";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Carrera carrera = new Carrera(
                        rs.getInt("id_carrera"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("activo")
                );

                carreras.add(carrera);
            }

        }

        return carreras;
    }

    public void crear(Carrera carrera) throws SQLException {

        String sql = "INSERT INTO carrera (nombre, descripcion) VALUES (?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());

            ps.executeUpdate();
        }

    }

    public boolean editar(Carrera carrera) throws SQLException {

        String sql = "UPDATE carrera SET nombre = ?, descripcion = ? WHERE id_carrera = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());
            ps.setInt(3, carrera.getIdCarrera());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idCarrera, boolean activo) throws SQLException {

        String sql = "UPDATE carrera SET activo = ? WHERE id_carrera = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idCarrera);

            return ps.executeUpdate() > 0;
        }
    }


    public boolean existeNombre(String nombre) throws SQLException {

        String sql = "SELECT id_carrera FROM carrera WHERE nombre = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeNombreEnOtraCarrera(String nombre, int idCarrera) throws SQLException {

        String sql = "SELECT id_carrera FROM carrera WHERE nombre = ? AND id_carrera <> ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idCarrera);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public Carrera buscarPorId(int idCarrera) throws SQLException {

        String sql = "SELECT * FROM carrera WHERE id_carrera = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCarrera);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Carrera(
                            rs.getInt("id_carrera"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }





}