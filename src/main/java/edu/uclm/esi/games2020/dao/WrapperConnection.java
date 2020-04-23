package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;


public class WrapperConnection implements AutoCloseable {

    private Pool pool;
    private Connection connection;
    private final Logger log = Logger.getLogger(WrapperConnection.class.getName());

    public WrapperConnection(Pool pool) {
        this.pool = pool;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/software?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            this.connection = DriverManager.getConnection(url, "software", "software");
        } catch (Exception e) {
        	log.info("La conexión con la BBDD ha fallado");
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void close() throws Exception {
        this.pool.liberame(this);

    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            return this.connection.prepareStatement(sql);
        } catch (SQLException e) {
        	log.info("La conexión con la BBDD ha fallado");
        }
        return null;
    }
}
