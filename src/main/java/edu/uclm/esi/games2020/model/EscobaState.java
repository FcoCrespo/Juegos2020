package edu.uclm.esi.games2020.model;

public class EscobaState implements IState {
	private User user;

	@Override
	public void setUser(User user) {
		this.user = user;
	}
}
