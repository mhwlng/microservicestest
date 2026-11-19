package com.mhwlng.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  private final String issuer;
  private final String audience;

  public JwtUtil(@Value("${jwt.secret}") String secret,
                 @Value("${jwt.issuer}") String issuer,
                 @Value("${jwt.audience}") String audience) {
    byte[] keyBytes = Base64.getDecoder()
        .decode(secret.getBytes(StandardCharsets.UTF_8));
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);

    this.issuer = issuer;
    this.audience = audience;
  }

  public String generateToken(String email, String role) {
    return Jwts.builder()
        .issuer(issuer)
        .audience().add(audience).and()
        .subject(email)
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10)) // 10 hours
        .signWith(secretKey)
        .compact();
  }

  public void validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey)
          .requireIssuer(issuer)
          .requireAudience(audience)      
          .build()
          .parseSignedClaims(token);
    } catch (SignatureException e) {
      throw new JwtException("Invalid JWT signature");
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT");
    }
  }
}
