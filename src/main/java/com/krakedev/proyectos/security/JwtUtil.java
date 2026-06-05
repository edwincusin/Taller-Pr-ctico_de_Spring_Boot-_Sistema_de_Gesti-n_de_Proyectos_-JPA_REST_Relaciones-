package com.krakedev.proyectos.security;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {
	private static final String CLAVE_SECRETA = "EstaEsUnaClaveSuperSecretaLarga1234567890";
	private static final String EMISOR = "KeakeDevBackend";
	private static final long TIEMPO__EXPIRACION = 1000L * 60 * 30; //30 minutos (modificar el ultima multiplicacion o 30)

	public static String generarToken(String userName, String rol) {

		Algorithm algorithm = Algorithm.HMAC256(CLAVE_SECRETA);
		long tiempoActual = System.currentTimeMillis();
		Date fechaExpiracion = new Date(tiempoActual + TIEMPO__EXPIRACION);

		String tokenGenerado = JWT.create().withIssuer(EMISOR).withSubject(userName)
				.withIssuedAt(new Date(tiempoActual)).withExpiresAt(fechaExpiracion).withClaim("rol", rol)
				.sign(algorithm);

		return tokenGenerado;
	}

	public static DecodedJWT validarToken(String token) {

		try {
			Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);
			JWTVerifier verificador = JWT.require(algoritmo).withIssuer(EMISOR).build();

			DecodedJWT tokenDecodificado = verificador.verify(token);
			return tokenDecodificado;

		} catch (Exception e) {
			// Se devuelve null para indicar que la validación falló.
			return null;
		}
	}

}
