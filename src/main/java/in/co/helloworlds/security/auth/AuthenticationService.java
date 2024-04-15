package in.co.helloworlds.security.auth;

import in.co.helloworlds.security.config.JWTService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.co.helloworlds.security.user.User;
import in.co.helloworlds.security.user.UserRepository;

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
		System.out.println("AuthenticationService.register");
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
		var xx = userRepository.save(user);
		System.out.println(xx);
		String token = jwtService.generateToken(map, user);
		return AuthenticationResponse.builder().token(token).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		System.out.println("AuthenticationService.authenticate");
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()));
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		String token = jwtService.generateToken(user);
		return AuthenticationResponse.builder().token(token).build();
	}

	public AuthenticationResponse refreshToken(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()));
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		String token = jwtService.generateRefreshToken(user);
		return AuthenticationResponse.builder().token(token).build();
	}
}
