package in.co.helloworlds.security.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

@Service
public class JWTService {

	@Value("${application.security.jwt.secret-key}fsfd4432dsfdsf")
	private String secretKey;
	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;
	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

	private SecretKey getSignInKey() {
		// byte[] decodedKey = Decoders.BASE64.decode(secretKey);
		// return Keys.hmacShaKeyFor(decodedKey);

		// MacAlgorithm alg = Jwts.SIG.HS512; // or HS384 or HS256
		// return alg.key().build();

		return Keys.hmacShaKeyFor(secretKey.getBytes());

	}

	private Claims extractAllClaims(String token) {
		System.out.println("Tokken: " + token + " \n" + getSignInKey().getFormat() + "\n" + getSignInKey().getFormat());
		return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();

	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private String buildToken(
			Map<String, String> extraClaims,
			UserDetails userDetails,
			long expiration) {
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey())
				.compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenExpired(String token) {
		final Date expiration = extractClaim(token, Claims::getExpiration);
		return expiration.before(new Date());
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public String generateToken(UserDetails userDetails) {
		System.out.println(userDetails);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("email", userDetails.getUsername());
		map.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
		return generateToken(map, userDetails);
	}

	public String generateToken(
			Map<String, String> extraClaims,
			UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	public String generateRefreshToken(
			UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, refreshExpiration);
	}
}
