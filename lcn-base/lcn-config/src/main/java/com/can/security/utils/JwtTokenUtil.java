package com.can.security.utils;

import com.can.security.user.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @description:
 * @author: LCN
 * @date: 2018-06-13 21:21
 */

public class JwtTokenUtil {

	/** 生成jwt的密钥 */
	@Value("${jwt.secret:DefaultSecret}")
	private String secret;

	/** 生成jwt第二部分的sub */
	private final static String CLAIM_KEY_USERNAME = "sub";

	/** 生成jwt第二部分的created */
	private final static String CLAIM_KEY_CREATED = "iat";

	/** 时间工具 */
	private Clock clock = DefaultClock.INSTANCE;

	/** 加密算法 */
	private final static SignatureAlgorithm SIGNATUREALGORITHM;

	static {
		// 加密算法这里采用 HS512
		SIGNATUREALGORITHM = SignatureAlgorithm.HS512;
	}

	/**
	 * 从token里面获取用户名
	 * @param token
	 *
	 * @return
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 *  从token里面获取发布的日期
	 * @param token
	 *
	 * @return
	 */
	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	/**
	 * 从toekn里面获取过期的日期
	 * @param token
	 *
	 * @return
	 */
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * 从token里面获取claim(声明)
	 * @param token
	 * @param claimsResolver
	 * @param <T>
	 * @return
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * 生成 token
	 * @param userName 用户名
	 * @param expiration	token过期时间 单位(秒)
	 * @return
	 */
	public String generateToken(String userName, Long expiration) {

		Map<String, Object> claims = new HashMap<>(32);

		claims.put(CLAIM_KEY_USERNAME, userName);
		claims.put(CLAIM_KEY_CREATED, new Date());

		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(generateExpirationDate(expiration))
				.signWith(SIGNATUREALGORITHM, secret)
				.compact();
	}

	/**
	 * token是否能被刷新
	 *
	 * @param token
	 * @param lastPasswordReset
	 * @return
	 */
	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = getIssuedAtDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
				&& (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	/**
	 * 刷新token
	 *
	 * @param token
	 * @param exp
	 * @return
	 */
	public String refreshToken(String token, Long exp) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate, exp);

		final Claims claims = getAllClaimsFromToken(token);
		claims.setIssuedAt(createdDate);
		claims.setExpiration(expirationDate);

		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	/**
	 * 验证用户的token是否可以使用
	 *
	 * @param token
	 * @param userDetails
	 * @return
	 */
	public boolean validateToken(String token, UserDetails userDetails) {

		JwtUser user = (JwtUser) userDetails;
		final String username = getUsernameFromToken(token);
		final Date created = getIssuedAtDateFromToken(token);
		return (
				username.equals(user.getUsername())
						&& !isTokenExpired(token)
						&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
		);
	}

	/**
	 *	从token里面获取声明(claim)
	 *
	 * @param token
	 * @return
	 */
	private Claims getAllClaimsFromToken(String token) {

		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	/**
	 * 判断token是否过期
	 *
	 * @param token
	 * @return
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(clock.now());
	}

	/**
	 * 判断token的创建日期是否在密码最后一次重置之前
	 * @param created
	 *
	 * @param lastPasswordReset 上次密码重置时间
	 * @return
	 */
	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	/**
	 * 是否获取jwt的过期时间，
	 *
	 * @param token
	 * @return
	 */
	private Boolean ignoreTokenExpiration(String token) {
		return false;
	}

	/**
	 * 生成token过期的时间
	 *
	 * @param expiration 多久过期(单位：秒)
	 * @return
	 */
	private Date generateExpirationDate(Long expiration) {
		return new Date(System.currentTimeMillis() + expiration * 1000);
	}

	/**
	 * 生成token
	 *
	 * @param claims  声明
	 * @param subject 主题
	 * @param expiration 多久过期(单位：秒)
	 * @return
	 */
	private String doGenerateToken(Map<String, Object> claims, String subject, Long expiration) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate, expiration);

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SIGNATUREALGORITHM, secret)
				.compact();
	}

	/**
	 * 生成token过期的时间
	 * @param createdDate 开始时间
	 * @param expiration 多久过期(单位：秒)
	 *
	 * @return
	 */
	private Date calculateExpirationDate (Date createdDate, Long expiration) {
		return new Date(createdDate.getTime() + expiration * 1000);
	}
}
