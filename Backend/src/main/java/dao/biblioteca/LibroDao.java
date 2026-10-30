package dao.biblioteca;

import config.DBConnectionSingleton;
import dto.biblioteca.PeticionLibro;
import model.biblioteca.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibroDao {

    private static final String SELECT_BASE = "SELECT id_libro, titulo, autor, editorial, cantidad_total, "
            + "cantidad_disponible, activo FROM libro ";

    private static final String FILTRO = "WHERE (? = '' OR titulo LIKE ? OR autor LIKE ? OR editorial LIKE ?) "
            + "AND (? = '' OR (? = 'ACTIVO' AND activo = 1) OR (? = 'INACTIVO' AND activo = 0)) ";

    public List<Libro> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = SELECT_BASE + FILTRO + "ORDER BY titulo LIMIT ? OFFSET ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = poner(ps, buscar, estado);
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

    public int contar(String buscar, String estado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM libro " + FILTRO;
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            poner(ps, buscar, estado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public Libro buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE id_libro = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? leer(rs) : null;
            }
        }
    }

    public boolean existe(String titulo, String autor, String editorial, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM libro WHERE titulo = ? AND autor = ? AND editorial = ? AND id_libro <> ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setString(3, editorial);
            ps.setInt(4, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int prestamosActivos(int idLibro) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE id_libro = ? AND estado = 'ACTIVO'";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int crear(PeticionLibro d) throws SQLException {
        String sql = "INSERT INTO libro (titulo, autor, editorial, cantidad_total, cantidad_disponible) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getTitulo());
            ps.setString(2, d.getAutor());
            ps.setString(3, d.getEditorial());
            ps.setInt(4, d.getCantidadTotal());
            ps.setInt(5, d.getCantidadTotal());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void actualizar(int id, PeticionLibro d, int disponible) throws SQLException {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, editorial = ?, cantidad_total = ?, "
                + "cantidad_disponible = ? WHERE id_libro = ?";
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getTitulo());
            ps.setString(2, d.getAutor());
            ps.setString(3, d.getEditorial());
            ps.setInt(4, d.getCantidadTotal());
            ps.setInt(5, disponible);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    public void cambiarActivo(int id, boolean activo) throws SQLException {
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE libro SET activo = ? WHERE id_libro = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private int poner(PreparedStatement ps, String buscar, String estado) throws SQLException {
        String patron = "%" + buscar + "%";
        ps.setString(1, buscar);
        ps.setString(2, patron);
        ps.setString(3, patron);
        ps.setString(4, patron);
        ps.setString(5, estado);
        ps.setString(6, estado);
        ps.setString(7, estado);
        return 8;
    }

    private Libro leer(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id_libro"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                rs.getInt("cantidad_total"),
                rs.getInt("cantidad_disponible"),
                rs.getBoolean("activo"));
    }
}