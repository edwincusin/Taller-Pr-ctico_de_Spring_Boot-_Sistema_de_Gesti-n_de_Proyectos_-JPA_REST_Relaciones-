package com.krakedev.proyectos.security;

import java.io.IOException;
import java.util.Collections;

import org.apache.catalina.filters.ExpiresFilter.XHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.services.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenBlacklistService blackList;
	
	public JwtAuthenticationFilter(TokenBlacklistService blackList) {
		super();
		this.blackList = blackList;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader=request.getHeader("Authorization");
		
		if(authHeader==null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token=authHeader.substring(7);
		
		if(blackList.estaInvalido(token)) {
			response.setStatus(XHttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Acceso denegado: session esta cerrada");
			return;
		}
		
		DecodedJWT datosToken=JwtUtil.validarToken(token);
		
		if(datosToken!=null) {
			String username=datosToken.getSubject();
			String rolOriginal=datosToken.getClaim("rol").asString();
			String rolSpring="ROLE_"+rolOriginal;
			
			SimpleGrantedAuthority authority=new SimpleGrantedAuthority(rolSpring);
			// Crea un objeto Authentication con:
	        // - usuario autenticado
	        // - contraseña nula (porque ya se validó el JWT)
	        // - lista de autoridades del usuario
			UsernamePasswordAuthenticationToken authentication=
					new UsernamePasswordAuthenticationToken(username, null,Collections.singleton(authority));
			// Guarda la autenticación en el contexto de seguridad de Spring
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		// Continúa con el siguiente filtro o controlador de la cadena
	    filterChain.doFilter(request, response);
	}

}
