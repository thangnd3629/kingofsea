package com.supergroup.admin.domain.provider;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.supergroup.core.utils.DateUtil;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class AdminJwtTokenProvider {

    @Value("${jwt.secret}")
    private String JWT_SECRET ;
    @Value("${jwt.expiry.access.user}")
    private int    EXPIRY_USER_ACCESS_TOKEN; // min
    @Value("${jwt.expiry.access.admin}")
    private int    EXPIRY_ADMIN_ACCESS_TOKEN; // min

    private final String ACCOUNT_ID = "account_id";

    public String generateToken(Long accountId, Date expiryDate) {
        Date now = new Date();
        Key key = getHmacKey();
        var jwt = Jwts.builder()
                      .claim(ACCOUNT_ID, accountId)
                      .setIssuedAt(now)
                      .signWith(key);
        if (expiryDate != null) {jwt.setExpiration(expiryDate);}
        return jwt.compact();
    }

    public Long getAccountId(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                                  .setSigningKey(getHmacKey())
                                  .build();
        var body = jwtParser.parseClaimsJws(token).getBody();
        return body.get(ACCOUNT_ID, Long.class);
    }

    private Key getHmacKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(JWT_SECRET), SignatureAlgorithm.HS256.getJcaName());
    }

    private Date getExpiredDateByDuration(int duration) {
        LocalDateTime date = LocalDateTime.now().plusMinutes(duration);
        return DateUtil.convertToDateViaInstant(date);
    }


    public String generateUserAccessToken(Long userId) {
        return generateAccessToken(userId, EXPIRY_USER_ACCESS_TOKEN);
    }

    public String generateAdminAccessToken(Long adminId) {
        return generateAccessToken(adminId, EXPIRY_ADMIN_ACCESS_TOKEN);
    }

    public String generateAccessToken(Long accountId, int duration) {
        return generateToken(accountId, this.getExpiredDateByDuration(duration));
    }

    public String generateRefreshToken(Long accountId) {
        return generateToken(accountId, null);
    }

    public Optional<String> getJwt(HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return getJwtFromAuthorization(authorization);
    }

    private Optional<String> getJwtFromAuthorization(String authorization) {
        return getJwt(authorization);
    }

    /**
     * WARNING: This method is only allowed to call when token is filtered in JwtTokenFilter
     */
    public String getToken(String authorization) {
        return authorization.split(" ")[1].trim();
    }

    private Optional<String> getJwt(String authorization) {
        return StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")
               ? Optional.of(authorization.split(" ")[1].trim())
               : Optional.empty();
    }
}
