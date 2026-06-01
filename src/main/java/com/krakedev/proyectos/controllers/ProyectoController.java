package com.krakedev.proyectos.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

	private final ProyectoService servicio;

	public ProyectoController(ProyectoService servicio) {
		this.servicio = servicio;
	}
	
	//INSERTAR NUEVO
	@PostMapping
	public ResponseEntity<?> insertar(@RequestBody Proyecto proyecto) {

		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(servicio.insertar(proyecto));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar proyecto");
		}
	}
	
	// BUSCAR POR ID
	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorID(@PathVariable Long id) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.buscarPorID(id)); 
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar proyecto");
		}
	}

	// LISTAR TODOS
	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.listar());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar proyectos");
		}
	}
	
	//ACTUALIZAR
	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id,@RequestBody Proyecto proyectoNuevo) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.actualizar(id, proyectoNuevo)); 
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar proyecto");
		}
		
	}
	
	//ELIMINAR
	@DeleteMapping("/{id}")
	public ResponseEntity<?>  eliminar(@PathVariable Long id) {
		try {
						
			servicio.eliminar(id);
			return ResponseEntity.status(HttpStatus.OK).body("Eliminado correctamente");
			
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar proyectos");
		}
	}

	
	
	
}
