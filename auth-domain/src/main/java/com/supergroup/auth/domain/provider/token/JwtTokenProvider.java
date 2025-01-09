package com.supergroup.auth.domain.provider.token;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.VerifySession;
import com.supergroup.core.utils.DateUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Component
public class JwtTokenProvider {

    public static final String USER_ID       = "user_id";
    public static final String SESSION_ID    = "session_id";
    public static final String ACCOUNT_ID    = "account_id";
    public static final String VERIFY_ID     = "verify_id";
    public static final String VERIFY_REASON = "verify_reason";
    public static final String ISSUER        = "supergroup";
    public static final String NEW_EMAIL     = "new_email";
    public static final String UUID          = "uuid";

    @Value("${jwt.secret}")
    public String JWT_SECRET;
    @Value("${jwt.expiry.access.user}")
    public Long   EXPIRY_USER_ACCESS_TOKEN;
    @Value("${jwt.expiry.verify-session}")
    public Long   EXPIRY_VERIFY_TOKEN;

    /**
     * Generate token
     *
     * @param claims: set of claim
     * @param expiryDate: expiry date
     */
    public String generateToken(Map<String, ?> claims, Date expiryDate) {
        Date now = new Date();
        Key key = getHmacKey();
        var jwt = Jwts.builder()
                      .setIssuer(ISSUER)
                      .setClaims(claims)
                      .setIssuedAt(now)
                      .signWith(key);
        if (expiryDate != null) {
            jwt.setExpiration(expiryDate);
        }
        return jwt.compact();
    }

    /**
     * Get claim in access token
     *
     * @param token: access token
     */
    public AccessTokenClaim getAccessTokenClaim(String token) {
        var jwtParser = Jwts.parserBuilder()
                            .setSigningKey(getHmacKey())
                            .build();
        var body = jwtParser.parseClaimsJws(token).getBody();
        return new AccessTokenClaim(
                body.get(USER_ID, Long.class),
                body.get(SESSION_ID, Long.class),
                body.get(UUID, String.class)
        );
    }

    /**
     * Generate user access token
     *
     * @param userId: id of user
     * @param sessionId: id of login session
     */
    public String generateUserAccessToken(Long userId, Long sessionId, String uuid) {
        return generateToken(
                Map.of(USER_ID, userId,
                       UUID, uuid,
                       SESSION_ID, sessionId),
                this.getExpiredDateByDuration(EXPIRY_USER_ACCESS_TOKEN));
    }

    /**
     * Generate refresh access token
     *
     * @param userId: id of user
     * @param sessionId: id of login session
     */
    public String generateRefreshToken(Long userId, Long sessionId) {
        return generateToken(Map.of(USER_ID, userId, SESSION_ID, sessionId), null);
    }

    public RefreshTokenClaim getRefreshTokenClaim(String refreshToken) {
        var jwtParser = Jwts.parserBuilder()
                            .setSigningKey(getHmacKey())
                            .build();
        var body = jwtParser.parseClaimsJws(refreshToken).getBody();
        return new RefreshTokenClaim(
                body.get(USER_ID, Long.class),
                body.get(SESSION_ID, Long.class));
    }

    /**
     * Generate verify token
     *
     * @param verifySession: verify session you want to generate
     */
    public String generateVerifyToken(VerifySession verifySession) {
        var claims = Map.of(
                ACCOUNT_ID, verifySession.getAccountId(),
                VERIFY_ID, verifySession.getId(),
                VERIFY_REASON, verifySession.getReason().name());
        return generateToken(claims, this.getExpiredDateByDuration(EXPIRY_VERIFY_TOKEN));
    }

    public VerifyTokenClaim getVerifyTokenClaim(String verifyToken) {
        var jwtParser = Jwts.parserBuilder()
                            .setSigningKey(getHmacKey())
                            .build();
        var body = jwtParser.parseClaimsJws(verifyToken).getBody();
        return new VerifyTokenClaim(
                body.get(ACCOUNT_ID, Long.class),
                body.get(VERIFY_ID, Long.class),
                VerifyReason.valueOf(body.get(VERIFY_REASON, String.class)));
    }

    private Key getHmacKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(JWT_SECRET), SignatureAlgorithm.HS256.getJcaName());
    }

    private Date getExpiredDateByDuration(Long duration) {
        LocalDateTime date = LocalDateTime.now().plusMinutes(duration);
        return DateUtil.convertToDateViaInstant(date);
    }

    public Claims getBodyToken(String token) {
        var jwtParser = Jwts.parserBuilder()
                            .setSigningKey(getHmacKey())
                            .build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String generateChangeEmailVerifyToken(VerifySession verifySession, String email) {
        var claims = Map.of(
                ACCOUNT_ID, verifySession.getAccountId(),
                VERIFY_ID, verifySession.getId(),
                VERIFY_REASON, verifySession.getReason().name(),
                NEW_EMAIL, email);
        return generateToken(claims, this.getExpiredDateByDuration(EXPIRY_VERIFY_TOKEN));
    }
}
