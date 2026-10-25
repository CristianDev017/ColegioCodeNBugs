package dao;

import config.DBConnectionSingleton;
import dto.PeticionCurso;
import model.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CursoDao {

    private static final String SELECT_BASE = "SELECT id_curso, nombre, descripcion, activo FROM curso ";

    public List<Curso> listar(int pagina, int tamano) throws SQLException {
        List<Curso> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "ORDER BY nombre LIMIT ? OFFSET ?")) {
            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public List<Curso> listarActivos() throws SQLException {
        List<Curso> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE activo = 1 ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leer(rs));
            }
        }
        return lista;
    }

    public int contar() throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM curso");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public Curso buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE id_curso = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeNombre(String nombre, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM curso WHERE nombre = ? AND id_curso <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int contarEnCurriculoActivo(int idCurso) throws SQLException {
        String sql = "SELECT COUNT(*) FROM curriculo_curso cc "
                + "JOIN curriculo c ON c.id_curriculo = cc.id_curriculo "
                + "JOIN ciclo_escolar ce ON ce.id_ciclo = c.id_ciclo "
                + "WHERE cc.id_curso = ? AND ce.estado = 'ACTIVO'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int crear(PeticionCurso d) throws SQLException {
        String sql = "INSERT INTO curso (nombre, descripcion) VALUES (?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, PeticionCurso d) throws SQLException {
        String sql = "UPDATE curso SET nombre = ?, descripcion = ? WHERE id_curso = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getDescripcion());
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void cambiarActivo(int id, boolean activo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE curso SET activo = ? WHERE id_curso = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Curso leer(ResultSet rs) throws SQLException {
        return new Curso(
                rs.getInt("id_curso"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo"));
    }
}