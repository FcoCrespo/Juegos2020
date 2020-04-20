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
        FichaDomino ficha1 = this.deck.getFicha();
        FichaDomino ficha2 = this.deck.getFicha();
        FichaDomino ficha3 = this.deck.getFicha();
        FichaDomino ficha4 = this.deck.getFicha();
        FichaDomino ficha5 = this.deck.getFicha();
        FichaDomino ficha6 = this.deck.getFicha();
        FichaDomino ficha7 = this.deck.getFicha();
        ficha1.setState(player.getState());
        ficha2.setState(player.getState());
        ficha3.setState(player.getState());
        ficha4.setState(player.getState());
        ficha5.setState(player.getState());
        ficha6.setState(player.getState());
        ficha7.setState(player.getState());
        JSONArray jsaFichasJugador = new JSONArray();
        jsaFichasJugador.put(ficha1.toJSON());
        jsaFichasJugador.put(ficha2.toJSON());
        jsaFichasJugador.put(ficha3.toJSON());
        jsaFichasJugador.put(ficha4.toJSON());
        jsaFichasJugador.put(ficha5.toJSON());
        jsaFichasJugador.put(ficha6.toJSON());
        jsaFichasJugador.put(ficha7.toJSON());

        JSONObject jso = new JSONObject();
        jso.put("data", jsaFichasJugador);
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
