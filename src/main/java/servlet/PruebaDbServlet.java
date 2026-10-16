package servlet;

import config.DBConnectionSingleton;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/api/prueba-db")
public class PruebaDbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM rol")) {
            rs.next();
            resp.getWriter().println("Conectado. Roles en la base: " + rs.getInt(1));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("Error: " + e.getMessage());
        }
    }
}