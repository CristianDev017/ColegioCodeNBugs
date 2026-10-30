package dao.academico;

import config.DBConnectionSingleton;
import model.academico.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurriculoDao {

    public String estadoCiclo(int idCiclo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT estado FROM ciclo_escolar WHERE id_ciclo = ?")) {
            ps.setInt(1, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public Boolean gradoActivo(int idGrado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT activo FROM grado WHERE id_grado = ?")) {
            ps.setInt(1, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBoolean(1) : null;
            }
        }
    }

    public int buscarId(int idCiclo, int idGrado) throws SQLException {
        String sql = "SELECT id_curriculo FROM curriculo WHERE id_ciclo = ? AND id_grado = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int[] buscarCicloGrado(int idCurriculo) throws SQLException {
        String sql = "SELECT id_ciclo, id_grado FROM curriculo WHERE id_curriculo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new int[]{rs.getInt(1), rs.getInt(2)} : null;
            }
        }
    }

    public int crear(int idCiclo, int idGrado) throws SQLException {
        String sql = "INSERT INTO curriculo (id_grado, id_ciclo) VALUES (?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public List<Curso> listarCursos(int idCurriculo) throws SQLException {
        String sql = "SELECT c.id_curso, c.nombre, c.descripcion, c.activo FROM curriculo_curso cc "
                + "JOIN curso c ON c.id_curso = cc.id_curso WHERE cc.id_curriculo = ? ORDER BY c.nombre";
        List<Curso> lista = new ArrayList<>();
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Curso(
                            rs.getInt("id_curso"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getBoolean("activo")));
                }
            }
        }
        return lista;
    }

    public boolean existeCurso(int idCurriculo, int idCurso) throws SQLException {
        String sql = "SELECT COUNT(*) FROM curriculo_curso WHERE id_curriculo = ? AND id_curso = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculo);
            ps.setInt(2, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void agregarCurso(int idCurriculo, int idCurso) throws SQLException {
        String sql = "INSERT INTO curriculo_curso (id_curriculo, id_curso) VALUES (?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculo);
            ps.setInt(2, idCurso);
            ps.executeUpdate();
        }
    }

    public void quitarCurso(int idCurriculo, int idCurso) throws SQLException {
        String sql = "DELETE FROM curriculo_curso WHERE id_curriculo = ? AND id_curso = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculo);
            ps.setInt(2, idCurso);
            ps.executeUpdate();
        }
    }
}