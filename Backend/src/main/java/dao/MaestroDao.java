package dao;

import config.DBConnectionSingleton;
import dto.PeticionAsistencia;
import model.AlumnoNomina;
import model.ContextoAsignacion;
import model.CursoMaestro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaestroDao {

    private static final String ETIQUETA_SECCION = "CONCAT(n.nombre, ' - ', g.nombre, "
            + "IF(ca.nombre IS NULL, '', CONCAT(' (', ca.nombre, ')')), ' - Sección ', s.nombre)";

    private static final String DESDE = "FROM asignacion_maestro a "
            + "JOIN empleado e ON e.id_empleado = a.id_empleado "
            + "JOIN curriculo_curso cc ON cc.id_curriculo_curso = a.id_curriculo_curso "
            + "JOIN curso cu ON cu.id_curso = cc.id_curso "
            + "JOIN curriculo cr ON cr.id_curriculo = cc.id_curriculo "
            + "JOIN ciclo_escolar c ON c.id_ciclo = cr.id_ciclo "
            + "JOIN seccion s ON s.id_seccion = a.id_seccion "
            + "JOIN grado g ON g.id_grado = s.id_grado "
            + "JOIN nivel n ON n.id_nivel = g.id_nivel "
            + "LEFT JOIN carrera ca ON ca.id_carrera = g.id_carrera ";

    private static final String SELECT_CURSO = "SELECT a.id_asignacion, cu.nombre AS curso, "
            + ETIQUETA_SECCION + " AS seccion, c.id_ciclo, c.anio, "
            + "(SELECT COUNT(*) FROM inscripcion i WHERE i.id_seccion = a.id_seccion "
            + "AND i.id_ciclo = c.id_ciclo AND i.estado = 'ACTIVA') AS estudiantes " + DESDE;

    public List<CursoMaestro> listarCursos(int idUsuario, int pagina, int tamano) throws SQLException {
        List<CursoMaestro> lista = new ArrayList<>();
        String sql = SELECT_CURSO + "WHERE e.id_usuario = ? AND c.estado = 'ACTIVO' "
                + "ORDER BY cu.nombre, s.nombre LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, tamano);
            ps.setInt(3, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leerCurso(rs));
                }
            }
        }
        return lista;
    }

    public int contarCursos(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) " + DESDE + "WHERE e.id_usuario = ? AND c.estado = 'ACTIVO'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public CursoMaestro buscarCurso(int idUsuario, int idAsignacion) throws SQLException {
        String sql = SELECT_CURSO + "WHERE e.id_usuario = ? AND a.id_asignacion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leerCurso(rs) : null;
            }
        }
    }

    public ContextoAsignacion contexto(int idUsuario, int idAsignacion) throws SQLException {
        String sql = "SELECT a.id_seccion, c.id_ciclo, c.estado, c.fecha_inicio, c.fecha_fin " + DESDE
                + "WHERE e.id_usuario = ? AND a.id_asignacion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new ContextoAsignacion(
                        rs.getInt("id_seccion"),
                        rs.getInt("id_ciclo"),
                        rs.getString("estado").equals("ACTIVO"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"));
            }
        }
    }

    public List<AlumnoNomina> nomina(int idAsignacion, ContextoAsignacion ctx, String fecha, int pagina, int tamano)
            throws SQLException {
        List<AlumnoNomina> lista = new ArrayList<>();
        String sql = "SELECT i.id_inscripcion, es.carnet, CONCAT(es.apellidos, ', ', es.nombres) AS estudiante, "
                + "a.estado FROM inscripcion i "
                + "JOIN estudiante es ON es.id_estudiante = i.id_estudiante "
                + "LEFT JOIN asistencia a ON a.id_inscripcion = i.id_inscripcion "
                + "AND a.id_asignacion = ? AND a.fecha = ? "
                + "WHERE i.id_seccion = ? AND i.id_ciclo = ? AND i.estado = 'ACTIVA' "
                + "ORDER BY es.apellidos, es.nombres LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.setString(2, fecha);
            ps.setInt(3, ctx.getIdSeccion());
            ps.setInt(4, ctx.getIdCiclo());
            ps.setInt(5, tamano);
            ps.setInt(6, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AlumnoNomina(
                            rs.getInt("id_inscripcion"),
                            rs.getString("carnet"),
                            rs.getString("estudiante"),
                            rs.getString("estado")));
                }
            }
        }
        return lista;
    }

    public int contarNomina(ContextoAsignacion ctx) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE id_seccion = ? AND id_ciclo = ? AND estado = 'ACTIVA'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ctx.getIdSeccion());
            ps.setInt(2, ctx.getIdCiclo());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public boolean inscripcionDeLaSeccion(int idInscripcion, ContextoAsignacion ctx) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE id_inscripcion = ? AND id_seccion = ? "
                + "AND id_ciclo = ? AND estado = 'ACTIVA'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.setInt(2, ctx.getIdSeccion());
            ps.setInt(3, ctx.getIdCiclo());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void guardarAsistencia(int idAsignacion, String fecha, List<PeticionAsistencia.Registro> registros)
            throws SQLException {
        String sql = "INSERT INTO asistencia (id_inscripcion, id_asignacion, fecha, estado) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE estado = VALUES(estado)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (PeticionAsistencia.Registro r : registros) {
                    ps.setInt(1, r.getIdInscripcion());
                    ps.setInt(2, idAsignacion);
                    ps.setString(3, fecha);
                    ps.setString(4, r.getEstado());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private CursoMaestro leerCurso(ResultSet rs) throws SQLException {
        return new CursoMaestro(
                rs.getInt("id_asignacion"),
                rs.getString("curso"),
                rs.getString("seccion"),
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                rs.getInt("estudiantes"));
    }
}