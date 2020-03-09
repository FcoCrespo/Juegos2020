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
    protected void setState(User user) {
        IState state = new TERState();
        user.setState(state);
        state.setUser(user);
    }

    public boolean verifyPlay(WebSocketSession session, int lugar_x, int lugar_y) {

        if (session != this.turn) {
            return false;
        }

        try {
            return this.tablero[lugar_x][lugar_y] == -1;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

    }

    public int doPlay(WebSocketSession session, int lugar_x, int lugar_y) {

        int i = this.getPosOfSession(session);

        if(i>=0){
            this.tablero[lugar_x][lugar_y] = i;
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

    public int winner(int lugar_x, int lugar_y, int id) {
        if (checkDiagonal1(lugar_x, lugar_y, id) || checkDiagonal2(lugar_x, lugar_y, id) || checkFila(lugar_x, id) || checkColumna(lugar_y, id)) {
            return id;
        } else if (this.fullTable()) {
            return -2;
        } else {
            return -1;
        }

    }

    private boolean checkDiagonal1(int lugar_x, int lugar_y, int id) {

        int fichasEnLinea = 0;

        for (int i = lugar_x, j = lugar_y; i >= 0 && j < tablero.length; i--, j++) {
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        for(int i = lugar_x+1, j = lugar_y-1; i < tablero.length && j>=0; i++, j--){
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        return fichasEnLinea >= 3;
    }

    private boolean checkDiagonal2(int lugar_x, int lugar_y, int id) {
        int fichasEnLinea = 0;

        for (int i = lugar_x, j = lugar_y; i >= 0 && j >= 0; i--, j--) {
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        for (int i = lugar_x+1, j = lugar_y+1; i < tablero.length && j < tablero.length; i++, j++) {
            if(tablero[i][j] == id){
                fichasEnLinea++;
            }else if (fichasEnLinea<3){
                fichasEnLinea = 0;
            }
        }

        return fichasEnLinea >= 3;
    }

    private boolean checkFila(int lugar_x, int id) {
        int fichasEnLinea = 0;

        for (int j = 0; j < tablero[0].length; j++) {

            if (tablero[lugar_x][j] == id) {
                fichasEnLinea++;
            } else if (fichasEnLinea < 3) {
                fichasEnLinea = 0;
            }
        }
        return fichasEnLinea >= 3;
    }

    private boolean checkColumna(int lugar_y, int id) {
        int fichasEnLinea = 0;

        for (int i = 0; i < tablero.length; i++) {

            if (tablero[i][lugar_y] == id) {
                fichasEnLinea++;
            } else if (fichasEnLinea < 3) {
                fichasEnLinea = 0;
            }
        }
        return fichasEnLinea >= 3;
    }


    public void notifyPlay(WebSocketSession session, int lugar_x, int lugar_y) throws IOException {
        int pos = this.getPosOfSession(session);
        if(pos>=0) {
            JSONObject jso = this.toJSON();
            jso.put("type", "matchPlay");
            String name = players.get(pos).getUserName();
            jso.put("playName", name);
            jso.put("play_x", lugar_x);
            jso.put("play_y", lugar_y);
            for (User player : this.players) {
                player.send(jso);
            }
        }

    }
}

