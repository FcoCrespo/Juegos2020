package edu.uclm.esi.games2020.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

public class DominoMatch extends Match {

	private Deque<FichaDomino> tablero;
	private DeckDomino deck;
	private List<FichaDomino> fichasJugadores;
	private FichaDomino fichaColocada;
	private final Logger log = Logger.getLogger(DominoMatch.class.getName());
	private static final String EMPATA_PARTIDA = "La partida ha finalizado en empate";
	private static final String GANA_PARTIDA = " ha ganado la partida";
	private static final String JSON_PLAYER = "playName";

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
	public void start() throws IOException {
		this.started = true;
		super.notifyStart();
		super.inicializaTurn();
	}

	@Override
	protected JSONObject startData(User player) {
		FichaDomino ficha;
		JSONArray jsaFichasJugador = new JSONArray();

		for (FichaDomino fichasJugadore : fichasJugadores)
			log.info("\nFicha que tienen los jugadores : " + fichasJugadore.getNumber1() + " | "
					+ fichasJugadore.getNumber2());

		for (int i = 0; i < 7; i++) {
			ficha = this.deck.getFicha();
			ficha.setState(player.getState());
			this.fichasJugadores.add(ficha);
			jsaFichasJugador.put(ficha.toJSON());
			log.info("\nFicha : " + ficha.getNumber1() + " | " + ficha.getNumber2());
		}

		JSONObject jso = new JSONObject();
		jso.put("data", jsaFichasJugador);
		return jso;
	}

	public FichaDomino obtenerFichaJugador(int number1, int number2) {

		log.info("\nLas fichas que hay en la mano son: ");

		for (FichaDomino elem : this.fichasJugadores)
			log.info("[" + elem.getNumber1() + " | " + elem.getNumber2() + "] ");

		log.info("\n");

		FichaDomino ficha;
		int i = 0;
		int pos = 0;
		boolean seguir = true;
		do {

			if (this.fichasJugadores.get(i).getNumber1() == number1
					&& this.fichasJugadores.get(i).getNumber2() == number2) {
				seguir = false;
				pos = i;
			}
			i = i + 1;
		} while (i < this.fichasJugadores.size() && seguir);

		if (!seguir) {
			ficha = this.fichasJugadores.get(pos);

			log.info("\nLa ficha cogida de la mano es: " + ficha.getNumber1() + " | " + ficha.getNumber2());
			return ficha;
		}

		return null;
	}

	@Override
	public String play(JSONObject jso, WebSocketSession session) throws IOException {
		if (jso.getString("type").equals("doPlayDO"))
			return this.doPlayDO(jso, session);

		if (jso.getString("type").equals("robCard"))
			this.robar(session);

		if (jso.getString("type").equals("passTurn"))
			this.pasar(session);

		return null;
	}

	public String doPlayDO(JSONObject jso, WebSocketSession session) throws IOException {

		int number1 = jso.getInt("number_1");
		int number2 = jso.getInt("number_2");
		log.info("\nLlega la ficha con los valores: \"+number1+\" | \"+number2");

		boolean posicionTablero = jso.getBoolean("posicion");

		if (session == this.turn) {
			this.fichaColocada = obtenerFichaJugador(number1, number2);
			if (this.fichaColocada == null) {
				this.notifyInvalidPlay(session, "La ficha no existe");
			} else {
				FichaDomino f = this.getHigherDouble(this.fichasJugadores);
				if(f==null || this.tablero.isEmpty() && !fichaColocada.iguales(f)) {
					this.notifyInvalidPlay(session, "Usa el mayor doble");				
				} else {
					log.info("\nEl jugador " + this.getNamePlayerSession(session));
					log.info("\nFicha colocada: " + this.fichaColocada.getNumber1() + " | "
							+ this.fichaColocada.getNumber2());
					log.info("\nLa ficha pertenece al jugador con la sesion: "
							+ this.fichaColocada.getState().getUser().getSession().getId());
					log.info("\nLa sesion del jugador es: " + session.getId());
	
					if (this.verifyPlay(session, number1, number2, posicionTablero)) {
						quitarFichaMano();
						this.notifyPlay(session, posicionTablero);
						return this.notifyNext(session);
					} else {
						this.notifyInvalidPlay(session, "Jugada inválida");
					}
				}
			}
		} else {
			this.notifyInvalidPlay(session, "No es su turno");
		}
		return null;
	}

	public String notifyNext(WebSocketSession session) throws IOException {
		int w = this.winner(session, doPlay(session));

		if (w == -1) {
			this.notifyTurn(this.rotateTurn(session));
		} else if (w == -2) {
			this.notifyFinish(EMPATA_PARTIDA);
		} else {
			this.notifyFinish(this.players.get(w).getUserName() + GANA_PARTIDA);
			return this.players.get(w).getUserName();
		}
		return null;
	}

