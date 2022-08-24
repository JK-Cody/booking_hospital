package com.hospital.common.utils;

import com.alibaba.excel.util.StringUtils;
import io.jsonwebtoken.*;

import java.util.Date;

/**
 * JWT的token工具
 */
public class JwtToken {

    private static long tokenExpiration = 24*60*60*1000;
    private static String tokenSignKey = "123456";  //Token的键值

    public static void main(String[] args) {

        String token = JwtToken.createToken(1L, "55");    //1L即long型的1
        System.out.println(token);
        System.out.println(JwtToken.getUserId(token));
        System.out.println(JwtToken.getUserName(token));
    }

    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("Client-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }


    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
    }


    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }
}
