package edu.uclm.esi.games2020.model;


@Tabla(tabla = "Tokens")
public class Token {

    private Long id;
    private String email;
    private String uuid;
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
		return uuid;
	}

	public void setToken(String token) {
		this.uuid = token;
	}

	public Long getFecha() {
		return fecha;
	}

	public void setFecha(Long fecha) {
		this.fecha = fecha;
	}
    
	
    
}
