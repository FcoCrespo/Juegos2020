package edu.uclm.esi.games2020.model;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.games2020.dao.UserDAO;

@Tabla(tabla = "Usuarios")
public class User {
	private String userName;
	@NoJSON
	private String email;
	@NoJSON 
	private WebSocketSession session;
	@NoJSON
	private IState state;
	@NoJSON
	private HttpSession httpSession;
	
	@Autoejecutable
	public void imprimir() {
		System.out.println("Hola");
	}
	
	public void setState(IState state) {
		this.state = state;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public static User identify(String userName, String pwd) throws Exception {
		return UserDAO.identify(userName, pwd);
	}

	public JSONObject toJSON() {
		return new JSONObject().put("userName", this.userName);
	}

	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	public void send(JSONObject json) throws IOException {
		this.session.sendMessage(new TextMessage(json.toString()));		
	}

	public IState getState() {
		return this.state;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	
	public HttpSession getHttpSession() {
		return httpSession;
	}
}
