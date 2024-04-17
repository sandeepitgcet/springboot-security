package in.co.helloworlds.security.auth;

import in.co.helloworlds.security.config.JWTService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.co.helloworlds.security.user.User;
import in.co.helloworlds.security.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		log.info("register() Service");
		HashMap<String, String> map = new HashMap<>();
		String encodedPassString = passwordEncoder.encode(request.getPassword());
		map.put("firstname", request.getFirstName());
		map.put("lastname", request.getLastName());
		map.put("email", request.getEmail());
		map.put("password", encodedPassString);
		User user = User.builder()
				.firstname(request.getFirstName())
				.lastname(request.getLastName())
				.email(request.getEmail())
				.password(encodedPassString)
				.build();
		userRepository.save(user);
		String token = jwtService.generateToken(map, user);
		return AuthenticationResponse.builder().token(token).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		log.info("authenticate() Service");
		try {
			// Retrieve user once
			User user = userRepository.findByEmail(request.getEmail())
					.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

			// Authenticate
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),
							request.getPassword()));

			// Generate token
			String token = jwtService.generateToken(user);
			return AuthenticationResponse.builder().token(token).build();
		} catch (BadCredentialsException e) {
			log.error(e.getMessage());
			return AuthenticationResponse.builder().error(e.getMessage()).build();
		} catch (UsernameNotFoundException e) {
			log.error(e.getMessage());
			return AuthenticationResponse.builder().error(e.getMessage()).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return AuthenticationResponse.builder().error(e.getMessage()).build();
		}
	}

	// public AuthenticationResponse authenticate(AuthenticationRequest request) {
	// log.info("authenticate()");
	// try {
	// authenticationManager.authenticate(
	// new UsernamePasswordAuthenticationToken(
	// request.getEmail(),
	// request.getPassword()));
	// } catch (BadCredentialsException e) {
	// log.error(e.getMessage());
	// return AuthenticationResponse.builder().error(e.getMessage()).build();
	// } catch (Exception e) {
	// log.error(e.getMessage(), e);
	// return AuthenticationResponse.builder().error(e.getMessage()).build();
	// }
	// User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
	// String token = jwtService.generateToken(user);
	// return AuthenticationResponse.builder().token(token).build();
	// }

	public AuthenticationResponse refreshToken(AuthenticationRequest request) {
		log.info("RefreshToken() Service");
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()));
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		String token = jwtService.generateRefreshToken(user);
		return AuthenticationResponse.builder().token(token).build();
	}

	public void logout(String token) {
		log.info("logout() Service");
		jwtService.deleteAllTokens(token);
	}
}
