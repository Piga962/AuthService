package com.Oracle.AuthService.infra.security;

import com.Oracle.AuthService.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${jwt.secret.oracle}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Oracle Project")
                    .withClaim("id",user.getUser_id())
                    .withClaim("role",user.getRole())
                    .withClaim("telegramChatId",user.getTelegramChatId())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        }catch (JWTCreationException e){
            return null;
        }
    }

    public Long getUserId(String token){
        if(token == null){
            throw new RuntimeException();
        }
        DecodedJWT verifier = null;
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            verifier = JWT.require(algorithm)
                    .withIssuer("Oracle Project")
                    .build()
                    .verify(token);
        }catch(JWTVerificationException e){
            System.out.println(e.toString());
        }
        Long id = verifier.getClaim("id").asLong();
        if(id == null){
            throw new RuntimeException("Invalid verifier");
        }
        return id;
    }

    public Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-06:00"));
    }

}
