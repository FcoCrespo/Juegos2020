package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public abstract class Match {
	
	private final Logger log = Logger.getLogger(Match.class.getName());
    protected List<User> players;
    protected String id;
    protected boolean started;
    private int readyPlayers;
    protected WebSocketSession turn;
    @NoJSON
    private Game game;


    protected int getPosOfSession(WebSocketSession session) {
        int pos = -1;
        for (User u : this.players) {
            if (u.getSession() == session) {
                pos = players.indexOf(u);
            }
        }
        return pos;
    }
    
    protected String getNamePlayerSession(WebSocketSession session) {
        String name = null;
        for (User u : this.players) {
            if (u.getSession() == session) {
                name = players.get(players.indexOf(u)).getUserName();
            }
        }
        return name;
    }
    
    protected int getIdOtherPlayer(WebSocketSession session) {
        int pos = -1;
        for (User u : this.players) {
            if (!u.getSession().equals(session)) {
                pos = players.indexOf(u);
            }
        }
        return pos;
    }
    
    protected User getUserSession(WebSocketSession session) {
    	User user=null;
        for (User u : this.players) {
            if (u.getSession().equals(session)) {
            	user = u;
            }
        }
        if(user!=null) {
        	return user;
        }
        return null;
    }
    

    public Match() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
    }

    public void addPlayer(User user) {
        this.players.add(user);
        setState(user);
    }

    public String rotateTurn(WebSocketSession lastTurn) {

        int pos = getPosOfSession(lastTurn);

        if (pos == (players.size() - 1)) {
            pos = 0;
        } else {
            pos++;
        }

        this.turn = players.get(pos).getSession();
        return players.get(pos).getUserName();

    }

    protected abstract void setState(User user);

    public List<User> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }

    public abstract void start() throws Exception;
	


    public JSONObject toJSON() {
        JSONObject jso = new JSONObject();
        jso.put("idMatch", this.id);
        jso.put("started", this.started);
        JSONArray jsa = new JSONArray();
        for (User user : this.players)
            jsa.put(user.toJSON());
        jso.put("players", jsa);
        return jso;
    }

    public void notifyTurn(String name) throws IOException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchChangeTurn");
        for (User player : this.players) {
            jso.put("turn", name);
            player.send(jso);
        }
    }

    public void notifyStart() throws IOException{
        JSONObject jso = this.toJSON();
        jso.put("type", "matchStarted");
        for (User player : this.players) {
            jso.put("startData", startData(player));
            player.send(jso);
        }
    }

    public void notifyFinish(String result) throws IOException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchFinished");
        jso.put("result", result);
        for (User player : this.players) {
            player.send(jso);
        }
    }

    protected abstract JSONObject startData(User player);

    public void playerReady() throws Exception{
        ++readyPlayers;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean ready() {
        return this.readyPlayers == game.requiredPlayers;
    }

    public String inicializaTurn() throws IOException{
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
        	log.info("\nError durante el turno");
        }
        try {
        	User u = players.get(sr.nextInt(players.size()));
            String name = rotateTurn(u.getSession());
            this.notifyTurn(name);
            return name;
        }
        catch(Exception e) {
        	log.info("\nError durante el turno");
        	return null;
        }
        
    }

    public void notifyInvalidPlay(WebSocketSession session, String mensaje) throws IOException {
        JSONObject jso = this.toJSON();
        jso.put("type", "matchIlegalPlay");
        jso.put("result", mensaje);
        int pos = getPosOfSession(session);
        if(pos>=0)
            players.get(pos).send(jso);
    }

    public abstract void play(JSONObject jso, WebSocketSession session) throws IOException;
	
}
