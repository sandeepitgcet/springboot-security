package in.co.helloworlds.security.config;

import in.co.helloworlds.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import in.co.helloworlds.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationConfig {

	private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Bean
	public UserDetailsService userDetailsService() {
		log.info("userDetailsService()");
		return (username) ->  {
				UserDetails userDetails = userRepository
											.findByEmail(username)
											.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
			SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
				return userDetails;
			};
		};

//		return (username) -> {
//			UserDetails userDetails = (UserDetails)redisService.getValue(username);
//			if(userDetails == null) {
//				log.info("User not found in cache");
//				userDetails =  userRepository
//						.findByEmail(username)
//						.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//			}
//			log.info("User found in cache");
//			// Set user details in the security context
//			SecurityContextHolder.getContext().setAuthentication(
//					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
//			return userDetails;

	@Bean
	public PasswordEncoder passwordEncoder() {
		log.info("passwordEncoder()");
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		log.info("authenticationProvider()");
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		log.info("authenticationManager()");
		return configuration.getAuthenticationManager();
	}

}
