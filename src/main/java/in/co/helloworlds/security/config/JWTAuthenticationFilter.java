package in.co.helloworlds.security.config;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.co.helloworlds.security.exception.MyErrorResponse;
import in.co.helloworlds.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.co.helloworlds.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final JWTService jwtService;

	private final UserDetailsService userDetailsService;

	private final TokenRepository tokenRepository;

	private final RedisService redisService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws IOException {
		log.info("OncePerRequestFilter --> doFilterInternal()");
		try{
			final String authHeader = request.getHeader("Authorization");
			final String jwt;
			final String userEmail;
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				log.error("doFilterInternal() --> Auth header missing");
				filterChain.doFilter(request, response);
				return;
			}
            jwt = authHeader.substring(7);
			userEmail = jwtService.extractUsername(jwt);
			User user = (User)redisService.getValue(userEmail);
			if(user == null) {
				log.error("doFilterInternal() --> User not found");
				throw new UsernameNotFoundException("User not found");
			}
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
			//log.info("UserDetails: " , user);
			//System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			filterChain.doFilter(request, response);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			MyErrorResponse errorResponse = new MyErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Set content type to JSON
			objectMapper.writeValue(response.getWriter(), errorResponse);

		}
//
//
//		Object cacheToken = redisService.getValue("token::"+userEmail);
//		if(cacheToken == null) {
//			// Either the token is expired or Invalid
//			throw new Error("Either the token is Expired or Invalid");
//		}
//		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//			log.info("UserDetails: " + userDetails.getUsername() + " " + userDetails.getPassword());
//			boolean isTokenValid = tokenRepository.findByToken(jwt)
//					.map(t -> !t.expired && !t.revoked)
//					.orElse(false);
//
//			if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
//				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//						userDetails,
//						null,
//						userDetails.getAuthorities());
//				authToken.setDetails(
//						new WebAuthenticationDetailsSource().buildDetails(request));
//				SecurityContextHolder.getContext().setAuthentication(authToken);
//			} else {
//				log.error("JWT token is invalid");
//			}
//		} else {
//			log.error("Else Auth == null or securityContextHolder == null");
//		}
//		log.info("JWTAuthenticationFilter.doFilterInternal  --> end");
//		filterChain.doFilter(request, response);


	}

}
