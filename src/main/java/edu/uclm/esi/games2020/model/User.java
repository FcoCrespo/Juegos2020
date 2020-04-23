package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.games2020.dao.UserDAO;


@Tabla(tabla = "Usuarios")
public class User implements Serializable{
    
	private String userName;
    @NoJSON
    private String email;
    @NoJSON
    private String cuenta;
    @NoJSON
    private transient WebSocketSession session;
    @NoJSON
    private transient IState state;
    @NoJSON
    private transient HttpSession httpSession;
    private transient Logger log = Logger.getLogger(User.class.getName());

    @Autoejecutable
    public void imprimir() {
    	log.info("\nHola");
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
    
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public static User identify(String userName, String pwd){
    	try {
    		return UserDAO.identify(userName, pwd);
    	}
    	catch(Exception e) {
    		return null;
    	}
        
    }

    public JSONObject toJSON() {
    	JSONObject jsonUser = new JSONObject();
    	jsonUser.put("userName", this.userName);
    	jsonUser.put("cuenta", this.cuenta);
    	return jsonUser;
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

    public WebSocketSession getSession() {
        return this.session;
    }

}
