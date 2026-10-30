package dao.finanzas;

import config.DBConnectionSingleton;
import model.finanzas.Solvencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SolvenciaDao {

    private static final String SELECT_BASE = "SELECT so.id_solvencia, so.id_estudiante, e.carnet, "
            + "CONCAT(e.nombres, ' ', e.apellidos) AS estudiante, so.id_ciclo, c.anio, "
            + "(SELECT CONCAT(n.nombre, ' - ', g.nombre) FROM inscripcion i "
            + "JOIN seccion se ON se.id_seccion = i.id_seccion "
            + "JOIN grado g ON g.id_grado = se.id_grado "
            + "JOIN nivel n ON n.id_nivel = g.id_nivel "
            + "WHERE i.id_estudiante = so.id_estudiante AND i.id_ciclo = so.id_ciclo AND i.estado = 'ACTIVA' "
            + "LIMIT 1) AS grado, "
            + "so.fecha_emision, u.username AS usuario "
            + "FROM solvencia so "
            + "JOIN estudiante e ON e.id_estudiante = so.id_estudiante "
            + "JOIN ciclo_escolar c ON c.id_ciclo = so.id_ciclo "
            + "JOIN usuario u ON u.id_usuario = so.id_usuario ";

    public List<Solvencia> listar(String buscar, int idCiclo, int pagina, int tamano) throws SQLException {
        List<Solvencia> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE (? = 0 OR so.id_ciclo = ?) "
                + "AND (e.carnet LIKE ? OR CONCAT(e.nombres, ' ', e.apellidos) LIKE ?) "
                + "ORDER BY so.id_solvencia DESC LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
            ps.setString(3, "%" + buscar + "%");
            ps.setString(4, "%" + buscar + "%");
            ps.setInt(5, tamano);
            ps.setInt(6, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(String buscar, int idCiclo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM solvencia so JOIN estudiante e ON e.id_estudiante = so.id_estudiante "
                + "WHERE (? = 0 OR so.id_ciclo = ?) "
                + "AND (e.carnet LIKE ? OR CONCAT(e.nombres, ' ', e.apellidos) LIKE ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
            ps.setString(3, "%" + buscar + "%");
            ps.setString(4, "%" + buscar + "%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Solvencia buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE so.id_solvencia = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public LocalDate[] fechasCiclo(int idCiclo) throws SQLException {
        String sql = "SELECT fecha_inicio, fecha_fin FROM ciclo_escolar WHERE id_ciclo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new LocalDate[]{rs.getDate(1).toLocalDate(), rs.getDate(2).toLocalDate()};
            }
        }
    }

    public int crear(int idEstudiante, int idCiclo, int idUsuario) throws SQLException {
        String sql = "INSERT INTO solvencia (id_estudiante, id_ciclo, id_usuario) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Solvencia leer(ResultSet rs) throws SQLException {
        return new Solvencia(
                rs.getInt("id_solvencia"),
                rs.getInt("id_estudiante"),
                rs.getString("carnet"),
                rs.getString("estudiante"),
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                rs.getString("grado"),
                rs.getString("fecha_emision"),
                rs.getString("usuario"));
    }
}