	public void robar(WebSocketSession session) throws IOException {
		if (session == this.turn) {
			if (!this.deck.getFichas().isEmpty()) {
				FichaDomino ficha = this.deck.getFicha();
				ficha.setState(this.getUserSession(session).getState());
				this.fichasJugadores.add(ficha);
				log.info("\nel jugador roba la ficha : " + ficha.getNumber1() + " | " + ficha.getNumber2());
				int pos = this.getPosOfSession(session);
				if (pos >= 0) {
					JSONObject jso = this.toJSON();
					jso.put("type", "cardRobbed");
					String name = players.get(pos).getUserName();
					jso.put(JSON_PLAYER, name);
					jso.put("fichaN1", ficha.getNumber1());
					jso.put("fichaN2", ficha.getNumber2());
					User player = this.getUserSession(session);
					player.send(jso);
					this.notifyNext(session);
				}
			} else {
				this.notifyInvalidPlay(session, "Ya no hay más fichas para robar.");
			}
		} else {
			this.notifyInvalidPlay(session, "No es su turno.");
		}
	}

	public void pasar(WebSocketSession session) throws IOException {
		if (session == this.turn) {
			if (!this.deck.getFichas().isEmpty()) {
				this.notifyInvalidPlay(session, "Roba ficha primero.");
			} else {
				int pos = this.getPosOfSession(session);
				if (pos >= 0) {
					JSONObject jso = this.toJSON();
					jso.put("type", "matchChangeTurn");
					String name = players.get(pos).getUserName();
					jso.put(JSON_PLAYER, name);
					for (User player : this.players) {
						player.send(jso);
					}
					this.notifyNext(session);
				}
			}
		} else {
			this.notifyInvalidPlay(session, "No es su turno.");
		}

	}

