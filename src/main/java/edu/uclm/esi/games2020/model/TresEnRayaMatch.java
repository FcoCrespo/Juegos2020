package edu.uclm.esi.games2020.model;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public class TresEnRayaMatch extends Match {
    private int[][] tablero;

    public TresEnRayaMatch() {
        super();
        this.tablero = new int[3][3];

        for (int i = 0; i < this.tablero.length; i++)
            for (int j = 0; j < this.tablero[i].length; j++)
                this.tablero[i][j] = -1;
    }

    @Override
    public void start() throws IOException {
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
    protected void setState(User user) {
        IState state = new TERState();
        user.setState(state);
        state.setUser(user);
    }

    public boolean verifyPlay(WebSocketSession session, int lugarx, int lugary) {

        if (session != this.turn) {
            return false;
        }

        try {
            return this.tablero[lugarx][lugary] == -1;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

    }

    public int doPlay(WebSocketSession session, int lugarx, int lugary) {

        int i = this.getPosOfSession(session);

        if(i>=0){
            this.tablero[lugarx][lugary] = i;
        }

        return i;

    }

    // Devuelve false si el tablero no está completo y true si lo esta
    private boolean fullTable() {

        boolean b = true;

        for (int i = 0; i < this.tablero.length; i++) {
            for (int j = 0; j < this.tablero[i].length; j++) {
                if (this.tablero[i][j] == -1) {
                    return false;
                }
            }
        }

        return b;
    }

    public int winner(int lugarx, int lugary, int id) {
        if (checkDiagonal1(lugarx, lugary, id) || checkDiagonal2(lugarx, lugary, id) || checkFila(lugarx, id) || checkColumna(lugary, id)) {
            return id;
        } else if (this.fullTable()) {
            return -2;
        } else {
            return -1;
        }

    }

    private boolean checkDiagonal1(int lugarx, int lugary, int id) {

        int fichasEnLinea = 0;

        for (int i = lugarx, j = lugary; i >= 0 && j < tablero.length; i--, j++) {
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        for(int i = lugarx+1, j = lugary-1; i < tablero.length && j>=0; i++, j--){
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        return fichasEnLinea >= 3;
    }

    private boolean checkDiagonal2(int lugarx, int lugary, int id) {
        int fichasEnLinea = 0;

        for (int i = lugarx, j = lugary; i >= 0 && j >= 0; i--, j--) {
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        for (int i = lugarx+1, j = lugary+1; i < tablero.length && j < tablero.length; i++, j++) {
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        return fichasEnLinea >= 3;
    }

    private boolean checkFila(int lugarx, int id) {
        int fichasEnLinea = 0;

        for (int j = 0; j < tablero[0].length; j++) {

            if (tablero[lugarx][j] == id) {
                fichasEnLinea++;
            } else if (fichasEnLinea < 3) {
                fichasEnLinea = 0;
            }
        }
        return fichasEnLinea >= 3;
    }

    private boolean checkColumna(int lugary, int id) {
        int fichasEnLinea = 0;

        for (int i = 0; i < tablero.length; i++) {

            if (tablero[i][lugary] == id) {
                fichasEnLinea++;
            } else if (fichasEnLinea < 3) {
                fichasEnLinea = 0;
            }
        }
        return fichasEnLinea >= 3;
    }


    public void notifyPlay(WebSocketSession session, int lugarx, int lugary) throws IOException {
        int pos = this.getPosOfSession(session);
        if(pos>=0) {
            JSONObject jso = this.toJSON();
            jso.put("type", "matchPlay");
            String name = players.get(pos).getUserName();
            jso.put("playName", name);
            jso.put("play_x", lugarx);
            jso.put("play_y", lugary);
            for (User player : this.players) {
                player.send(jso);
            }
        }

    }

	@Override
	public String play(JSONObject jso, WebSocketSession session) throws IOException {
	
			int x = jso.getInt("lugar_x");
			int y = jso.getInt("lugar_y");
		
			
			if(this.verifyPlay(session, x, y)) {

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
					return this.players.get(w).getUserName();
				}
			}else {
				this.notifyInvalidPlay(session, "Invalid play");
			}
		return null;
		
	}
}

