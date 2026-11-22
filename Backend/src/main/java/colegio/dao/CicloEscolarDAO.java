
package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.CicloEscolar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CicloEscolarDAO {

    private static final String CONSULTA =
            "SELECT c.id_ciclo, c.anio, c.fecha_inicio, c.fecha_fin, " +
                    "c.estado, COUNT(cg.id_grado) AS cantidad_grados " +
                    "FROM ciclo_escolar c " +
                    "LEFT JOIN ciclo_grado cg ON c.id_ciclo = cg.id_ciclo " +
                    "AND cg.habilitado = 1 ";

    public List<CicloEscolar> listar() throws SQLException {
        List<CicloEscolar> ciclos = new ArrayList<>();

        String sql = CONSULTA +
                "GROUP BY c.id_ciclo, c.anio, c.fecha_inicio, c.fecha_fin, c.estado " +
                "ORDER BY c.anio DESC";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ciclos.add(convertir(rs));
                }
            }
        }

        return ciclos;
    }

    public CicloEscolar buscarPorId(int id) throws SQLException {
        String sql = CONSULTA +
                "WHERE c.id_ciclo = ? " +
                "GROUP BY c.id_ciclo, c.anio, c.fecha_inicio, c.fecha_fin, c.estado";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertir(rs);
                }
            }
        }

        return null;
    }

    public boolean existeAnio(int anio) throws SQLException {
        String sql = "SELECT 1 FROM ciclo_escolar WHERE anio = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void crear(int anio, Date inicio, Date fin) throws SQLException {
        String sql = "INSERT INTO ciclo_escolar " +
                "(anio, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, ?, 'PLANIFICADO')";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setDate(2, inicio);
            ps.setDate(3, fin);
            ps.executeUpdate();
        }
    }

    public List<Integer> listarGrados(int idCiclo) throws SQLException {
        List<Integer> grados = new ArrayList<>();

        String sql = "SELECT id_grado FROM ciclo_grado " +
                "WHERE id_ciclo = ? AND habilitado = 1 ORDER BY id_grado";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grados.add(rs.getInt("id_grado"));
                }
            }
        }

        return grados;
    }

    public String buscarEstado(Connection con, int idCiclo) throws SQLException {
        String sql = "SELECT estado FROM ciclo_escolar " +
                "WHERE id_ciclo = ? FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        }

        return null;
    }

    public boolean gradoActivo(Connection con, int idGrado) throws SQLException {
        String sql = "SELECT 1 FROM grado " +
                "WHERE id_grado = ? AND activo = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrado);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean gradoAsignado(Connection con, int idCiclo, int idGrado)
            throws SQLException {

        String sql = "SELECT 1 FROM ciclo_grado " +
                "WHERE id_ciclo = ? AND id_grado = ? AND habilitado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void agregarGrado(Connection con, int idCiclo, int idGrado)
            throws SQLException {

        String sql = "INSERT INTO ciclo_grado " +
                "(id_ciclo, id_grado, habilitado) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE habilitado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);
            ps.executeUpdate();
        }
    }

    public boolean quitarGrado(Connection con, int idCiclo, int idGrado)
            throws SQLException {

        String sql = "UPDATE ciclo_grado SET habilitado = 0 " +
                "WHERE id_ciclo = ? AND id_grado = ? AND habilitado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);

            return ps.executeUpdate() > 0;
        }
    }

    public String buscarCicloActivo() throws SQLException {
        String sql = "SELECT anio FROM ciclo_escolar " +
                "WHERE estado = 'ACTIVO' LIMIT 1";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("anio");
                }
            }
        }

        return null;
    }

    public int contarGrados(int idCiclo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ciclo_grado " +
                "WHERE id_ciclo = ? AND habilitado = 1";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public boolean cambiarEstado(int idCiclo, String estado)
            throws SQLException {

        String sql = "UPDATE ciclo_escolar SET estado = ? WHERE id_ciclo = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idCiclo);

            return ps.executeUpdate() > 0;
        }
    }

    private CicloEscolar convertir(ResultSet rs) throws SQLException {
        return new CicloEscolar(
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                rs.getDate("fecha_inicio"),
                rs.getDate("fecha_fin"),
                rs.getString("estado"),
                rs.getInt("cantidad_grados")
        );
    }
}
