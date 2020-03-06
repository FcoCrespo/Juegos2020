package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public WrapperConnection getBd() throws SQLException {
        return pool.getConnection();
    }
}
