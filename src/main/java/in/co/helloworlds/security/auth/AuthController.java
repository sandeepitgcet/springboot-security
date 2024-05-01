package in.co.helloworlds.security.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.SecretKey;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired
  private AuthenticationService service;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(
      @RequestBody RegisterRequest request) {
    log.info("register()");
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    log.info("authenticate()");
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(
      @RequestBody AuthenticationRequest request) {
    log.info("refresh-token()");
    return ResponseEntity.ok(service.refreshToken(request));
  }

  @GetMapping("/test")
  @Cacheable("test")
  public String buildANdValidateToken(@RequestParam String val) {
    System.out.println("AuthController.buildANdValidateToken");
//    System.out.println(redisTemplate.opsForValue().get("name"));
//    throw new RuntimeException();
    return val+" Helllo";
  }

}