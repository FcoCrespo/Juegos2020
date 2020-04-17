package edu.uclm.esi.games2020.model;

import org.json.JSONObject;

public class FichaDomino {
    private int number1;
    private int number2;
    private DominoState state;

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
    
    public void rotateFicha() {
    	
    	int aux = this.getNumber1();
    	this.setNumber1(this.getNumber2());
    	this.setNumber2(aux);    	
    	
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

	public DominoState getState() {
		return state;
	}

	public void setState(DominoState state) {
		this.state = state;
	}
    
	public boolean equals(FichaDomino other) {
		System.out.println(other.getNumber1());
		if(this.getNumber1() == other.getNumber1() && this.getNumber2() == other.getNumber2()) {
			return true;
		}
		return false;
	} 
	
}