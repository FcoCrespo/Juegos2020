package edu.uclm.esi.games2020.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

public class DominoMatch extends Match {

    private FichaDomino[][] tablero;
    private List<Integer> puntuaciones;
    private DeckDomino deck;

    public DominoMatch() {
        super();
        this.deck = new DeckDomino();
        this.deck.suffle();
        this.tablero = new FichaDomino[5][5];

        for (int i = 0; i < this.tablero.length; i++)
            for (int j = 0; j < this.tablero[i].length; j++)
                this.tablero[i][j] = null;
    }


    @Override
    protected void setState(User user) {

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
}
