package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final UsuarioService servicio;

	public AuthController(UsuarioService servicio) {
		this.servicio = servicio;
	}
	
	@PostMapping("/registrar")
	public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario){
		
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(servicio.insertar(usuario));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR AL GUARDAR NUEVO USUARIO");
		}
	}
	

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioLogueado = servicio.login(usuario.getUsername(), usuario.getPassword());

			if (usuarioLogueado != null) {
				String token = JwtUtil.generarToken(usuarioLogueado.getUsername(), usuarioLogueado.getRol());
				return ResponseEntity.ok(Map.of("TOKEN",token));
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al logear");
		}

	}

	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil(
	        @RequestHeader("Authorization") String authHeader) {

	    try {
	    	System.out.println("Header recibido: [" + authHeader + "]");
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("Token no enviado o formato incorrecto");
	        }

	        String token = authHeader.substring(7);

	        DecodedJWT jwt = JwtUtil.validarToken(token);

	        if (jwt == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("Acceso denegado");
	        }

	        String usuario = jwt.getSubject();
	        String rol = jwt.getClaim("rol").asString();

	        return ResponseEntity.ok(
	                Map.of(
	                        "Mensaje", "Bienvenido",
	                        "Usuario", usuario,
	                        "Rol", rol,
	                        "Estatus", "Autenticado Exitosamente"));

	    } catch (Exception e) {
	        e.printStackTrace();

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al verPerfil");
	    }
	}

}
