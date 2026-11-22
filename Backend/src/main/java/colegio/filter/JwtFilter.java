
package colegio.filter;

import colegio.services.JwtTokenService;
import colegio.util.Json;
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

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            Json.error(response, 401, "No se envio el token");
            return;
        }

        String token = authorization.substring(7).trim();

        if (token.isEmpty()) {
            Json.error(response, 401, "El token esta vacio");
            return;
        }

        try {
            Jws<Claims> jwt = jwtService.validate(token);
            Claims datos = jwt.getPayload();

            String usuario = datos.getSubject();
            String rol = datos.get("rol", String.class);
            Number id = datos.get("id", Number.class);

            if (usuario == null || usuario.isBlank() || rol == null || rol.isBlank() || id == null) {
                Json.error(response, 401, "Token invalido");
                return;
            }

            request.setAttribute("usuarioId", id.intValue());
            request.setAttribute("username", usuario);
            request.setAttribute("rol", rol);

            chain.doFilter(req, res);

        } catch (JwtException | IllegalArgumentException e) {
            Json.error(response, 401, "Token invalido o expirado");
        }
    }
}
