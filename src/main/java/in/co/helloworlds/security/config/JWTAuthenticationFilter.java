package in.co.helloworlds.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.co.helloworlds.security.exception.MyErrorResponse;
import in.co.helloworlds.security.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final JWTService jwtService;

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
			filterChain.doFilter(request, response);
		}catch (Exception e){
			log.error(e.getMessage(),e);
			MyErrorResponse errorResponse = new MyErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Set content type to JSON
			objectMapper.writeValue(response.getWriter(), errorResponse);

		}
	}

}
