package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import edu.uclm.esi.games2020.dao.UserDAO;

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

}
