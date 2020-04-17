package edu.uclm.esi.games2020.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DominoMatch extends Match {

    private Deque<FichaDomino> tablero;
    private DeckDomino deck;
    private List<FichaDomino> fichasJugadores;

    public DominoMatch() {
        super();
        this.deck = new DeckDomino();
        this.deck.suffle();
        this.tablero = new ArrayDeque<>();
        this.fichasJugadores = new ArrayList<>();

    }


    @Override
    protected void setState(User user) {
        IState state = new DominoState();
        user.setState(state);
        state.setUser(user);
    }

    @Override
    public void start() throws Exception {
        this.started = true;
        super.notifyStart();
        super.inicializaTurn();
    }

    @Override
    protected JSONObject startData(User player) {
        JSONObject jso = new JSONObject();
        JSONArray jsaTablero = new JSONArray();
        jsaTablero.put(this.tablero);
        jso.put("table", jsaTablero);
        return jso;
    }

    @Override
    public void play(JSONObject jso, WebSocketSession session) throws IOException {

    }

    public boolean verifyPlay(WebSocketSession session, int lugar_x, int lugar_y) {

        if (session != this.turn) {
            return false;
        }
        return true;
    }
    
    @Override
    public String inicializaTurn() throws IOException {
    	
    	User u = getStartingPlayer(); 
    	
		if(u != null) {
			this.turn = u.getSession();
			this.notifyTurn(u.getUserName());
			return u.getUserName();
		}
		else {
			return super.inicializaTurn();		// Si ningun jugador tiene un doble, turno aleatorio 
		}
		
    }


	private User getStartingPlayer() {
		
		FichaDomino f = getHigherDouble(this.fichasJugadores);
		return f.getState().getUser();

	}


	private FichaDomino getHigherDouble(List<FichaDomino> fichas) {
		
		int higher = 6;
		boolean found = false;
		FichaDomino doble = null;
		
		while(!found || higher==-1) {
			
			doble = new FichaDomino(higher,higher);
			doble = find(doble, fichas);
			if(doble != null)
				found = true;
			higher--;
		}
		
		if(!found)
			doble = null;
		
		return doble;
		
	}


	private FichaDomino find(FichaDomino doble, List<FichaDomino> fichas) {
		
		for(FichaDomino ficha : fichas) {
			if(ficha.equals(doble)) {
				return ficha;
			}
		}
		return null;
	}
    
}
