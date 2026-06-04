package com.krakedev.proyectos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krakedev.proyectos.entidades.Usuario;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}
