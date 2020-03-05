package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.uclm.esi.games2020.model.*;

public class UserDAO {

	public static void insert(String email, String userName, String pwd) throws Exception {
		try(WrapperConnection bd=Broker.get().getBd()) {
			String sql="insert into user (email, user_name, pwd) values (?, ?, ?)";
			try(PreparedStatement ps=bd.prepareStatement(sql)) {
				ps.setString(1, email);
				ps.setString(2, userName);
				ps.setString(3, pwd);
				ps.executeUpdate();
			}
		}
	}

	public static User identify(String userName, String pwd) throws Exception {
		try(WrapperConnection bd=Broker.get().getBd()) {
			String sql="select email from user where user_name=? and pwd=?";
			try(PreparedStatement ps=bd.prepareStatement(sql)) {
				ps.setString(1, userName);
				ps.setString(2, pwd);
				try(ResultSet rs=ps.executeQuery()) {
					if (rs.next()) {
						User user=new User();
						user.setEmail(rs.getString(1));
						user.setUserName(userName);
						return user;
					} else throw new SQLException();
				}
			}
		}
	}
}
