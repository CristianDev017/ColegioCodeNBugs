package colegio.filter;

import colegio.services.JwtTokenService;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/api/*")
public class JwtFilter implements Filter {

    private final JwtTokenService jwtService = new JwtTokenService();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String ruta = request.getServletPath();

        if (ruta.equals("/api/auth/login") || request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(req, res);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\":\"No se envio el token\"}");
            return;
        }
        String token = authorization.substring(7).trim();

        if (token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\":\"El token esta vacio\"}");
            return;
        }

        try {
            Jws<Claims> jwt = jwtService.validate(token);
            Claims datos = jwt.getPayload();

            String usuario = datos.getSubject();
            String rol = datos.get("rol", String.class);
            Number id = datos.get("id", Number.class);

            if (usuario == null || usuario.isBlank() || rol == null || rol.isBlank() || id == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"mensaje\":\"Token invalido\"}");
                return;
            }

            request.setAttribute("usuarioId", id.intValue());
            request.setAttribute("username", usuario);
            request.setAttribute("rol", rol);

            chain.doFilter(req, res);

        } catch (JwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\":\"Token invalido o expirado\"}");
        }
    }
}
