package in.co.helloworlds.security.auth;

import in.co.helloworlds.security.config.JWTService;

import java.util.HashMap;
import java.util.Optional;

import in.co.helloworlds.security.config.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.co.helloworlds.security.user.User;
import in.co.helloworlds.security.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JWTService jwtService;

	private final AuthenticationManager authenticationManager;

	private final RedisService redisService;

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

		// Authenticate
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()));
//
		UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// Generate token
		String token = jwtService.generateToken(user);

		redisService.setValue(user.getUsername(), user);
		return AuthenticationResponse.builder().token(token).build();


	}

	@CacheEvict("token")
	public void logout() {
		log.info("logout() Service");
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		redisService.deleteKey(userName);
	}
}