	public int winner(WebSocketSession session, int id) {
		if (!manoSinFichas(session)) {
			return id;
		} else if (this.deck.getFichas().isEmpty() && this.tablero.isEmpty()) {
			boolean continuar = posibleColocacion();
			if (!continuar) {

				return contarFichasJugadores(session, id);
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public int contarFichasJugadores(WebSocketSession session, int id) {
		int fichasJugadorActual = 0;
		int fichasOtroJugador = 0;

		for (FichaDomino fichasJugadore : this.fichasJugadores) {
			if (fichasJugadore.getState().getUser().getSession().getId().equals(session.getId())) {
				fichasJugadorActual = fichasJugadorActual + fichasJugadore.getNumber1() + fichasJugadore.getNumber2();
			} else {
				fichasOtroJugador = fichasOtroJugador + fichasJugadore.getNumber1() + fichasJugadore.getNumber2();
			}
		}

		if (fichasJugadorActual < fichasOtroJugador) {
			return id;
		} else if (fichasJugadorActual > fichasOtroJugador) {
			return this.getIdOtherPlayer(session);
		}

		return -2;

	}

	public boolean manoSinFichas(WebSocketSession session) {
		boolean tieneFichas = false;
		String fichasmano = "Las fichas que quedan en las manos de los jugadores son: " + fichasJugadores.size();
		log.info(fichasmano);

		for (FichaDomino elem : this.fichasJugadores) {
			log.info("\n[" + elem.getNumber1() + " | " + elem.getNumber2() + "] y pertene a: "
					+ elem.getState().getUser().getUserName() + ". ");
		}
		log.info("\n");

		for (int i = 0; i < this.fichasJugadores.size() && !tieneFichas; i++) {
			if (this.fichasJugadores.get(i).getState().getUser().getSession().getId().equals(session.getId())) {
				tieneFichas = true;
			}
		}
		return tieneFichas;

	}

	public int doPlay(WebSocketSession session) {

		return this.getPosOfSession(session);

	}

	public boolean posibleColocacion() {
		FichaDomino fichaIzq = this.tablero.peek();
		FichaDomino fichaDer = this.tablero.peekLast();
		for (FichaDomino fichasJugadore : this.fichasJugadores) {
			assert fichaIzq != null;
			if (fichaIzq.getNumber1() == fichasJugadore.getNumber1()
					|| fichaIzq.getNumber1() == fichasJugadore.getNumber2()) {
				return true;
			}

			if (fichaDer.getNumber2() == fichasJugadore.getNumber1()
					|| fichaDer.getNumber2() == fichasJugadore.getNumber2()) {
				return true;
			}
		}
		return false;
	}

	public void notifyPlay(WebSocketSession session, boolean posicionTablero) throws IOException {
		int pos = this.getPosOfSession(session);
		if (pos >= 0) {
			JSONObject jso = this.toJSON();
			jso.put("type", "matchPlay");
			String name = players.get(pos).getUserName();
			jso.put(JSON_PLAYER, name);
			jso.put("fichaN1", this.fichaColocada.getNumber1());
			jso.put("fichaN2", this.fichaColocada.getNumber2());
			jso.put("posicion", posicionTablero);
			for (User player : this.players) {
				player.send(jso);
			}
		}

	}

	public boolean fichaPerteneJugador(WebSocketSession session) {
		return this.fichaColocada.getState().getUser().getSession().getId().equals(session.getId());
	}

	public boolean fichaEnMano() {
		for (FichaDomino fichasJugadore : this.fichasJugadores) {
			if (fichasJugadore.getNumber1() == this.fichaColocada.getNumber1()
					&& fichasJugadore.getNumber2() == this.fichaColocada.getNumber2()) {
				return true;
			}
			if (fichasJugadore.getNumber1() == this.fichaColocada.getNumber2()
					&& fichasJugadore.getNumber2() == this.fichaColocada.getNumber1()) {
				return true;
			}
		}
		return false;
	}

	public boolean verifyPlay(WebSocketSession session, int number1, int number2, boolean posicionTablero) {

		if (!fichaPerteneJugador(session)) {
			return false;
		}

		if (!fichaEnMano()) {
			return false;
		}

		boolean colocacionCorrecta = true;
		if (!tablero.isEmpty()) {

			colocacionCorrecta = false;
			if (posicionTablero) {
				FichaDomino fichaIzq = tablero.peek();
				assert fichaIzq != null;

				if (fichaIzq.getNumber1() == this.fichaColocada.getNumber1()) {

					this.fichaColocada.setNumber1(number2);
					this.fichaColocada.setNumber2(number1);

					tablero.addFirst(this.fichaColocada);
					colocacionCorrecta = true;
				} else if (fichaIzq.getNumber1() == this.fichaColocada.getNumber2()) {

					tablero.addFirst(this.fichaColocada);
					colocacionCorrecta = true;
				}
			} else {
				FichaDomino fichaDer = tablero.peekLast();
				assert fichaDer != null;

				if (fichaDer.getNumber2() == this.fichaColocada.getNumber1()) {
					colocacionCorrecta = true;
					tablero.addLast(this.fichaColocada);
				} else if (fichaDer.getNumber2() == this.fichaColocada.getNumber2()) {

					this.fichaColocada.setNumber1(number2);
					this.fichaColocada.setNumber2(number1);

					tablero.addLast(this.fichaColocada);
					colocacionCorrecta = true;
				}
			}

		} else {
			tablero.addFirst(this.fichaColocada);
		}

		return colocacionCorrecta;
	}

	public void quitarFichaMano() {
		String estadoFichasMano = "\nEl tamaño de las fichas de la mano es: " + this.fichasJugadores.size();
		log.info(estadoFichasMano);

		for (FichaDomino elem : this.fichasJugadores) {
			String estadoFichaJugadores = "\n[" + elem.getNumber1() + " | " + elem.getNumber2() + "] ";
			log.info(estadoFichaJugadores);
		}
		log.info("\n");

		int i;
		int pos = 0;
		for (i = 0; i < this.fichasJugadores.size(); i++) {
			if (this.fichasJugadores.get(i).getNumber1() == this.fichaColocada.getNumber1()
					&& this.fichasJugadores.get(i).getNumber2() == this.fichaColocada.getNumber2()) {
				pos = i;
			}

			if (this.fichasJugadores.get(i).getNumber1() == this.fichaColocada.getNumber2()
					&& this.fichasJugadores.get(i).getNumber2() == this.fichaColocada.getNumber1()) {
				pos = i;
			}
		}
		FichaDomino fichaEliminada = this.fichasJugadores.remove(pos);
		String estadoFichaEliminada = "\nLa ficha eliminada de la mano es: " + fichaEliminada.getNumber1() + " | "
				+ fichaEliminada.getNumber2();
		log.info(estadoFichaEliminada);

		String estadoDeck = "\nEl tamaño de las fichas de la mano tras eliminar esta ficha es: "
				+ this.fichasJugadores.size();
		log.info(estadoDeck);

		for (FichaDomino elem : this.fichasJugadores) {
			String estadoFichaDeck = "\n[" + elem.getNumber1() + " | " + elem.getNumber2() + "] ";
			log.info(estadoFichaDeck);
		}
		log.info("\n");
	}

	@Override
	public String inicializaTurn() throws IOException {

		User u = this.getStartingPlayer();

		if (u != null) {
			this.turn = u.getSession();
			this.notifyTurn(u.getUserName());
			return u.getUserName();
		} else {
			return super.inicializaTurn(); // Si ningun jugador tiene un doble, turno aleatorio
		}

	}

	private User getStartingPlayer() {

		FichaDomino f = this.getHigherDouble(this.fichasJugadores);
		if (f != null)
			return f.getState().getUser();
		return null;
	}

	private FichaDomino getHigherDouble(List<FichaDomino> fichas) {

		int higher = 6;
		boolean found = false;
		FichaDomino doble = null;

		while (!found && higher > -1) {

			doble = new FichaDomino(higher, higher);
			doble = this.find(doble, fichas);
			if (doble != null)
				found = true;
			higher--;
		}

		if (!found)
			doble = null;

		return doble;

	}

	private FichaDomino find(FichaDomino doble, List<FichaDomino> fichas) {

		for (FichaDomino ficha : fichas) {
			if (ficha.iguales(doble)) {
				return ficha;
			}
		}
		return null;
	}

}
