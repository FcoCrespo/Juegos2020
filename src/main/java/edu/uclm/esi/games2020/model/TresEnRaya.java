package edu.uclm.esi.games2020.model;

public class TresEnRaya extends Game {

    public TresEnRaya() {
        super(2);
    }

    @Override
    public String getName() {
        return "Tres En Raya";
    }

    @Override
    protected Match buildMatch() {
        return new TresEnRayaMatch();
    }

}
