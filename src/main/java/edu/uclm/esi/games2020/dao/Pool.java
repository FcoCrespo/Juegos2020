package edu.uclm.esi.games2020.dao;

import java.util.ArrayList;

public class Pool {

    private ArrayList<WrapperConnection> libres;
    private ArrayList<WrapperConnection> ocupadas;

    public Pool(int numeroDeConexiones) {
        this.libres = new ArrayList<>(numeroDeConexiones);
        this.ocupadas = new ArrayList<>(numeroDeConexiones);

        for (int i = 0; i < numeroDeConexiones; i++) {
            this.libres.add(new WrapperConnection(this));
        }
    }

    public WrapperConnection getConnection() {
        WrapperConnection result = this.libres.remove(0);
        this.ocupadas.add(result);
        return result;
    }

    public void liberame(WrapperConnection wrapperConnection) {
        this.ocupadas.remove(wrapperConnection);
        this.libres.add(wrapperConnection);
    }
}
