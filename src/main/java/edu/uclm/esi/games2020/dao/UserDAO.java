package edu.uclm.esi.games2020.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.esi.games2020.model.*;

public class UserDAO {
	private static final Logger log = Logger.getLogger(UserDAO.class.getName());
	
	private  UserDAO(){
		
	}

    public static void insert(String email, String userName, String pwd, String cuenta){
        try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "insert into user (email, user_name, pwd, wins) values (?, ?, AES_ENCRYPT(?, 'software'), 0)";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, userName);
                ps.setString(3, pwd);
                ps.executeUpdate();
            }
            String sql2 = "insert into user_cuenta (user_name, cuenta) values (?,  AES_ENCRYPT(?, 'software'))";
            try (PreparedStatement ps = bd.prepareStatement(sql2)) {
                ps.setString(1, userName);
                ps.setString(2, cuenta);
                ps.executeUpdate();
            }
        } catch (Exception e) {
			log.info("\nError al insertar usuario");
		}
    }

    public static User identify(String userName, String pwd) {
        try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "SELECT u.user_name, u.email, CAST(AES_DECRYPT(c.cuenta, 'software') AS CHAR(100)) user_cuenta " + 
            		"FROM user u, user_cuenta c " + 
            		"WHERE u.user_name = ? AND u.user_name = c.user_name AND u.pwd = AES_ENCRYPT(?, 'software')";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setString(1, userName);
                ps.setString(2, pwd);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setUserName(rs.getString(1));
                        user.setEmail(rs.getString(2));
                        user.setCuenta(rs.getString(3));
                       
                        return user;
                    } else throw new SQLException();
                }
            }
        } catch (Exception e) {
        	log.info("\nError al identificar usuario");
			return null;
		}
    }
    
    public static String getEmail(String email){
        try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "SELECT email " +
                    "FROM user u " +
                    "WHERE email = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String reqEmail;
                        reqEmail = rs.getString(1);
                        return reqEmail;
                    }
                }

            }
        } catch (Exception e1) {
            log.info("\nError al obtener email");
            return null;
        }
        return null;

    }
    
	public static JSONArray getRankedUsers() {
		JSONArray jsa = new JSONArray();
        try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "SELECT user_name, wins " +
                    "FROM user";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                	String username;
                    int wins;                    
                    while (rs.next()) {
                        username = rs.getString(1);
                        wins = rs.getInt(2);
                        jsa.put(username);
                        jsa.put(Integer.toString(wins));
                    }
                    return jsa;                    
                }

            }
        } catch (Exception e1) {
            log.info("\nError al obtener email");
            return null;
        }
	}
	
    public static void updateWins(String username, int wins)  {
    	try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "update user set wins = ? where user_name = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setInt(1, wins);
                ps.setString(2, username);
                ps.executeUpdate();
            }

        } catch (Exception e) {
        	log.info("\n"+e.getMessage());
        	log.info("\nError al actualizar victorias");
		}
    }
    
    public static void cambiarPass(String email, String pwd)  {
    	try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "update user set pwd = AES_ENCRYPT(?, 'software') where email = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setString(1, pwd);
                ps.setString(2, email);
                ps.executeUpdate();
            }
            String sql2 = "delete from user_token where email = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql2)) {
                ps.setString(1, email);
                ps.executeUpdate();
            }
        } catch (Exception e) {
        	log.info("\nError al cambiar pass");
		}
    }
}
