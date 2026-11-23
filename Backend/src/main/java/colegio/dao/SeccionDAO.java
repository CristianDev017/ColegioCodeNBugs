
package colegio.dao;

import colegio.conexion.Conexion;
import colegio.model.Seccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SeccionDAO {

    private static final String CONSULTA =
            "SELECT s.id_seccion, s.id_ciclo_grado, " +
                    "ce.id_ciclo, ce.anio, ce.estado AS estado_ciclo, " +
                    "g.id_grado, g.nombre AS grado, " +
                    "n.nombre AS nivel, c.nombre AS carrera, " +
                    "s.nombre, s.cupo, s.activo " +
                    "FROM seccion s " +
                    "JOIN ciclo_grado cg ON cg.id_ciclo_grado = s.id_ciclo_grado " +
                    "JOIN ciclo_escolar ce ON ce.id_ciclo = cg.id_ciclo " +
                    "JOIN grado g ON g.id_grado = cg.id_grado " +
                    "JOIN nivel n ON n.id_nivel = g.id_nivel " +
                    "LEFT JOIN carrera c ON c.id_carrera = g.id_carrera ";

    public List<Seccion> listar(int pagina, int tamano)
            throws SQLException {

        List<Seccion> secciones = new ArrayList<>();

        String sql = CONSULTA +
                "ORDER BY ce.anio DESC, g.nombre, s.nombre, s.id_seccion " +
                "LIMIT ? OFFSET ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tamano);
            ps.setInt(2, (pagina - 1) * tamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    secciones.add(convertir(rs));
                }
            }
        }

        return secciones;
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM seccion";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public List<Seccion> listarCatalogo() throws SQLException {

        List<Seccion> secciones = new ArrayList<>();

        String sql = CONSULTA +
                "ORDER BY ce.anio DESC, g.nombre, s.nombre, s.id_seccion";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                secciones.add(convertir(rs));
            }
        }

        return secciones;
    }

    public Seccion buscarPorId(int id) throws SQLException {

        String sql = CONSULTA + "WHERE s.id_seccion = ?";

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

    public List<Map<String, Object>> listarGradosDisponibles()
            throws SQLException {

        List<Map<String, Object>> opciones = new ArrayList<>();

        String sql =
                "SELECT cg.id_ciclo_grado, ce.id_ciclo, ce.anio, " +
                        "g.id_grado, g.nombre AS grado, " +
                        "n.nombre AS nivel, c.nombre AS carrera, " +
                        "g.cupo_default " +
                        "FROM ciclo_grado cg " +
                        "JOIN ciclo_escolar ce ON ce.id_ciclo = cg.id_ciclo " +
                        "JOIN grado g ON g.id_grado = cg.id_grado " +
                        "JOIN nivel n ON n.id_nivel = g.id_nivel " +
                        "LEFT JOIN carrera c ON c.id_carrera = g.id_carrera " +
                        "WHERE cg.habilitado = 1 AND g.activo = 1 " +
                        "AND ce.estado IN ('PLANIFICADO', 'ACTIVO') " +
                        "ORDER BY ce.anio DESC, n.nombre, g.nombre";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> opcion = new LinkedHashMap<>();

                opcion.put("idCicloGrado", rs.getInt("id_ciclo_grado"));
                opcion.put("idCiclo", rs.getInt("id_ciclo"));
                opcion.put("anio", rs.getInt("anio"));
                opcion.put("idGrado", rs.getInt("id_grado"));
                opcion.put("grado", rs.getString("grado"));
                opcion.put("nivel", rs.getString("nivel"));
                opcion.put("carrera", rs.getString("carrera"));
                opcion.put("cupoDefault", rs.getInt("cupo_default"));

                opciones.add(opcion);
            }
        }

        return opciones;
    }

    public String obtenerEstadoCicloGrado(int idCicloGrado)
            throws SQLException {

        String sql =
                "SELECT ce.estado " +
                        "FROM ciclo_grado cg " +
                        "JOIN ciclo_escolar ce ON ce.id_ciclo = cg.id_ciclo " +
                        "JOIN grado g ON g.id_grado = cg.id_grado " +
                        "WHERE cg.id_ciclo_grado = ? " +
                        "AND cg.habilitado = 1 AND g.activo = 1";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCicloGrado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        }

        return null;
    }

    public boolean existeDuplicado(int idCicloGrado, String nombre,
                                   int idExcluir) throws SQLException {

        String sql =
                "SELECT 1 FROM seccion " +
                        "WHERE id_ciclo_grado = ? " +
                        "AND LOWER(nombre) = LOWER(?) " +
                        "AND id_seccion <> ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCicloGrado);
            ps.setString(2, nombre);
            ps.setInt(3, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void crear(int idCicloGrado, String nombre, int cupo)
            throws SQLException {

        String sql =
                "INSERT INTO seccion " +
                        "(id_ciclo_grado, nombre, cupo, activo) " +
                        "VALUES (?, ?, ?, 1)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCicloGrado);
            ps.setString(2, nombre);
            ps.setInt(3, cupo);
            ps.executeUpdate();
        }
    }

    public boolean editar(int id, int idCicloGrado,
                          String nombre, int cupo) throws SQLException {

        String sql =
                "UPDATE seccion " +
                        "SET id_ciclo_grado = ?, nombre = ?, cupo = ? " +
                        "WHERE id_seccion = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCicloGrado);
            ps.setString(2, nombre);
            ps.setInt(3, cupo);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int id, boolean activo)
            throws SQLException {

        String sql =
                "UPDATE seccion SET activo = ? WHERE id_seccion = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Seccion convertir(ResultSet rs) throws SQLException {

        return new Seccion(
                rs.getInt("id_seccion"),
                rs.getInt("id_ciclo_grado"),
                rs.getInt("id_ciclo"),
                rs.getInt("anio"),
                rs.getInt("id_grado"),
                rs.getString("grado"),
                rs.getString("nivel"),
                rs.getString("carrera"),
                rs.getString("nombre"),
                rs.getInt("cupo"),
                rs.getBoolean("activo"),
                rs.getString("estado_ciclo")
        );
    }
}
