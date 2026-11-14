
package colegio.services;

import colegio.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtTokenService {

    private static final String SECRET = "clave-secreta-para-mi-colegio-de-IPC2-2026";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generate(Usuario usuario) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(2, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> validate(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
