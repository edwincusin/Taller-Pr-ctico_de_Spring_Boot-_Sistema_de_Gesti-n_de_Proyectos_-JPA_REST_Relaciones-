package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:5173") // esta permiido que se consuma desde la 
public class AuthController {

	private final UsuarioService servicio;
	private final TokenBlacklistService blackListService;
	private final UsuarioRepository repositorio;

	public AuthController(UsuarioService servicio, TokenBlacklistService blackListService, UsuarioRepository repositorio) {
		this.servicio = servicio;
		this.blackListService = blackListService;
		this.repositorio = repositorio;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {

		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(servicio.insertar(usuario));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR AL GUARDAR NUEVO USUARIO");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
		try {
			String username=credenciales.get("username");
			String password=credenciales.get("password");
			
			 if(username == null || password == null) {
		            return ResponseEntity.badRequest()
		                    .body("Username y password son obligatorios");
		        }
			
			Usuario usuarioLogueado = servicio.login(username, password);

			if (usuarioLogueado != null) {
			    String token = JwtUtil.generarToken(usuarioLogueado.getUsername(), usuarioLogueado.getRol());
			    return ResponseEntity.ok(Map.of("token", token, "rol", usuarioLogueado.getRol()));
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al logear");
		}

	}

	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil(@RequestHeader("Authorization") String authHeader) {

		try {
			System.out.println("Header recibido: [" + authHeader + "]");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no enviado o formato incorrecto");
			}

			String token = authHeader.substring(7);

			// validar si la session ya fue cerrada para no volver a invalidar algo cerrrado
			if (blackListService.estaInvalido(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Esta sesión ya fue cerrada, tienes que volver a logear");
			}

			DecodedJWT jwt = JwtUtil.validarToken(token);

			if (jwt == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado");
			}

			String usuario = jwt.getSubject();
			String rol = jwt.getClaim("rol").asString();

			return ResponseEntity.ok(Map.of("Mensaje", "Bienvenido", "Usuario", usuario, "Rol", rol, "status",
					"Autenticado Exitosamente"));

		} catch (Exception e) {
			e.printStackTrace();

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al verPerfil");
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

		// Verifica que el encabezado Authorization exista
		// y que tenga el formato: "Bearer <token>"
		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			// Extrae únicamente el JWT eliminando el prefijo "Bearer "
			String token = authHeader.substring(7);

			// validar si la session ya fue cerrada para no volver a invalidar algo cerrrado
			if (blackListService.estaInvalido(token)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La sesión ya fue cerrada");
			}

			// Agrega el token a la blacklist para impedir que vuelva a utilizarse
			blackListService.invalidarToken(token);

			// Retorna una respuesta indicando que el cierre de sesión fue exitoso
			return ResponseEntity.status(HttpStatus.OK)
					.body(Map.of("mensaje", "Sesión cerrada exitosamente. Token invalidado."));
		} else {

			// Retorna un error si no se recibió el token o el formato es incorrecto
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionado");
		}
	}
	
	//metodo usando pringSecurity
	
	@GetMapping("/perfilconsecurity")
	// Endpoint protegido que muestra información del usuario autenticado
	public ResponseEntity<?> verPerfil() {

	    // Obtiene el objeto Authentication almacenado por Spring Security
	    // dentro del SecurityContext después de validar el JWT
	    Authentication auth = SecurityContextHolder
	            .getContext()
	            .getAuthentication();

	    // Obtiene el nombre del usuario autenticado
	    // Normalmente corresponde al username guardado en el JWT
	    String usuario = auth.getName();

	    // Obtiene el primer rol o autoridad asignada al usuario
	    // Ejemplo: ROLE_ADMIN o ROLE_USER
	    String rol = auth.getAuthorities()
	            .iterator()
	            .next()
	            .getAuthority();

	    // Devuelve una respuesta HTTP 200 con información
	    // extraída del contexto de seguridad
	    return ResponseEntity.status(HttpStatus.OK)
	            .body(Map.of(
	                    "Mensaje", "Bienvenido al sistema protegido por Spring Security",
	                    "Usuario", usuario,
	                    "Rol_detectado", rol,
	                    "Status", "Autenticado exitosamente"
	            ));
	}

}
