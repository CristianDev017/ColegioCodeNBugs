
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

    // Listado paginado para la tabla de Carreras
    public List<Carrera> listar(int pagina, int tamano) throws SQLException {
        List<Carrera> carreras = new ArrayList<>();

        String sql = "SELECT * FROM carrera " +
                "ORDER BY id_carrera LIMIT ? OFFSET ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    carreras.add(convertir(rs));
                }
            }
        }

        return carreras;
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM carrera";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // Catalogo completo para seleccionar carreras en Grados
    public List<Carrera> listar() throws SQLException {
        List<Carrera> carreras = new ArrayList<>();

        String sql = "SELECT * FROM carrera ORDER BY nombre, id_carrera";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                carreras.add(convertir(rs));
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
        String sql = "UPDATE carrera SET nombre = ?, descripcion = ? " +
                "WHERE id_carrera = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());
            ps.setInt(3, carrera.getIdCarrera());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idCarrera, boolean activo)
            throws SQLException {

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

    public boolean existeNombreEnOtraCarrera(String nombre, int idCarrera)
            throws SQLException {

        String sql = "SELECT id_carrera FROM carrera " +
                "WHERE nombre = ? AND id_carrera <> ?";

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
                    return convertir(rs);
                }
            }
        }

        return null;
    }

    private Carrera convertir(ResultSet rs) throws SQLException {
        return new Carrera(
                rs.getInt("id_carrera"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo")
        );
    }
}
