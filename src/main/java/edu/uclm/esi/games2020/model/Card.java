package edu.uclm.esi.games2020.model;

import org.json.JSONObject;

public class Card {
	private int number;
	private Suit suit;
	private IState state;
	
	public Card(int number, Suit suit) {
		super();
		this.number = number;
		this.suit = suit;
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("number", this.number);
		jso.put("suit", this.suit);
		return jso;
	}

	public void setState(IState state) {
		this.state = state;
	}
	
}
