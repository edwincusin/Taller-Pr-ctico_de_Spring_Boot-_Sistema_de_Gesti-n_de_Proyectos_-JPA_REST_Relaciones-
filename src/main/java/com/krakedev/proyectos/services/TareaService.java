package com.krakedev.proyectos.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.EmpleadoRepository;
import com.krakedev.proyectos.repositories.ProyectoRepository;
import com.krakedev.proyectos.repositories.TareaRepository;

@Service
public class TareaService {

	private final TareaRepository tareaRepository;
	private final EmpleadoRepository empleadoRepository;
	private final ProyectoRepository proyectoRepository;
	
	public TareaService(TareaRepository tareaRepository, EmpleadoRepository empleadoRepository,
			ProyectoRepository proyectoRepository) {
		this.tareaRepository = tareaRepository;
		this.empleadoRepository = empleadoRepository;
		this.proyectoRepository = proyectoRepository;
	}
	
	//INSERTAR
	public Tarea insertar(Tarea tarea) {
		Proyecto proyecto=proyectoRepository.findById(tarea.getProyecto().getId()).orElseThrow(()-> new RuntimeException("Proyecto no existe en la BDD"));
		
		List<Empleado> empleadosBDD=new ArrayList<Empleado>();
		
		for(Empleado empleado : tarea.getEmpleados()) {
			Empleado empleadoReal=empleadoRepository.findById(empleado.getId()).orElseThrow(()-> new RuntimeException("Empleado no existe en la BDD"));
			
			empleadosBDD.add(empleadoReal);
		}
		
		tarea.setEmpleados(empleadosBDD);
		tarea.setProyecto(proyecto);
		
		return tareaRepository.save(tarea);
		
	}
	
	  // BUSCAR POR ID
    public Tarea buscarPorID(Long id) {
        return tareaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("No existe tarea con ID: " + id));
    }
	
    public Tarea actualizar(Long id, Tarea tareaNueva) {

        Tarea tarea = buscarPorID(id);

        Proyecto proyecto = proyectoRepository.findById(
                tareaNueva.getProyecto().getId())
                .orElseThrow(() ->
                        new RuntimeException("Proyecto no existe en la BDD"));

        List<Empleado> empleadosBDD = new ArrayList<>();

        for (Empleado empleado : tareaNueva.getEmpleados()) {

            Empleado empleadoReal = empleadoRepository.findById(
                    empleado.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Empleado no existe en la BDD"));

            empleadosBDD.add(empleadoReal);
        }

        tarea.setDescripcion(tareaNueva.getDescripcion());
        tarea.setFechaLimite(tareaNueva.getFechaLimite());
        tarea.setCostoEstimado(tareaNueva.getCostoEstimado());

        tarea.setProyecto(proyecto);
        tarea.setEmpleados(empleadosBDD);

        return tareaRepository.save(tarea);
    }
    
    // LISTAR TODOS
    public List<Tarea> listar() {
        return tareaRepository.findAll();
    }

    
 // ELIMINAR
    public void eliminar(Long id) {

        Tarea tarea = buscarPorID(id);

        tareaRepository.delete(tarea);
    }

	
}
