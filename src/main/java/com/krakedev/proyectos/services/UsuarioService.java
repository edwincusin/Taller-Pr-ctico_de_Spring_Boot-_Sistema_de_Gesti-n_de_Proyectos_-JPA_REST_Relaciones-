package com.krakedev.proyectos.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;

@Service
public class UsuarioService {
	
	private final UsuarioRepository repositorio;

	public UsuarioService(UsuarioRepository repositorio) {
		super();
		this.repositorio = repositorio;
	}

	public Usuario insertar(Usuario usuario) {
	    if(repositorio.findByUsername(usuario.getUsername()).isPresent()) {
	        throw new RuntimeException("El usuario ya existe");
	    }
	    
		String passEncriptada=BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
		usuario.setPassword(passEncriptada);
		return repositorio.save(usuario);
	}
	
	public Usuario login(String username, String password) {
		
		Optional<Usuario> usuarioEncontrado=repositorio.findByUsername(username);
		
		if(usuarioEncontrado.isPresent()) {
			Usuario usuario=usuarioEncontrado.get();
			
			boolean passEsCorrecto=BCrypt.checkpw(password, usuario.getPassword());
			
			if(passEsCorrecto) {
				return usuario;
			}			
		}
		return null;
	}
}
