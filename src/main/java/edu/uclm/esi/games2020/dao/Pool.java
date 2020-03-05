package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.util.Vector;

public class Pool {
	
	private Vector<WrapperConnection> libres;
	private Vector<WrapperConnection> ocupadas;
	
	public Pool(int numeroDeConexiones) {
		this.libres = new Vector<>(numeroDeConexiones);
		this.ocupadas = new Vector<>(numeroDeConexiones);
		
		for(int i=0; i<numeroDeConexiones; i++) {
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
