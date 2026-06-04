package com.krakedev.proyectos.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Usuario;
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
	
}
