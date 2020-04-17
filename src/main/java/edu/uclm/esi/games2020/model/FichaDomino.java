package edu.uclm.esi.games2020.model;

import org.json.JSONObject;

public class FichaDomino {
    private int number1;
    private int number2;

    public FichaDomino(int number1, int number2) {
        super();
        this.number1 = number1;
        this.number2 = number2;
    }

    public JSONObject toJSON() {
        JSONObject jso = new JSONObject();
        jso.put("number1", this.number1);
        jso.put("number2", this.number2);
        return jso;
    }
}