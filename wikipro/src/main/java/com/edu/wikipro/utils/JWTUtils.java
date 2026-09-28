package com.edu.wikipro.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Date;

public class JWTUtils {
    private static final long EXPIRE = 1800000;
    // JWT签名密钥：请替换为你自己的随机长字符串（至少32位），切勿使用默认值
    private static final String SECRET = "CHANGE_ME_TO_YOUR_OWN_RANDOM_SECRET_KEY_AT_LEAST_32_CHARS";

    public static String createToken(String username){
        long now = System.currentTimeMillis();
        Date expireDate = new Date(now + EXPIRE);
        Algorithm alg = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(expireDate)
                .sign(alg);
    }

    public static boolean verifyToken(String token){
        try{
            Algorithm alg = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(alg).build();
            verifier.verify(token);
            return true;
        }catch (JWTVerificationException e){
            return false;
        }
    }

    public static String getUsername(String token){
        try{
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        }catch (JWTDecodeException e){
            return null;
        }
    }
}
