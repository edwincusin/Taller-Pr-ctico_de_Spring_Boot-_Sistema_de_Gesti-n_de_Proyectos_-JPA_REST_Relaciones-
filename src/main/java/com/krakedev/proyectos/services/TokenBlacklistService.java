package com.krakedev.proyectos.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
	
	private final Set<String> blackListToken=ConcurrentHashMap.newKeySet();
		
	public void invalidarToken(String token) {
		blackListToken.add(token);
	}
	
	public boolean estaInvalido(String token) {
		return blackListToken.contains(token);
	}
}
