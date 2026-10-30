package dao.docencia;

import config.DBConnectionSingleton;
import dto.docencia.PeticionNotas;
import model.docencia.AlumnoNota;
import model.docencia.ContextoAsignacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotaDao {

    public List<AlumnoNota> listar(ContextoAsignacion ctx, int idPonderacion, int pagina, int tamano)
            throws SQLException {
        List<AlumnoNota> lista = new ArrayList<>();
        String sql = "SELECT i.id_inscripcion, es.carnet, CONCAT(es.apellidos, ', ', es.nombres) AS estudiante, "
                + "n.valor FROM inscripcion i "
                + "JOIN estudiante es ON es.id_estudiante = i.id_estudiante "
                + "LEFT JOIN nota n ON n.id_inscripcion = i.id_inscripcion AND n.id_ponderacion = ? "
                + "WHERE i.id_seccion = ? AND i.id_ciclo = ? AND i.estado = 'ACTIVA' "
                + "ORDER BY es.apellidos, es.nombres LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPonderacion);
            ps.setInt(2, ctx.getIdSeccion());
            ps.setInt(3, ctx.getIdCiclo());
            ps.setInt(4, tamano);
            ps.setInt(5, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AlumnoNota(
                            rs.getInt("id_inscripcion"),
                            rs.getString("carnet"),
                            rs.getString("estudiante"),
                            rs.getBigDecimal("valor")));
                }
            }
        }
        return lista;
    }

    public void guardar(int idUsuario, int idPonderacion, List<PeticionNotas.Registro> notas) throws SQLException {
        String sql = "INSERT INTO nota (id_inscripcion, id_ponderacion, id_usuario, valor) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE valor = VALUES(valor), id_usuario = VALUES(id_usuario), "
                + "fecha_registro = CURRENT_TIMESTAMP";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (PeticionNotas.Registro r : notas) {
                    ps.setInt(1, r.getIdInscripcion());
                    ps.setInt(2, idPonderacion);
                    ps.setInt(3, idUsuario);
                    ps.setBigDecimal(4, r.getValor());
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
}