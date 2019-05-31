package cn.edu.bupt.util.token;

import cn.edu.bupt.bean.vo.Identity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Slf4j
public class TokenUtil {

    public static String createToken(Identity identity, String apiKeySecret){
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKeySecret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(String.valueOf(identity.getId()))
                .setIssuedAt(now)
                .setSubject(identity.getId() + "/" + identity.getClientId())
                .setIssuer(identity.getIssuer())
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        long ttlMillis = identity.getDuration();
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
            identity.setDuration(exp.getTime());
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Identity parseToken(String token, String apiKeySecret){
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(apiKeySecret))
                .parseClaimsJws(token).getBody();

        String[] subjectInfos = claims.getSubject().split("/");
        String id = subjectInfos[0];
        String clientId = subjectInfos[1];

        // 封装成pojo
        Identity identity = new Identity();
        identity.setId(Long.parseLong(id));
        identity.setClientId(clientId);
        identity.setDuration(claims.getExpiration().getTime());

        return identity;
    }

    public static void main(String[] args) {
        Identity identity = parseToken("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwIiwiaWF0IjoxNTU4OTQ1MTM1LCJzdWIiOiIwLzEyM0BnbWFpbC5jb20iLCJpc3MiOiJhdXhfdmVyaWZpY2F0aW9uIiwiZXhwIjoxNTU4OTUxMTM1fQ.-zEwZnvzVEgrb8HFb2PU-2JXrolCeBw2Z68y0_iylDo",
                "bupt_verification_510108199311080018");
        System.out.println(identity);
//        Identity identity = new Identity();
//        identity.setId(1L);
//        identity.setClientId("liwangadd@gmail.com");
//        identity.setDuration(600000L);
//        identity.setIssuer("aux verification");
//        System.out.println(TokenUtil.createToken(identity, "bupt_verification_510108199311080018"));
    }

}
