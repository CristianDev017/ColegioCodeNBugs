package dao.estudiantes;

import config.DBConnectionSingleton;
import model.estudiantes.InscripcionDetalle;
import model.academico.SeccionDisponible;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDao {

    private static final String NOMBRE_GRADO =
            "CONCAT(n.nombre, ' - ', g.nombre, IF(ca.nombre IS NULL, '', CONCAT(' (', ca.nombre, ')')))";

    private static final String SELECT_DETALLE = "SELECT i.id_inscripcion, i.id_estudiante, e.carnet, "
            + "CONCAT(e.apellidos, ', ', e.nombres) AS estudiante, i.id_seccion, "
            + NOMBRE_GRADO + " AS grado, s.nombre AS seccion, c.anio, i.fecha_inscripcion, i.estado "
            + "FROM inscripcion i "
            + "JOIN estudiante e ON e.id_estudiante = i.id_estudiante "
            + "JOIN seccion s ON s.id_seccion = i.id_seccion "
            + "JOIN grado g ON g.id_grado = s.id_grado "
            + "JOIN nivel n ON n.id_nivel = g.id_nivel "
            + "LEFT JOIN carrera ca ON ca.id_carrera = g.id_carrera "
            + "JOIN ciclo_escolar c ON c.id_ciclo = i.id_ciclo ";

    private static final String FILTRO = "WHERE i.id_ciclo = ? AND (? = 0 OR i.id_seccion = ?) "
            + "AND (e.carnet LIKE ? OR e.nombres LIKE ? OR e.apellidos LIKE ? "
            + "OR CONCAT(e.nombres, ' ', e.apellidos) LIKE ?) ";

    public int cicloActivo() throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id_ciclo FROM ciclo_escolar WHERE estado = 'ACTIVO'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public String estadoEstudiante(int idEstudiante) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT estado FROM estudiante WHERE id_estudiante = ?")) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public int[] infoSeccion(int idSeccion) throws SQLException {
        String sql = "SELECT s.cupo, s.activo, g.activo AS grado_activo, s.id_grado "
                + "FROM seccion s JOIN grado g ON g.id_grado = s.id_grado WHERE s.id_seccion = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new int[]{
                        rs.getInt("cupo"),
                        rs.getBoolean("activo") ? 1 : 0,
                        rs.getBoolean("grado_activo") ? 1 : 0,
                        rs.getInt("id_grado")};
            }
        }
    }

    public boolean tieneCurriculo(int idCiclo, int idGrado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM curriculo c "
                + "JOIN curriculo_curso cc ON cc.id_curriculo = c.id_curriculo "
                + "WHERE c.id_ciclo = ? AND c.id_grado = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int contarInscritos(int idSeccion, int idCiclo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE id_seccion = ? AND id_ciclo = ? AND estado = 'ACTIVA'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public boolean existeInscripcion(int idEstudiante, int idCiclo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion WHERE id_estudiante = ? AND id_ciclo = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int crear(int idEstudiante, int idCiclo, int idSeccion, int idUsuario) throws SQLException {
        String sql = "INSERT INTO inscripcion (id_estudiante, id_ciclo, id_seccion, id_usuario, fecha_inscripcion) "
                + "VALUES (?, ?, ?, ?, CURDATE())";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            ps.setInt(3, idSeccion);
            ps.setInt(4, idUsuario);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public InscripcionDetalle buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_DETALLE + "WHERE i.id_inscripcion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public List<InscripcionDetalle> listar(int idCiclo, String buscar, int idSeccion, int pagina, int tamano) throws SQLException {
        List<InscripcionDetalle> lista = new ArrayList<>();
        String sql = SELECT_DETALLE + FILTRO + "ORDER BY e.apellidos, e.nombres LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String patron = "%" + buscar + "%";
            ps.setInt(1, idCiclo);
            ps.setInt(2, idSeccion);
            ps.setInt(3, idSeccion);
            ps.setString(4, patron);
            ps.setString(5, patron);
            ps.setString(6, patron);
            ps.setString(7, patron);
            ps.setInt(8, tamano);
            ps.setInt(9, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(int idCiclo, String buscar, int idSeccion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inscripcion i JOIN estudiante e ON e.id_estudiante = i.id_estudiante " + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String patron = "%" + buscar + "%";
            ps.setInt(1, idCiclo);
            ps.setInt(2, idSeccion);
            ps.setInt(3, idSeccion);
            ps.setString(4, patron);
            ps.setString(5, patron);
            ps.setString(6, patron);
            ps.setString(7, patron);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public List<SeccionDisponible> seccionesDisponibles(int idCiclo) throws SQLException {
        List<SeccionDisponible> lista = new ArrayList<>();
        String sql = "SELECT s.id_seccion, " + NOMBRE_GRADO + " AS grado, s.nombre AS seccion, s.cupo, "
                + "(SELECT COUNT(*) FROM inscripcion i WHERE i.id_seccion = s.id_seccion "
                + "AND i.id_ciclo = ? AND i.estado = 'ACTIVA') AS inscritos "
                + "FROM seccion s "
                + "JOIN grado g ON g.id_grado = s.id_grado "
                + "JOIN nivel n ON n.id_nivel = g.id_nivel "
                + "LEFT JOIN carrera ca ON ca.id_carrera = g.id_carrera "
                + "WHERE s.activo = 1 AND g.activo = 1 "
                + "AND EXISTS (SELECT 1 FROM curriculo cu "
                + "JOIN curriculo_curso cc ON cc.id_curriculo = cu.id_curriculo "
                + "WHERE cu.id_ciclo = ? AND cu.id_grado = g.id_grado) "
                + "ORDER BY n.id_nivel, g.nombre, s.nombre";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiclo);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new SeccionDisponible(
                            rs.getInt("id_seccion"),
                            rs.getString("grado"),
                            rs.getString("seccion"),
                            rs.getInt("cupo"),
                            rs.getInt("inscritos")));
                }
            }
        }
        return lista;
    }

    private InscripcionDetalle leer(ResultSet rs) throws SQLException {
        return new InscripcionDetalle(
                rs.getInt("id_inscripcion"),
                rs.getInt("id_estudiante"),
                rs.getString("carnet"),
                rs.getString("estudiante"),
                rs.getInt("id_seccion"),
                rs.getString("grado"),
                rs.getString("seccion"),
                rs.getInt("anio"),
                rs.getDate("fecha_inscripcion").toString(),
                rs.getString("estado"));
    }
}