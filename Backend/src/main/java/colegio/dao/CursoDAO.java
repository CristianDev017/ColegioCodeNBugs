
package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    private static final String CONSULTA =
            "SELECT id_curso, nombre, descripcion, activo FROM curso ";

    public List<Curso> listar(int pagina, int tamano) throws SQLException {
        List<Curso> cursos = new ArrayList<>();

        String sql = CONSULTA + "ORDER BY id_curso LIMIT ? OFFSET ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cursos.add(convertir(rs));
                }
            }
        }

        return cursos;
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM curso";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public List<Curso> listarCatalogo() throws SQLException {
        List<Curso> cursos = new ArrayList<>();

        String sql = CONSULTA + "ORDER BY nombre";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cursos.add(convertir(rs));
            }
        }

        return cursos;
    }

    public Curso buscarPorId(int idCurso) throws SQLException {
        String sql = CONSULTA + "WHERE id_curso = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurso);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertir(rs);
                }
            }
        }

        return null;
    }

    public boolean nombreOcupado(String nombre, int idExcluir)
            throws SQLException {

        String sql = "SELECT 1 FROM curso " +
                "WHERE LOWER(nombre) = LOWER(?) AND id_curso <> ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void crear(String nombre, String descripcion) throws SQLException {
        String sql = "INSERT INTO curso (nombre, descripcion, activo) " +
                "VALUES (?, ?, 1)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.executeUpdate();
        }
    }

    public boolean editar(int idCurso, String nombre, String descripcion)
            throws SQLException {

        String sql = "UPDATE curso SET nombre = ?, descripcion = ? " +
                "WHERE id_curso = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, idCurso);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idCurso, boolean activo)
            throws SQLException {

        String sql = "UPDATE curso SET activo = ? WHERE id_curso = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idCurso);

            return ps.executeUpdate() > 0;
        }
    }

    private Curso convertir(ResultSet rs) throws SQLException {
        return new Curso(
                rs.getInt("id_curso"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("activo")
        );
    }
}
