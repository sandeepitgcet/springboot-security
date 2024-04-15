package in.co.helloworlds.security.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired
  private AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request) {
    System.out.println("AuthController.register");
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.refreshToken(request));
  }

  @PostMapping("/test")
  public void buildANdValidateToken(@RequestBody RegisterRequest request) {
    System.out.println("AuthController.buildANdValidateToken");

    System.out.println(request.toString());
    HashMap<String, Object> claims = new HashMap<String, Object>();
    claims.put("email", request.getEmail());
    claims.put("firstName", request.getFirstName());
    claims.put("lastName", request.getLastName());
    claims.put("password", request.getPassword());

    MacAlgorithm alg = Jwts.SIG.HS512; // or HS384 or HS256
    SecretKey secretKey = alg.key().build();
    System.out.println(secretKey.getEncoded().toString());

    System.out.println(alg.key().build() == alg.key().build());

    String token = Jwts.builder()
        .claims(new HashMap<>())
        .subject(request.getEmail())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 360000))
        .signWith(secretKey)
        .compact();

    System.out.println("Token :" + token);

    var payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

    System.out.println(payload);
  }

}