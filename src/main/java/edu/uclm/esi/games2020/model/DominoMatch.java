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
        this.fichaColocada = null;
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
    		this.fichasJugadores.add(ficha);
    		jsaFichasJugador.put(ficha.toJSON());
    		System.out.println("Ficha : "+ficha.getNumber1()+" | "+ficha.getNumber2());
    	}
    	
        JSONObject jso = new JSONObject();
        jso.put("data", jsaFichasJugador);
        return jso;
    }

    public FichaDomino obtenerFichaJugador(int number_1, int number_2) {
    	
    	System.out.println("Las fichas que hay en la mano son: ");
    	
    	for (FichaDomino elem : this.fichasJugadores) {
    	    System.out.print("["+elem.getNumber1()+" | "+elem.getNumber2()+"] ");
    	}
    	System.out.println("");
    	
    	FichaDomino ficha = null;
    	int i=0;
    	int pos = 0;
    	boolean seguir = true;
    	do{
    		
    		if(this.fichasJugadores.get(i).getNumber1()==number_1 && this.fichasJugadores.get(i).getNumber2()==number_2 ) {
    			seguir = false;
    			pos=i;
    		}
    		i=i+1;
    	}while(i < this.fichasJugadores.size() && seguir==true);
    	
    	if(seguir==false) {
    		ficha = this.fichasJugadores.get(pos);
    		
    		System.out.println("La ficha cogida de la mano es: "+ficha.getNumber1()+" | "+ficha.getNumber2());
    		return ficha;
    	}
    	
    	return null;
    }
    
    @Override
	public void play(JSONObject jso, WebSocketSession session) throws IOException {
    		
	    	 
			int number_1 = jso.getInt("number_1");
			int number_2 = jso.getInt("number_2");
			System.out.println("Llega la ficha con los valores: "+number_1+" | "+number_2);
			
			boolean posicionTablero = jso.getBoolean("posicion");
			
			//this.fichaColocada = new FichaDomino(number_1,number_2);
			
			if (session == this.turn) {
				this.fichaColocada = obtenerFichaJugador(number_1,number_2);
				if(this.fichaColocada==null) {
					this.notifyInvalidPlay(session, "La ficha no existe");
				}
				else {
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
						this.notifyInvalidPlay(session, "Jugada inválida");
					}
				}			
	        }
			else {
				this.notifyInvalidPlay(session, "No es su turno");
			}
		
		
	}
    
    public void robar(WebSocketSession session) throws IOException {
    	if (session == this.turn) {
    		if(this.deck.getFichas().size()!=0) {
    			FichaDomino ficha = this.deck.getFicha();
        		ficha.setState(this.getUserSession(session).getState());
        		this.fichasJugadores.add(ficha);
        		System.out.println("el jugador roba la ficha : "+ficha.getNumber1()+" | "+ficha.getNumber2());
        		int pos = this.getPosOfSession(session);
                if(pos>=0) {
                    JSONObject jso = this.toJSON();
                    jso.put("type", "cardRobbed");
                    String name = players.get(pos).getUserName();
                    jso.put("playName", name);
                    jso.put("fichaN1", ficha.getNumber1());
                    jso.put("fichaN2", ficha.getNumber2());
                    User player = this.getUserSession(session);
                    player.send(jso);
                    
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
                }
    		}
    		else {
    			this.notifyInvalidPlay(session, "Ya no hay más fichas para robar.");
    		}
    	}
		else {
			this.notifyInvalidPlay(session, "No es su turno.");
		}
    }
    
    
    public void pasar(WebSocketSession session) throws IOException {
    	if (session == this.turn) {
        		int pos = this.getPosOfSession(session);
                if(pos>=0) {
                    JSONObject jso = this.toJSON();
                    jso.put("type", "matchChangeTurn");
                    String name = players.get(pos).getUserName();
                    jso.put("playName", name);
                    for (User player : this.players) {
                        player.send(jso);
                    }
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
                }
    		}
    	else {
    		this.notifyInvalidPlay(session, "No es su turno.");
    	}
    	
    }
		
	
    
    public int winner(WebSocketSession session, int id) {
        if (manoSinFichas(session)==false) {
            return id;
        }
        else if(this.deck.getFichas().size()==0) {
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
    	FichaDomino fichaDer = this.tablero.peekLast();
    	for(int i=0; i<this.fichasJugadores.size()&&continuar==false; i++) {
    		if(fichaIzq.getNumber1()==this.fichasJugadores.get(i).getNumber1() || fichaIzq.getNumber2()==this.fichasJugadores.get(i).getNumber2()) {
    			continuar=true;
    		}
    		else if(fichaDer.getNumber2()==this.fichasJugadores.get(i).getNumber1() || fichaDer.getNumber2()==this.fichasJugadores.get(i).getNumber2()) {
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
        if(tablero.size()!=0) {
        	System.out.println("El estado del tablero de tamaño " +this.tablero.size()+ "es antes de colocar: ");
        	int i = 0;
        	for (FichaDomino elem : this.tablero) {
        	    System.out.print("["+elem.getNumber1()+" | "+elem.getNumber2()+"] ");
        	}
        	System.out.println("");
        	colocacionCorrecta = false;
    		if(posicionTablero == true) {
    			FichaDomino fichaIzq = tablero.peek();
    			System.out.println("La ficha del tablero a la izquierda es: "+fichaIzq.getNumber1()+" | "+fichaIzq.getNumber2());
    			if(fichaIzq.getNumber1()==this.fichaColocada.getNumber1()) {
    				System.out.println("Entro en correcta izq rotando"); 
    				System.out.println("El jugador ha colocado la ficha: "+this.fichaColocada.getNumber1()+" | "+this.fichaColocada.getNumber2());
    				this.fichaColocada.setNumber1(number_2);
    				this.fichaColocada.setNumber2(number_1);
    				
    				System.out.println();
    				tablero.addFirst(this.fichaColocada);
    				colocacionCorrecta = true;
    			}
    			else if(fichaIzq.getNumber1()==this.fichaColocada.getNumber2()) {
    				System.out.println("Entro en correcta izq sin rotar"); 
    				System.out.println("El jugador ha colocado la ficha: "+this.fichaColocada.getNumber1()+" | "+this.fichaColocada.getNumber2());

    				tablero.addFirst(this.fichaColocada);
    				colocacionCorrecta = true;
    			}
    		}
    		else {
    			FichaDomino fichaDer = tablero.peekLast();
    			System.out.println("La ficha del tablero a la derecha es: "+fichaDer.getNumber1()+" | "+fichaDer.getNumber2());
    			if(fichaDer.getNumber2()==this.fichaColocada.getNumber1()) {
    				System.out.println("Entro en correcta derecha sin rotar");
    				System.out.println("El jugador ha colocado la ficha: "+this.fichaColocada.getNumber1()+" | "+this.fichaColocada.getNumber2());

    				colocacionCorrecta = true;
    				tablero.addLast(this.fichaColocada);
    			}
    			else if(fichaDer.getNumber2()==this.fichaColocada.getNumber2()) {
    				
    				System.out.println("Entro en correcta derecha rotando");
    				System.out.println("El jugador ha colocado la ficha: "+this.fichaColocada.getNumber1()+" | "+this.fichaColocada.getNumber2());

    				this.fichaColocada.setNumber1(number_2);
    				this.fichaColocada.setNumber2(number_1);
    				
    				tablero.addLast(this.fichaColocada);
    				colocacionCorrecta = true;
    			}
    		}
        	
        }
        else {
        	System.out.println("El tablero esta vacío y coloca ficha: "+this.fichaColocada.getNumber1()+" | "+this.fichaColocada.getNumber2());
        	tablero.addFirst(this.fichaColocada);
        }
        
        if(colocacionCorrecta==false) {
        	return false;
        }
        
        return true;
    }
    
    public void quitarFichaMano() {
    	boolean seguir = true;
    	System.out.println("El tamaño de las fichas de la mano es: "+this.fichasJugadores.size());
    	
    	for (FichaDomino elem : this.fichasJugadores) {
    	    System.out.print("["+elem.getNumber1()+" | "+elem.getNumber2()+"] ");
    	}
    	System.out.println("");
    	
    	int i=0;
    	int pos = 0;
    	for(i=0; i<this.fichasJugadores.size()&&seguir==true; i++) {
			if(this.fichasJugadores.get(i).getNumber1()==this.fichaColocada.getNumber1()&&this.fichasJugadores.get(i).getNumber2()==this.fichaColocada.getNumber2()) {
				pos=i;
				seguir=false;
			}
			else if(this.fichasJugadores.get(i).getNumber1()==this.fichaColocada.getNumber2()&&this.fichasJugadores.get(i).getNumber2()==this.fichaColocada.getNumber1()) {
				pos=i;
				seguir=false;
			}
		}
    	
    	FichaDomino fichaEliminada = this.fichasJugadores.remove(pos);
    	System.out.println("La ficha eliminada de la mano es: "+fichaEliminada.getNumber1()+" | "+fichaEliminada.getNumber2());
    	
    	System.out.println("El tamaño de las fichas de la mano tras eliminar esta ficha es: "+this.fichasJugadores.size());
    	
    	for (FichaDomino elem : this.fichasJugadores) {
    	    System.out.print("["+elem.getNumber1()+" | "+elem.getNumber2()+"] ");
    	}
    	System.out.println("");
    }


	
    
   
    
}
