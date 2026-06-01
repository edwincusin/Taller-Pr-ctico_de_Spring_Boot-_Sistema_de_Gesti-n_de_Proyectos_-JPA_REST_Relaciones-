package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.repositories.EmpleadoRepository;

@ Service
public class EmpleadoService {
	
	private final EmpleadoRepository repositoryEmpleado;


	public EmpleadoService(EmpleadoRepository repositoryEmpleado) {
		super();
		this.repositoryEmpleado = repositoryEmpleado;
	}

	//INSERTAR NUEVO
	public Empleado insertar(Empleado empleado) {
		return repositoryEmpleado.save(empleado);
	}
	
	// BUSCAR POR ID
	public Empleado buscarPorID(Long id) {
		Empleado empleadoEnocntrado = repositoryEmpleado.findById(id)
				.orElseThrow(()-> new RuntimeException("No existe empleado con la ID: " + id));
		return empleadoEnocntrado;
	}

	// LISTAR TODOS
	public List<Empleado> listar() {
		return repositoryEmpleado.findAll();
	}
	
	//ACTUALIZAR 
	public Empleado actualizar(Long id, Empleado empleadoNuevo) {
		Empleado empleado=buscarPorID(id);
		
		empleado.setNombre(empleadoNuevo.getNombre());
		empleado.setCargo(empleadoNuevo.getCargo());
		
		return repositoryEmpleado.save(empleado);
	}
	
	//ELIMINAR
	public void eliminar(Long id) {
	    Empleado empleado= buscarPorID(id);
	    repositoryEmpleado.delete(empleado);
	}

}
