package edu.uclm.esi.games2020.model;

public class Escoba extends Game {

	public Escoba() {
		super(2);
	}

	@Override
	public String getName() {
		return "Escoba";
	}

	@Override
	protected Match buildMatch() {
		return new EscobaMatch();
	}

}
