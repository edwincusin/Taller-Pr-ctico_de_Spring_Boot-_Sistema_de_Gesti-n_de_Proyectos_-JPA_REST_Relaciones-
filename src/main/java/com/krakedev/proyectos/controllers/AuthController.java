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
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioService servicio;
	private final TokenBlacklistService blackListService;

	public AuthController(UsuarioService servicio, TokenBlacklistService blackListService) {
		this.servicio = servicio;
		this.blackListService = blackListService;
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
	public ResponseEntity<?> login(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioLogueado = servicio.login(usuario.getUsername(), usuario.getPassword());

			if (usuarioLogueado != null) {
				String token = JwtUtil.generarToken(usuarioLogueado.getUsername(), usuarioLogueado.getRol());
				return ResponseEntity.ok(Map.of("TOKEN", token));
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

			return ResponseEntity.ok(Map.of("Mensaje", "Bienvenido", "Usuario", usuario, "Rol", rol, "Estatus",
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

}
