package in.co.helloworlds.security.auth;

import in.co.helloworlds.security.config.JWTService;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

	public RegisterResponse register(RegisterRequest request) {
		log.info("register() Service");
		//Check if Email is registered or not
		Optional<User>  user = userRepository.findByEmail(request.getEmail());
		if(user.isPresent()) {
			throw new BadCredentialsException("Email already in use");
		}

		//Save new User to DB
		User newUser = User.builder()
				.firstname(request.getFirstName())
				.lastname(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.build();
		userRepository.save(newUser);

		return RegisterResponse.builder()
				.statusCode(HttpStatus.ACCEPTED.value()).
				message("Registration Successful")
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		log.info("authenticate() Service");

//		// Retrieve user once
//		User user = userRepository.findByEmail(request.getEmail())
//				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

		// Authenticate
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()));
//
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// Generate token
		String token = jwtService.generateToken(user);
		return AuthenticationResponse.builder().token(token).build();


	}

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
		jwtService.blackListToken(token);
	}
}
