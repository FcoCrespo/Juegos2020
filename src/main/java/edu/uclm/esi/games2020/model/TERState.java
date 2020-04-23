package edu.uclm.esi.games2020.model;

public class TERState implements IState {
    private User user;

    @Override
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
	public User getUser() {
		return user;
	}
    
}
