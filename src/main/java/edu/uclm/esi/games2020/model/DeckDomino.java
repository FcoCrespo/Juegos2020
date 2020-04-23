package edu.uclm.esi.games2020.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DeckDomino {
    private List<FichaDomino> fichas;

    public DeckDomino() {
        this.setFichas(new ArrayList<>());
        int max = 7;
        int min = 0;
        for (int i = min; i < max; i++) {
            for (int j = min; j < max; j++){
                FichaDomino f = new FichaDomino(i, j);
                this.getFichas().add(f);
            }
            min++;
        }
    }

    public void suffle() {
        SecureRandom dado = new SecureRandom();
        for (int i = 0; i < 200; i++) {
            int a = dado.nextInt(this.getFichas().size());
            int b = dado.nextInt(this.getFichas().size());
            FichaDomino auxiliar = this.getFichas().get(a);
            this.getFichas().set(a, this.getFichas().get(b));
            this.getFichas().set(b, auxiliar);
        }
    }

    public FichaDomino getFicha() {
        return this.getFichas().remove(0);
    }

	public List<FichaDomino> getFichas() {
		return fichas;
	}
	

	public void setFichas(List<FichaDomino> fichas) {
		this.fichas = fichas;
	}
}
