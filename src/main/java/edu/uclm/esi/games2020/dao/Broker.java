package edu.uclm.esi.games2020.dao;

public class Broker {
    private Pool pool;
    

    private Broker() {
        pool = new Pool(10);
    }

    private static class BrokerHolder {
        static Broker singleton = new Broker();
    }

    public static Broker get() {
        return BrokerHolder.singleton;
    }

    public WrapperConnection getBd(){
        return pool.getConnection();
    }
}
