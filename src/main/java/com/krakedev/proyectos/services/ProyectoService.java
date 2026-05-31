package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.repositories.ProyectoRepository;

@Service
public class ProyectoService {

	private final ProyectoRepository repositoryProyecto;

	public ProyectoService(ProyectoRepository repositoryProyecto) {
		this.repositoryProyecto = repositoryProyecto;
	}

	//INSERTAR NUEVO
	public Proyecto insertar(Proyecto proyecto) {
		return repositoryProyecto.save(proyecto);
	}
	
	// BUSCAR POR ID
	public Proyecto buscarPorID(Long id) {
		Proyecto proyectoEncontrado = repositoryProyecto.findById(id)
				.orElseThrow(()-> new RuntimeException("No existe proyecto con la ID: " + id));
		return proyectoEncontrado;
	}

	// LISTAR TODOS
	public List<Proyecto> listar() {
		return repositoryProyecto.findAll();
	}
	
	//ACTUALIZAR 
	public Proyecto actualizar(Long id, Proyecto proyectoNuevo) {
		Proyecto proyecto=buscarPorID(id);
		
		proyecto.setNombre(proyectoNuevo.getNombre());
		proyecto.setDescripcion(proyectoNuevo.getDescripcion());
		proyecto.setFechaInicio(proyectoNuevo.getFechaInicio());
		
		return repositoryProyecto.save(proyecto);
		
	}

}
