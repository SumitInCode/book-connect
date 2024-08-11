package com.ssuamkiett.BookConnect.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {
    private static final long JWT_ACCESS_TOKEN_EXPIRATION_MS = 7200 * 1000L; // 2 hours in milliseconds
    private static final long JWT_REFRESH_TOKEN_EXPIRATION_MS = 864000000L; // 15 days in milliseconds
    @Value("${user.config.jwt-secret-key}")
    private String JWT_SECRET_KEY;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(HashMap<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, JWT_ACCESS_TOKEN_EXPIRATION_MS);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, JWT_REFRESH_TOKEN_EXPIRATION_MS);
    }

    private String buildToken(HashMap<String, Object> extraClaims,
                              UserDetails userDetails, long expirationTime) {
         var authorities = userDetails.getAuthorities()
                 .stream()
                 .map(GrantedAuthority::getAuthority)
                 .toList();
         return Jwts.builder()
                 .setClaims(extraClaims)
                 .setSubject(userDetails.getUsername())
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + expirationTime ))
                 .claim("authorities", authorities)
                 .signWith(getSignInKey())
                 .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
