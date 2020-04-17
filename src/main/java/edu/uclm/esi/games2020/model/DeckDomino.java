package edu.uclm.esi.games2020.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DeckDomino {
    private List<FichaDomino> fichas;

    public DeckDomino() {
        this.fichas = new ArrayList<>();
        int MaxInicial = 6;
        int MinInicial = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = MinInicial; j < MaxInicial; j++){
                FichaDomino f = new FichaDomino(i, j);
                this.fichas.add(f);
            }
            MinInicial++;
            MaxInicial--;
        }
    }

    public void suffle() {
        SecureRandom dado = new SecureRandom();
        for (int i = 0; i < 200; i++) {
            int a = dado.nextInt(this.fichas.size());
            int b = dado.nextInt(this.fichas.size());
            FichaDomino auxiliar = this.fichas.get(a);
            this.fichas.set(a, this.fichas.get(b));
            this.fichas.set(b, auxiliar);
        }
    }

    public FichaDomino getCard() {
        return this.fichas.remove(0);
    }
}
