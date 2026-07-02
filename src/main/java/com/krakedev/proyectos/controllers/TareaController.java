package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaService servicio;


	public TareaController(TareaService servicio) {
		this.servicio = servicio;
	}

	// CREAR TAREA NUEVO
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> crearTarea(@RequestBody Tarea tarea) {

		try {
	
			return ResponseEntity.status(HttpStatus.CREATED).body(servicio.insertar(tarea));

		} catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Prioridad no válida"));

	    } catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar tarea");
		}
	}

	// BUSCAR POR ID
	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorID(@PathVariable Long id) {

		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.buscarPorID(id));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar tarea por id");
		}
	}

	// LISTAR TODOS
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<?> listar() {

		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.listar());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar tareas");
		}
	}

	// ACTUALIZAR
	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Tarea tareaNueva) {

		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.actualizar(id, tareaNueva));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar tarea");
		}
	}

	// ELIMINAR
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {

		try {
			servicio.eliminar(id);

			return ResponseEntity.status(HttpStatus.OK).body("Se eliminó correctamente");

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar tarea");
		}
	}
}