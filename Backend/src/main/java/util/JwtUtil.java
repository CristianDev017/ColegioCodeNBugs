package  util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {

    private static final String SECRETO = "codenbugs-colegio-secreto-largo-2026-ipc2";
    public static final int MINUTOS = 30;

    private static final Algorithm algoritmo = Algorithm.HMAC256(SECRETO);

    public static String crear(int id, String username, String rol) {
        long ahora = System.currentTimeMillis();
        return JWT.create()
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("rol", rol)
                .withIssuedAt(new Date(ahora))
                .withExpiresAt(new Date(ahora + MINUTOS * 60L * 1000L))
                .sign(algoritmo);
    }

    public static DecodedJWT verificar(String token) {
        try {
            return JWT.require(algoritmo).build().verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}