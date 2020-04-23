package edu.uclm.esi.games2020.model;


import java.util.Collection;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.mail.*;

import org.json.JSONArray;


import edu.uclm.esi.games2020.dao.UserDAO;
import edu.uclm.esi.games2020.dao.TokenDAO;


public class Manager {
	
	
	private ConcurrentHashMap<String, User> connectedUsersByUserName;
	private ConcurrentHashMap<String, User> connectedUsersByHttpSession;
	private ConcurrentHashMap<String, Game> games;
	private ConcurrentHashMap<String, Match> pendingMatches;
	private ConcurrentHashMap<String, Match> inPlayMatches;
	private final Logger log = Logger.getLogger(Manager.class.getName());
	
	private Manager() {
		this.connectedUsersByUserName = new ConcurrentHashMap<>();
		this.connectedUsersByHttpSession = new ConcurrentHashMap<>();
		this.games = new ConcurrentHashMap<>();
		this.pendingMatches = new ConcurrentHashMap<>();
		this.inPlayMatches = new ConcurrentHashMap<>();
		
		Game ajedrez = new Ajedrez();
		Game ter = new TresEnRaya();
		Game escoba = new Escoba();
		Game domino = new Domino();
		
		this.games.put(ajedrez.getName(), ajedrez);
		this.games.put(ter.getName(), ter);
		this.games.put(escoba.getName(), escoba);
		this.games.put(domino.getName(), domino);
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

	public User login(HttpSession httpSession, String userName, String pwd){
		User user;
		try {
			user = UserDAO.identify(userName, pwd);
			user.setHttpSession(httpSession);
			this.connectedUsersByUserName.put(userName, user);
			this.connectedUsersByHttpSession.put(httpSession.getId(), user);
			return user;
		} catch (Exception e) {
			log.info("\n Error en Login");
			return null;
		}
		
	}
	
	public void register(String email, String userName, String pwd, String cuenta) {
		try {
			UserDAO.insert(email, userName, pwd, cuenta);
		} catch (Exception e) {
			log.info("\n Error en Register");
		}
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
	
	
	
	public void playerReady(String idMatch){
		Match match = this.pendingMatches.get(idMatch);
		try {
			match.playerReady();
			if (match.ready()) {
				this.pendingMatches.remove(idMatch);
				this.inPlayMatches.put(idMatch, match);
				match.notifyStart();
				match.notifyTurn(match.inicializaTurn());
			}
		} catch (Exception e) {
			log.info("\n Error en jugador preparado");
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
				log.info("El nombre del usuario es: "+user.getUserName());
				result.put(user.getUserName());
				result.put(user.getCuenta());
			}
		
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
			
			Long tenmin = fecha + 120000;
			Long timenow = System.currentTimeMillis();
			if(timenow>tenmin){
				TokenDAO.borrarToken(uuidToken);
				return -2;
			}
			String email = reqToken.getEmail();
			UserDAO.cambiarPass(email, pass);
			return 1;
		} catch (Exception e) {
			log.info("Se ha producido un error al enviar el correo.");
			return -1;
		}
		
	}

	public boolean emailPassReq(String emailReq) {
		
	   		Properties prop = new Properties();
			prop.put("mail.smtp.host", "smtp.gmail.com");
	        prop.put("mail.smtp.port", "587");
	        prop.put("mail.smtp.auth", "true");
	        prop.put("mail.smtp.starttls.enable", "true"); 
	        
	        Session session = Session.getInstance(prop,
	                new javax.mail.Authenticator() {
	        			@Override
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

	            String mensaje ="\nEmail enviado al correo "+email;
	            log.info(mensaje);

	        }catch (Exception e) {
	        	log.info("Se ha producido un error al enviar el correo.");
			}
		
			
			
			
			return true;
	}

}
