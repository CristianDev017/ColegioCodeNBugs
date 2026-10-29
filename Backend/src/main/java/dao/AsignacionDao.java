package dao;

import config.DBConnectionSingleton;
import dto.PeticionAsignacion;
import model.Asignacion;
import model.CursoSeccion;
import model.OpcionAsignacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDao {

    private static final String ETIQUETA_SECCION = "CONCAT(n.nombre, ' - ', g.nombre, "
            + "IF(ca.nombre IS NULL, '', CONCAT(' (', ca.nombre, ')')), ' - Sección ', s.nombre)";

    private static final String UNIONES_SECCION = "JOIN grado g ON g.id_grado = s.id_grado "
            + "JOIN nivel n ON n.id_nivel = g.id_nivel "
            + "LEFT JOIN carrera ca ON ca.id_carrera = g.id_carrera ";

    private static final String DESDE = "FROM asignacion_maestro a "
            + "JOIN empleado e ON e.id_empleado = a.id_empleado "
            + "JOIN curriculo_curso cc ON cc.id_curriculo_curso = a.id_curriculo_curso "
            + "JOIN curso cu ON cu.id_curso = cc.id_curso "
            + "JOIN curriculo cr ON cr.id_curriculo = cc.id_curriculo "
            + "JOIN ciclo_escolar c ON c.id_ciclo = cr.id_ciclo "
            + "JOIN seccion s ON s.id_seccion = a.id_seccion "
            + UNIONES_SECCION;

    private static final String SELECT_BASE = "SELECT a.id_asignacion, a.id_empleado, "
            + "CONCAT(e.apellidos, ', ', e.nombres) AS maestro, a.id_curriculo_curso, cu.nombre AS curso, "
            + "a.id_seccion, " + ETIQUETA_SECCION + " AS seccion, c.id_ciclo, c.anio " + DESDE;

    private static final String FILTRO = "WHERE (? = 0 OR a.id_empleado = ?) AND (? = 0 OR a.id_seccion = ?) ";

    public List<Asignacion> listar(int idEmpleado, int idSeccion, int pagina, int tamano) throws SQLException {
        List<Asignacion> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY c.anio DESC, maestro, curso LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idEmpleado);
            ps.setInt(3, idSeccion);
            ps.setInt(4, idSeccion);
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

    public int contar(int idEmpleado, int idSeccion) throws SQLException {
        String sql = "SELECT COUNT(*) " + DESDE + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idEmpleado);
            ps.setInt(3, idSeccion);
            ps.setInt(4, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Asignacion buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE a.id_asignacion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public List<OpcionAsignacion> maestrosActivos() throws SQLException {
        List<OpcionAsignacion> lista = new ArrayList<>();
        String sql = "SELECT id_empleado, CONCAT(apellidos, ', ', nombres) FROM empleado "
                + "WHERE puesto = 'Maestro' AND estado = 'ACTIVO' ORDER BY apellidos, nombres";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new OpcionAsignacion(rs.getInt(1), rs.getString(2)));
            }
        }
        return lista;
    }

    public List<OpcionAsignacion> seccionesConCurriculo() throws SQLException {
        List<OpcionAsignacion> lista = new ArrayList<>();
        String sql = "SELECT s.id_seccion, " + ETIQUETA_SECCION + " FROM seccion s " + UNIONES_SECCION
                + "WHERE s.activo = 1 AND g.activo = 1 AND EXISTS (SELECT 1 FROM curriculo cr "
                + "JOIN ciclo_escolar c ON c.id_ciclo = cr.id_ciclo "
                + "WHERE cr.id_grado = g.id_grado AND c.estado = 'ACTIVO') "
                + "ORDER BY n.id_nivel, g.nombre, s.nombre";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new OpcionAsignacion(rs.getInt(1), rs.getString(2)));
            }
        }
        return lista;
    }

    public List<CursoSeccion> cursosDeSeccion(int idSeccion) throws SQLException {
        List<CursoSeccion> lista = new ArrayList<>();
        String sql = "SELECT cc.id_curriculo_curso, cu.nombre, a.id_asignacion, a.id_empleado, "
                + "CONCAT(e.apellidos, ', ', e.nombres) AS maestro "
                + "FROM seccion s "
                + "JOIN curriculo cr ON cr.id_grado = s.id_grado "
                + "JOIN ciclo_escolar c ON c.id_ciclo = cr.id_ciclo AND c.estado = 'ACTIVO' "
                + "JOIN curriculo_curso cc ON cc.id_curriculo = cr.id_curriculo "
                + "JOIN curso cu ON cu.id_curso = cc.id_curso "
                + "LEFT JOIN asignacion_maestro a ON a.id_curriculo_curso = cc.id_curriculo_curso "
                + "AND a.id_seccion = s.id_seccion "
                + "LEFT JOIN empleado e ON e.id_empleado = a.id_empleado "
                + "WHERE s.id_seccion = ? ORDER BY cu.nombre";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idAsignacion = rs.getInt(3);
                    boolean hayAsignacion = !rs.wasNull();
                    int idEmpleado = rs.getInt(4);
                    lista.add(new CursoSeccion(
                            rs.getInt(1),
                            rs.getString(2),
                            hayAsignacion ? idAsignacion : null,
                            hayAsignacion ? idEmpleado : null,
                            rs.getString(5)));
                }
            }
        }
        return lista;
    }

    public String[] datosEmpleado(int idEmpleado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT puesto, estado FROM empleado WHERE id_empleado = ?")) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new String[]{rs.getString(1), rs.getString(2)} : null;
            }
        }
    }

    public int[] datosCurriculoCurso(int idCurriculoCurso) throws SQLException {
        String sql = "SELECT cr.id_grado, cr.id_ciclo, IF(c.estado = 'ACTIVO', 1, 0) FROM curriculo_curso cc "
                + "JOIN curriculo cr ON cr.id_curriculo = cc.id_curriculo "
                + "JOIN ciclo_escolar c ON c.id_ciclo = cr.id_ciclo WHERE cc.id_curriculo_curso = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculoCurso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)} : null;
            }
        }
    }

    public int[] datosSeccion(int idSeccion) throws SQLException {
        String sql = "SELECT s.id_grado, IF(s.activo = 1 AND g.activo = 1, 1, 0) FROM seccion s "
                + "JOIN grado g ON g.id_grado = s.id_grado WHERE s.id_seccion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new int[]{rs.getInt(1), rs.getInt(2)} : null;
            }
        }
    }

    public boolean existe(int idCurriculoCurso, int idSeccion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM asignacion_maestro WHERE id_curriculo_curso = ? AND id_seccion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurriculoCurso);
            ps.setInt(2, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean cicloActivo(int idCiclo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT estado FROM ciclo_escolar WHERE id_ciclo = ?")) {
            ps.setInt(1, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getString(1).equals("ACTIVO");
            }
        }
    }

    public boolean tieneRegistros(int idAsignacion) throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM ponderacion WHERE id_asignacion = ?) "
                + "+ (SELECT COUNT(*) FROM asistencia WHERE id_asignacion = ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.setInt(2, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int crear(PeticionAsignacion d) throws SQLException {
        String sql = "INSERT INTO asignacion_maestro (id_empleado, id_curriculo_curso, id_seccion) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdEmpleado());
            ps.setInt(2, d.getIdCurriculoCurso());
            ps.setInt(3, d.getIdSeccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void cambiarMaestro(int id, int idEmpleado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE asignacion_maestro SET id_empleado = ? WHERE id_asignacion = ?")) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM asignacion_maestro WHERE id_asignacion = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Asignacion leer(ResultSet rs) throws SQLException {
        return new Asignacion(
                rs.getInt("id_asignacion"),
                rs.getInt("id_empleado"),
                rs.getString("maestro"),
                rs.getInt("id_curriculo_curso"),
                rs.getString("curso"),
                rs.getInt("id_seccion"),
                rs.getString("seccion"),
                rs.getInt("id_ciclo"),
                rs.getInt("anio"));
    }
}