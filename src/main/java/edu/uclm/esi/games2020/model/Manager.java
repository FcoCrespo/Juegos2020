package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.mail.*;

import org.json.JSONArray;
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.games2020.dao.UserDAO;
import edu.uclm.esi.games2020.dao.TokenDAO;


public class Manager {
	
	
	private ConcurrentHashMap<String, User> connectedUsersByUserName;
	private ConcurrentHashMap<String, User> connectedUsersByHttpSession;
	private ConcurrentHashMap<String, Game> games;
	private ConcurrentHashMap<String, Match> pendingMatches;
	private ConcurrentHashMap<String, Match> inPlayMatches;
	
	private Manager() {
		this.connectedUsersByUserName = new ConcurrentHashMap<>();
		this.connectedUsersByHttpSession = new ConcurrentHashMap<>();
		this.games = new ConcurrentHashMap<>();
		this.pendingMatches = new ConcurrentHashMap<>();
		this.inPlayMatches = new ConcurrentHashMap<>();
		
		Game ajedrez = new Ajedrez();
		Game ter = new TresEnRaya();
		Game escoba = new Escoba();
		
		this.games.put(ajedrez.getName(), ajedrez);
		this.games.put(ter.getName(), ter);
		this.games.put(escoba.getName(), escoba);
	}
	
	public Match joinToMatch(User user, String gameName) {
		Game game = this.games.get(gameName);
		Match match = game.joinToMatch(user);
		if (!pendingMatches.containsKey(match.getId()))
			pendingMatches.put(match.getId(), match);
		return match;
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	public static Manager get() {
		return ManagerHolder.singleton;
	}

	public User login(HttpSession httpSession, String userName, String pwd) throws Exception {
		User user = UserDAO.identify(userName, pwd);
		user.setHttpSession(httpSession);
		this.connectedUsersByUserName.put(userName, user);
		this.connectedUsersByHttpSession.put(httpSession.getId(), user);
		return user;
	}
	
	public void register(String email, String userName, String pwd, String cuenta) throws Exception {
		UserDAO.insert(email, userName, pwd, cuenta);
	}
	
	public void logout(User user) {
		this.connectedUsersByUserName.remove(user.getUserName());
		this.connectedUsersByHttpSession.remove(user.getHttpSession().getId());
	}
	
	public JSONArray getGames() {
		Collection<Game> gamesList = this.games.values();
		JSONArray result = new JSONArray();
		for (Game game : gamesList)
			result.put(game.getName());
		return result;
	}
	
	
	
	public void playerReady(String idMatch, WebSocketSession session) throws Exception {
		Match match = this.pendingMatches.get(idMatch);
		match.playerReady(session);
		if (match.ready()) {
			this.pendingMatches.remove(idMatch);
			this.inPlayMatches.put(idMatch, match);
			match.notifyStart();
			match.notifyTurn(match.inicializaTurn());
		}
	}

	public User findUserByHttpSessionId(String httpSessionId) {
		return this.connectedUsersByHttpSession.get(httpSessionId);
	}

	

	public Match findMatch(String idMatch) {
		
		return this.inPlayMatches.get(idMatch);
	}

	public JSONArray getUser(HttpSession session) {
		Collection<User> userList = this.connectedUsersByHttpSession.values();
		JSONArray result = new JSONArray();
		for (User user : userList)
			if(user.getHttpSession().getId().equals(session.getId())) {
				System.out.println("El nombre del usuario es: "+user.getUserName());
				result.put(user.getUserName());
				System.out.println("La cuenta del usuario es: "+user.getCuenta());
				result.put(user.getCuenta());
			}
		System.out.println(result.toString());
		return result;
	}

	public int changePass(String token, String pass) {
		
		
		try {
			Token reqToken = TokenDAO.getToken(token);
			
			
			String uuidToken = reqToken.getToken();
			if(!uuidToken.equals(token)) {
				return -1;
			}
			Long fecha = reqToken.getFecha();
			
			Long tenmin = fecha + 60000;
			Long timenow = System.currentTimeMillis();
			if(timenow>timenow){
				TokenDAO.borrarToken(uuidToken);
				return -2;
			}
			String email = reqToken.getEmail();
			UserDAO.cambiarPass(email, pass);
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -1;
		}
		
	}

	public boolean emailPassReq(String emailReq) throws Exception {
		
	   		Properties prop = new Properties();
			prop.put("mail.smtp.host", "smtp.gmail.com");
	        prop.put("mail.smtp.port", "587");
	        prop.put("mail.smtp.auth", "true");
	        prop.put("mail.smtp.starttls.enable", "true"); 
	        
	        Session session = Session.getInstance(prop,
	                new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication("softwareuclm2020@gmail.com", "Manager2020");
	                    }
	                });
	        try {
	        	String email = UserDAO.getEmail(emailReq);
				if(email == null) {
					return false;
				}
				
				
				///Se tendra que crear el token en la bbdd
				
				String uuid = UUID.randomUUID().toString();
				
				TokenDAO.insert(email, uuid);
				
	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress("softwareuclm2020@gmail.com"));
	            message.setRecipients(
	                    Message.RecipientType.TO,
	                    InternetAddress.parse(emailReq)
	            );
	            message.setSubject("Password request");
	            message.setText("http://localhost:8600/newpassword.html?token="+uuid);

	            Transport.send(message);

	            System.out.println("Email enviado al correo "+emailReq);

	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
		
			
			
			
			return true;
	}

}
