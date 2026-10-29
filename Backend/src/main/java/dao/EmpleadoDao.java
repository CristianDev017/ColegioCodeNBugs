package dao;

import config.DBConnectionSingleton;
import dto.PeticionEmpleado;
import model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDao {

    private static final String SELECT_BASE = "SELECT id_empleado, nombres, apellidos, dpi, fecha_nacimiento, "
            + "telefono, direccion, puesto, fecha_contratacion, salario, estado FROM empleado ";

    private static final String FILTRO = "WHERE (? = '' OR CONCAT(nombres, ' ', apellidos) LIKE ? OR dpi LIKE ?) "
            + "AND (? = '' OR puesto = ?) AND (? = '' OR estado = ?) ";

    public List<Empleado> listar(String buscar, String puesto, String estado, int pagina, int tamano)
            throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY apellidos, nombres LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = poner(ps, buscar, puesto, estado);
            ps.setInt(i++, tamano);
            ps.setInt(i, (pagina - 1) * tamano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(leer(rs));
                }
            }
        }
        return lista;
    }

    public int contar(String buscar, String puesto, String estado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM empleado " + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            poner(ps, buscar, puesto, estado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Empleado buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE id_empleado = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existeDpi(String dpi, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM empleado WHERE dpi = ? AND id_empleado <> ?";
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

    public int crear(PeticionEmpleado d) throws SQLException {
        String sql = "INSERT INTO empleado (nombres, apellidos, dpi, fecha_nacimiento, telefono, direccion, "
                + "puesto, fecha_contratacion, salario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenar(ps, d);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, PeticionEmpleado d) throws SQLException {
        String sql = "UPDATE empleado SET nombres = ?, apellidos = ?, dpi = ?, fecha_nacimiento = ?, "
                + "telefono = ?, direccion = ?, puesto = ?, fecha_contratacion = ?, salario = ? "
                + "WHERE id_empleado = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenar(ps, d);
            ps.setInt(10, id);
            ps.executeUpdate();
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE empleado SET estado = ? WHERE id_empleado = ?")) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private int poner(PreparedStatement ps, String buscar, String puesto, String estado) throws SQLException {
        String patron = "%" + buscar + "%";
        ps.setString(1, buscar);
        ps.setString(2, patron);
        ps.setString(3, patron);
        ps.setString(4, puesto);
        ps.setString(5, puesto);
        ps.setString(6, estado);
        ps.setString(7, estado);
        return 8;
    }

    private void llenar(PreparedStatement ps, PeticionEmpleado d) throws SQLException {
        ps.setString(1, d.getNombres());
        ps.setString(2, d.getApellidos());
        ps.setString(3, d.getDpi());
        ps.setString(4, d.getFechaNacimiento());
        ps.setString(5, d.getTelefono());
        ps.setString(6, d.getDireccion());
        ps.setString(7, d.getPuesto());
        ps.setString(8, d.getFechaContratacion());
        ps.setBigDecimal(9, d.getSalario());
    }

    private Empleado leer(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getInt("id_empleado"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dpi"),
                rs.getString("fecha_nacimiento"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getString("puesto"),
                rs.getString("fecha_contratacion"),
                rs.getBigDecimal("salario"),
                rs.getString("estado"));
    }
}