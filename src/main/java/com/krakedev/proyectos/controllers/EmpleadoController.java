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

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.services.EmpleadoService;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

	private final EmpleadoService servicio;

	public EmpleadoController(EmpleadoService servicio) {
		super();
		this.servicio = servicio;
	}

	// INSERTAR NUEVO
	@PostMapping
	public ResponseEntity<?> insertar(@RequestBody Empleado empleado) {

		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(servicio.insertar(empleado));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar empleado");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar empleado por id");
		}
	}

	// LISTAR TODOS
	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.listar());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar empleado");
		}
	}

	// ACTUALIZAR
	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id,@RequestBody Empleado empleadoNuevo) {
		
		try {
			return ResponseEntity.status(HttpStatus.OK).body(servicio.actualizar(id, empleadoNuevo));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar empleado por id");
		}
	}

	// ELIMINAR
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			servicio.eliminar(id);
			return ResponseEntity.status(HttpStatus.OK).body("Se elimino correctamente");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar empleado por id");
		}
	}

}
