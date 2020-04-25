package edu.uclm.esi.games2020.model;

import org.json.JSONObject;

public class FichaDomino {
    private int number1;
    private int number2;
    private IState state;

    public FichaDomino(int number1, int number2) {
        super();
        this.setNumber1(number1);
        this.setNumber2(number2);
    }

    public JSONObject toJSON() {
        JSONObject jso = new JSONObject();
        jso.put("number1", this.getNumber1());
        jso.put("number2", this.getNumber2());
        return jso;
    }

	public int getNumber1() {
		return number1;
	}

	public void setNumber1(int number1) {
		this.number1 = number1;
	}

	public int getNumber2() {
		return number2;
	}

	public void setNumber2(int number2) {
		this.number2 = number2;
	}

	public IState getState() {
		return state;
	}

	public void setState(IState state) {
		this.state = state;
	}
    	
	@Override
	public boolean equals(Object other) {
		return (this.getNumber1() == ((FichaDomino) other).getNumber1() && this.getNumber2() == ((FichaDomino) other).getNumber2());
	} 
	
}