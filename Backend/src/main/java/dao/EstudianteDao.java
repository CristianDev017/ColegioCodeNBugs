package dao;

import config.DBConnectionSingleton;
import dto.PeticionEncargado;
import dto.PeticionEstudiante;
import model.Encargado;
import model.Estudiante;
import model.FichaMedica;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDao {

    private static final String SELECT_BASE = "SELECT id_estudiante, carnet, nombres, apellidos, dpi, "
            + "fecha_nacimiento, genero, direccion, estado FROM estudiante ";

    private static final String FILTRO = "WHERE (carnet LIKE ? OR nombres LIKE ? OR apellidos LIKE ? "
            + "OR CONCAT(nombres, ' ', apellidos) LIKE ?) AND (? = '' OR estado = ?) ";

    public List<Estudiante> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY apellidos, nombres LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String patron = "%" + buscar + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);
            ps.setString(5, estado);
            ps.setString(6, estado);
            ps.setInt(7, tamano);
            ps.setInt(8, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leerBasico(rs, null, null));
                }
            }
        }
        return lista;
    }

    public int contar(String buscar, String estado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM estudiante " + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String patron = "%" + buscar + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);
            ps.setString(5, estado);
            ps.setString(6, estado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Estudiante buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE id_estudiante = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return leerBasico(rs, buscarFicha(con, id), buscarEncargadoPrincipal(con, id));
            }
        }
    }

    public boolean existeDpi(String dpi, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM estudiante WHERE dpi = ? AND id_estudiante <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dpi);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean existeDpiEncargado(String dpi, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM encargado WHERE dpi = ? AND id_encargado <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dpi);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public String siguienteCarnet(int anio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM estudiante WHERE carnet LIKE ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, anio + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return String.format("%d-%04d", anio, rs.getInt(1) + 1);
            }
        }
    }

    public int crear(PeticionEstudiante d, String carnet, String passwordCodificada) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                int idUsuario = insertarUsuario(con, carnet, passwordCodificada);
                int idEstudiante = insertarEstudiante(con, idUsuario, carnet, d);
                insertarFicha(con, idEstudiante, d);
                int idEncargado = buscarOCrearEncargado(con, d.getEncargado());
                vincularEncargado(con, idEstudiante, idEncargado, d.getEncargado().getParentesco());
                con.commit();
                return idEstudiante;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void actualizar(int id, int idEncargado, PeticionEstudiante d) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                String sqlEst = "UPDATE estudiante SET nombres = ?, apellidos = ?, dpi = ?, fecha_nacimiento = ?, "
                        + "genero = ?, direccion = ? WHERE id_estudiante = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlEst)) {
                    ps.setString(1, d.getNombres());
                    ps.setString(2, d.getApellidos());
                    ps.setString(3, d.getDpi());
                    ps.setDate(4, Date.valueOf(d.getFechaNacimiento()));
                    ps.setString(5, d.getGenero());
                    ps.setString(6, d.getDireccion());
                    ps.setInt(7, id);
                    ps.executeUpdate();
                }
                String sqlFicha = "UPDATE ficha_medica SET alergias = ?, condiciones = ?, contacto_emergencia = ?, "
                        + "telefono_emergencia = ? WHERE id_estudiante = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlFicha)) {
                    ps.setString(1, d.getAlergias());
                    ps.setString(2, d.getCondiciones());
                    ps.setString(3, d.getContactoEmergencia());
                    ps.setString(4, d.getTelefonoEmergencia());
                    ps.setInt(5, id);
                    ps.executeUpdate();
                }
                PeticionEncargado e = d.getEncargado();
                String sqlEnc = "UPDATE encargado SET nombres = ?, apellidos = ?, dpi = ?, telefono = ?, "
                        + "correo = ?, direccion = ? WHERE id_encargado = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlEnc)) {
                    ps.setString(1, e.getNombres());
                    ps.setString(2, e.getApellidos());
                    ps.setString(3, e.getDpi());
                    ps.setString(4, e.getTelefono());
                    ps.setString(5, e.getCorreo());
                    ps.setString(6, e.getDireccion());
                    ps.setInt(7, idEncargado);
                    ps.executeUpdate();
                }
                String sqlVinculo = "UPDATE estudiante_encargado SET parentesco = ? "
                        + "WHERE id_estudiante = ? AND id_encargado = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlVinculo)) {
                    ps.setString(1, e.getParentesco());
                    ps.setInt(2, id);
                    ps.setInt(3, idEncargado);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("UPDATE estudiante SET estado = ? WHERE id_estudiante = ?")) {
                    ps.setString(1, estado);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                String sqlUsuario = "UPDATE usuario SET estado = ? "
                        + "WHERE id_usuario = (SELECT id_usuario FROM estudiante WHERE id_estudiante = ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                    ps.setString(1, estado.equals("INACTIVO") ? "INACTIVO" : "ACTIVO");
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private int insertarUsuario(Connection con, String carnet, String passwordCodificada) throws SQLException {
        String sql = "INSERT INTO usuario (id_rol, username, correo, password) "
                + "VALUES ((SELECT id_rol FROM rol WHERE nombre = 'ESTUDIANTE'), ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, carnet);
            ps.setString(2, carnet.toLowerCase() + "@colegio.local");
            ps.setString(3, passwordCodificada);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int insertarEstudiante(Connection con, int idUsuario, String carnet, PeticionEstudiante d) throws SQLException {
        String sql = "INSERT INTO estudiante (id_usuario, carnet, nombres, apellidos, dpi, fecha_nacimiento, genero, direccion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, carnet);
            ps.setString(3, d.getNombres());
            ps.setString(4, d.getApellidos());
            ps.setString(5, d.getDpi());
            ps.setDate(6, Date.valueOf(d.getFechaNacimiento()));
            ps.setString(7, d.getGenero());
            ps.setString(8, d.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void insertarFicha(Connection con, int idEstudiante, PeticionEstudiante d) throws SQLException {
        String sql = "INSERT INTO ficha_medica (id_estudiante, alergias, condiciones, contacto_emergencia, telefono_emergencia) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setString(2, d.getAlergias());
            ps.setString(3, d.getCondiciones());
            ps.setString(4, d.getContactoEmergencia());
            ps.setString(5, d.getTelefonoEmergencia());
            ps.executeUpdate();
        }
    }

    private int buscarOCrearEncargado(Connection con, PeticionEncargado e) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT id_encargado FROM encargado WHERE dpi = ?")) {
            ps.setString(1, e.getDpi());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        String sql = "INSERT INTO encargado (nombres, apellidos, dpi, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombres());
            ps.setString(2, e.getApellidos());
            ps.setString(3, e.getDpi());
            ps.setString(4, e.getTelefono());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void vincularEncargado(Connection con, int idEstudiante, int idEncargado, String parentesco) throws SQLException {
        String sql = "INSERT INTO estudiante_encargado (id_estudiante, id_encargado, parentesco, es_principal) "
                + "VALUES (?, ?, ?, 1)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idEncargado);
            ps.setString(3, parentesco);
            ps.executeUpdate();
        }
    }

    private FichaMedica buscarFicha(Connection con, int idEstudiante) throws SQLException {
        String sql = "SELECT alergias, condiciones, contacto_emergencia, telefono_emergencia "
                + "FROM ficha_medica WHERE id_estudiante = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new FichaMedica(
                        rs.getString("alergias"),
                        rs.getString("condiciones"),
                        rs.getString("contacto_emergencia"),
                        rs.getString("telefono_emergencia"));
            }
        }
    }

    private Encargado buscarEncargadoPrincipal(Connection con, int idEstudiante) throws SQLException {
        String sql = "SELECT e.id_encargado, e.nombres, e.apellidos, e.dpi, e.telefono, e.correo, e.direccion, ee.parentesco "
                + "FROM estudiante_encargado ee JOIN encargado e ON e.id_encargado = ee.id_encargado "
                + "WHERE ee.id_estudiante = ? AND ee.es_principal = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Encargado(
                        rs.getInt("id_encargado"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("dpi"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("direccion"),
                        rs.getString("parentesco"));
            }
        }
    }

    private Estudiante leerBasico(ResultSet rs, FichaMedica ficha, Encargado encargado) throws SQLException {
        return new Estudiante(
                rs.getInt("id_estudiante"),
                rs.getString("carnet"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dpi"),
                rs.getDate("fecha_nacimiento").toString(),
                rs.getString("genero"),
                rs.getString("direccion"),
                rs.getString("estado"),
                ficha,
                encargado);
    }
}