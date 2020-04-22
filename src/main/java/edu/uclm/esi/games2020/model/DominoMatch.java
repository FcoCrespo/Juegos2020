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
    private FichaDomino fichaColocada;
    

    public DominoMatch() {
        super();
        this.deck = new DeckDomino();
        this.deck.suffle();
        this.tablero = new ArrayDeque<>();
        this.fichasJugadores = new ArrayList<>();
        this.fichaColocada = new FichaDomino(7,7);
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

    public FichaDomino obtenerFichaJugador(int number_1, int number_2) {
    	
    	for(int i=0; i<this.fichasJugadores.size(); i++) {
    		if(fichasJugadores.get(i).getNumber1()==number_1 && fichasJugadores.get(i).getNumber2()==number_2 ) {
    			return fichasJugadores.get(i);
    		}
    	}
    	
    	return null;
    }
    
    @Override
	public void play(JSONObject jso, WebSocketSession session) throws IOException {
    		
	    	 
			int number_1 = jso.getInt("number_1");
			int number_2 = jso.getInt("number_2");
			boolean posicionTablero = jso.getBoolean("posicion");
			
			//this.fichaColocada = new FichaDomino(number_1,number_2);
			
			if (session == this.turn) {
				this.fichaColocada = obtenerFichaJugador(number_1,number_2);
				
				System.out.println("El jugador "+this.getNamePlayerSession(session));
				System.out.println("Ficha colocada: "+this.fichaColocada.getNumber1() + " | "+this.fichaColocada.getNumber2());
				
				System.out.println("La ficha pertenece al jugador con la sesion: "+this.fichaColocada.getState().getUser().getSession().getId());
				System.out.println("La sesion del jugador es: "+session.getId());
				
				if(this.verifyPlay(session, number_1, number_2, posicionTablero)) {
					quitarFichaMano();
					this.notifyPlay(session, posicionTablero);

					int w = this.winner(session, doPlay(session));

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
				}
	        }
			else {
				this.notifyInvalidPlay(session);
			}
		
		
	}
    
    public int winner(WebSocketSession session, int id) {
        if (manoSinFichas(session)==false) {
            return id;
        }
        else if(this.deck.getFichas().isEmpty()) {
        	boolean continuar = posibleColocacion();
    		if(continuar==false) {
    			int mismasFichas = contarFichasJugadores(session, id);
    			return mismasFichas;
    		}
    		else {
    			return -1;
    		}
        }
        else {
        	return -1;
        }
    }
    
    public int contarFichasJugadores(WebSocketSession session, int id) {
    	int fichasJugadorActual = 0;
    	int fichasOtroJugador = 0;
    	int idJugador = 0;
    	for(int i=0; i<this.fichasJugadores.size(); i++) {
    		if(this.fichasJugadores.get(i).getState().getUser().getSession().getId().equals(session.getId())) {
    			fichasJugadorActual ++;
    		}
    		else {
    			fichasOtroJugador ++;
    		}
    	}
    	
    	if(fichasJugadorActual>fichasOtroJugador) {
    		return id;
    	}
    	else if(fichasJugadorActual<fichasOtroJugador){
    		return this.getIdOtherPlayer(session);
    	}
    	
    	return -2;
    	
    }
    
    public boolean manoSinFichas(WebSocketSession session) {
    	boolean tieneFichas = false;
    	for(int i=0; i<this.fichasJugadores.size()&&tieneFichas==false; i++) {
    		if(this.fichasJugadores.get(i).getState().getUser().getSession().getId().equals(session.getId())) {
    			tieneFichas=true;
    		}
    	}
    	return tieneFichas;
    	
    }
    
    
    public int doPlay(WebSocketSession session) {

        int i = this.getPosOfSession(session);

        return i;

    }
    
    
    public boolean posibleColocacion() {
    	boolean continuar = false;
    	FichaDomino fichaIzq = this.tablero.peek();
    	FichaDomino fichaDer = this.tablero.poll();
    	for(int i=0; i<this.fichasJugadores.size()&&continuar==false; i++) {
    		if(fichaIzq.getNumber1()==this.fichasJugadores.get(i).getNumber1() || fichaIzq.getNumber2()==this.fichasJugadores.get(i).getNumber2()) {
    			continuar=true;
    		}
    		if(fichaDer.getNumber2()==this.fichasJugadores.get(i).getNumber1() || fichaDer.getNumber2()==this.fichasJugadores.get(i).getNumber2()) {
    			continuar=true;
    		}
    		
    	}
    	return continuar;
    	
    }
    
    
    
    
    
    public void notifyPlay(WebSocketSession session, boolean posicionTablero) throws IOException {
        int pos = this.getPosOfSession(session);
        if(pos>=0) {
            JSONObject jso = this.toJSON();
            jso.put("type", "matchPlay");
            String name = players.get(pos).getUserName();
            jso.put("playName", name);
            jso.put("fichaN1", this.fichaColocada.getNumber1());
            jso.put("fichaN2", this.fichaColocada.getNumber2());
            jso.put("posicion", posicionTablero);
            for (User player : this.players) {
                player.send(jso);
            }
        }

    }

    public boolean verifyPlay(WebSocketSession session, int number_1, int number_2, boolean posicionTablero) {

        if (session != this.turn) {
            return false;
        }
        
        if(!this.fichaColocada.getState().getUser().getSession().getId().equals(session.getId())) {
        	return false;
        }
        
        boolean existeFicha = false;
        for(int i=0; i < this.fichasJugadores.size()&&existeFicha==false; i++) {
        	if(this.fichasJugadores.get(i).getNumber1()==this.fichaColocada.getNumber1() && this.fichasJugadores.get(i).getNumber2()==this.fichaColocada.getNumber2()) {
        		existeFicha = true;
        	}
        	if(this.fichasJugadores.get(i).getNumber1()==this.fichaColocada.getNumber2() && this.fichasJugadores.get(i).getNumber2()==this.fichaColocada.getNumber1()) {
        		existeFicha = true;
        	}
        }
        if(existeFicha == false) {
        	return false;
        }
        
        boolean colocacionCorrecta = true;
        if(tablero.isEmpty()==false) {
        	colocacionCorrecta = false;
    		if(posicionTablero == true) {
    			FichaDomino fichaIzq = tablero.peek();
    			System.out.println("La ficha del tablero a la izquierda es: "+fichaIzq.getNumber1()+" | "+fichaIzq.getNumber2());
    			if(fichaIzq.getNumber1()==this.fichaColocada.getNumber1()) {
    				System.out.println("Entro en correcta izq rotando"); 
    				
    				this.fichaColocada.setNumber1(number_2);
    				this.fichaColocada.setNumber2(number_1);
    				
    				System.out.println();
    				tablero.addFirst(this.fichaColocada);
    				colocacionCorrecta = true;
    			}
    			if(fichaIzq.getNumber1()==this.fichaColocada.getNumber2()) {
    				System.out.println("Entro en correcta izq sin rotar"); 
    				
    				tablero.addFirst(this.fichaColocada);
    				colocacionCorrecta = true;
    			}
    		}
    		else {
    			FichaDomino fichaDer = tablero.poll();
    			System.out.println("La ficha del tablero a la derecha es: "+fichaDer.getNumber1()+" | "+fichaDer.getNumber2());
    			if(fichaDer.getNumber2()==this.fichaColocada.getNumber1()) {
    				System.out.println("Entro en correcta derecha sin rotar");
    				
    				colocacionCorrecta = true;
    				tablero.addLast(this.fichaColocada);
    			}
    			if(fichaDer.getNumber2()==this.fichaColocada.getNumber2()) {
    				
    				System.out.println("Entro en correcta derecha rotando");
    				
    				this.fichaColocada.setNumber1(number_2);
    				this.fichaColocada.setNumber2(number_1);
    				
    				tablero.addLast(this.fichaColocada);
    				colocacionCorrecta = true;
    			}
    		}
        	
        }
        else {
        	System.out.println("El tablero esta vacío y coloca ficha: "+this.fichaColocada.getNumber1()+" | "+this.fichaColocada.getNumber2());
        	tablero.add(this.fichaColocada);
        }
        
        if(colocacionCorrecta==false) {
        	return false;
        }
        
        return true;
    }
    
    public void quitarFichaMano() {
    	boolean seguir = true;
    	for(int i=0; i<this.fichasJugadores.size()&&seguir==true; i++) {
			if(this.fichasJugadores.get(i).getNumber1()==this.fichaColocada.getNumber1()&&this.fichasJugadores.get(i).getNumber2()==this.fichaColocada.getNumber2()) {
				this.fichasJugadores.remove(i);
				seguir=false;
			}
			if(this.fichasJugadores.get(i).getNumber1()==this.fichaColocada.getNumber2()&&this.fichasJugadores.get(i).getNumber2()==this.fichaColocada.getNumber1()) {
				this.fichasJugadores.remove(i);
				seguir=false;
			}
		}
    }
    
   
    
}
