package edu.uclm.esi.games2020.model;

import java.io.IOException;


import org.json.JSONObject;
import org.springframework.data.annotation.Id;


@Tabla(tabla = "Tokens")
public class Token {

    private Long id;
    private String email;
    private String token;
    private Long fecha;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getFecha() {
		return fecha;
	}

	public void setFecha(Long fecha) {
		this.fecha = fecha;
	}
    
	
    
}
