package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WrapperConnection implements AutoCloseable{
	
	private Pool pool;
	private Connection connection;

	public WrapperConnection(Pool pool) {
		this.pool = pool;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url="jdbc:mysql://alarcosj.esi.uclm.es:3306/ajedrez?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false";
			this.connection = DriverManager.getConnection(url, "ideas", "ideas123");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void close() throws Exception {
		this.pool.liberame(this);
		
	}

	public PreparedStatement prepareStatement(String sql) {
		try {
			return this.connection.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
