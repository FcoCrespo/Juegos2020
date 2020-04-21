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
    	FichaDomino ficha;
    	JSONArray jsaFichasJugador = new JSONArray();
    	
    	for(int i=0; i<fichasJugadores.size();i++) {
    		System.out.println("Ficha que tienen los jugadores : "+fichasJugadores.get(i).getNumber1()+" | "+fichasJugadores.get(i).getNumber2());
    	}
    	
    	for (int i = 0; i<7; i++) {
    		ficha = this.deck.getFicha();
    		ficha.setState(player.getState());
    		fichasJugadores.add(ficha);
    		jsaFichasJugador.put(ficha.toJSON());
    		System.out.println("Ficha : "+ficha.getNumber1()+" | "+ficha.getNumber2());
    	}
    	
        JSONObject jso = new JSONObject();
        jso.put("data", jsaFichasJugador);
        return jso;
    }

    @Override
	public void play(JSONObject jso, WebSocketSession session) throws IOException {
	
			int number_1 = jso.getInt("number_1");
			int number_2 = jso.getInt("number_2");
			
			FichaDomino fichaPuesta = new FichaDomino(number_1,number_2);
			
			boolean posicionTablero = jso.getBoolean("posicion");
			
			/*if(this.verifyPlay(session, fichaPuesta, posicionTablero)) {

				this.notifyPlay(session, x, y);

				int w = this.winner(x, y, this.doPlay(session, x, y));

				if(w == -1) {
					this.notifyTurn(this.rotateTurn(session));
				}else if(w == -2){
					String result = "La partida ha finalizado en empate";
					this.notifyFinish(result);
				}else {
					String result = this.players.get(w).getUserName() + " ha ganado la partida";
					this.notifyFinish(result);
				}
			}else {
				this.notifyInvalidPlay(session);
			}*/
		
		
	}

    public boolean verifyPlay(WebSocketSession session, FichaDomino fichaPuesta, boolean posicionTablero) {

        if (session != this.turn) {
            return false;
        }
        
        if(!fichaPuesta.getState().getUser().getSession().equals(session.getId())) {
        	return false;
        }
        
        boolean existeFicha = false;
        for(int i=0; i < this.fichasJugadores.size(); i++) {
        	if(this.fichasJugadores.get(i).getNumber1()==fichaPuesta.getNumber1() && this.fichasJugadores.get(i).getNumber2()==fichaPuesta.getNumber2()) {
        		existeFicha = true;
        	}
        	if(this.fichasJugadores.get(i).getNumber1()==fichaPuesta.getNumber2() && this.fichasJugadores.get(i).getNumber2()==fichaPuesta.getNumber1()) {
        		existeFicha = true;
        	}
        }
        if(existeFicha == false) {
        	return false;
        }
        
        boolean colocacionCorrecta = false;
        if(!tablero.isEmpty()) {
        	if(tablero.size()>2) {
        		if(posicionTablero == true) {
        			FichaDomino fichaIzq = tablero.peek();
        			if(fichaIzq.getNumber1()==fichaPuesta.getNumber1() || fichaIzq.getNumber1()==fichaPuesta.getNumber2()) {
        				colocacionCorrecta = true;
        			}
        			if(fichaIzq.getNumber2()==fichaPuesta.getNumber1() || fichaIzq.getNumber2()==fichaPuesta.getNumber2()) {
        				colocacionCorrecta = true;
        			}
        		}
        	}
        }
        
        if(colocacionCorrecta==false) {
        	return false;
        }
        
        return true;
    }
    
    /*@Override
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
		
	}*/


	private FichaDomino find(FichaDomino doble, List<FichaDomino> fichas) {
		
		for(FichaDomino ficha : fichas) {
			if(ficha.equals(doble)) {
				return ficha;
			}
		}
		return null;
	}
    
}
