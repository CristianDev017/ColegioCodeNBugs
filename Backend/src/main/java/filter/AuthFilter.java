package filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import util.Json;
import util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter("/api/*")
public class AuthFilter extends HttpFilter {

    private static final Set<String> RUTAS_PUBLICAS = Set.of(
            "/api/auth/login",
            "/api/auth/recuperar",
            "/api/auth/restablecer");

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        if (RUTAS_PUBLICAS.contains(req.getServletPath())) {
            chain.doFilter(req, resp);
            return;
        }

        String cabecera = req.getHeader("Authorization");
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            Json.error(resp, 401, "Debe iniciar sesión");
            return;
        }

        DecodedJWT jwt = JwtUtil.verificar(cabecera.substring(7));
        if (jwt == null) {
            Json.error(resp, 401, "La sesión expiró o no es válida");
            return;
        }

        req.setAttribute("usuarioId", jwt.getClaim("id").asInt());
        req.setAttribute("username", jwt.getClaim("username").asString());
        req.setAttribute("rol", jwt.getClaim("rol").asString());

        chain.doFilter(req, resp);
    }
}