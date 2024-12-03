package reservation.Security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reservation.Utils.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//call 1 time per request
	@Autowired
	JwtUtil jwtUtil;

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			// Authorization: Bearer token
			// token begins with index 7
			String token = authHeader.substring(7);
			try {
				Claims claims = jwtUtil.validateToken(token);
				String username = claims.getSubject();
				// return string for claim role
				// List<String> permissions = claims.get("permissions", List.class);
				String role = claims.get("role", String.class);
				if (role == null) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().write("{\"error\": \"Role is not found\"}");
					return;
				}
				// SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
				List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					// @PreAuthorize("hasRole('ADMIN')")
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							username, null, authorities);
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

				}
			} catch (ExpiredJwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Token expired\"}");
				return;
			} catch (JwtException e) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Invalid token\"}");
				return;
			}
		}
		filterChain.doFilter(request, response);

	}
}
