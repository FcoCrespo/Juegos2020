package edu.uclm.esi.games2020.model;

public class Domino extends Game {

        public Domino() {
            super(2);
        }

        @Override
        public String getName() {
            return "Domino";
        }

        @Override
        protected Match buildMatch() {
            return new TresEnRayaMatch();
        }
}
