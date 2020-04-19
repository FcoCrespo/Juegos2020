package edu.uclm.esi.games2020.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.uclm.esi.games2020.model.*;

public class UserDAO {

    public static void insert(String email, String userName, String pwd, String cuenta) throws Exception {
        try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "insert into user (email, user_name, pwd) values (?, ?, AES_ENCRYPT(?, 'software'))";
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
        }
    }

    public static User identify(String userName, String pwd) throws Exception {
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
                        System.out.println("El nombre del usuario es: " +user.getCuenta());
                        System.out.println("La cuenta del usuario es: " +user.getCuenta());
                        System.out.println("El email del usuario es: " +user.getEmail());
                        return user;
                    } else throw new SQLException();
                }
            }
        }
    }
    
    public static String getEmail(String email) throws Exception {
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
                        System.out.println("El email del usuario que solicita nueva pass es: " +reqEmail);
                        return reqEmail;
                    } else throw new SQLException();
                }
            }
        }
    }
    
    public static void cambiarPass(String email, String pwd) throws Exception {
    	try (WrapperConnection bd = Broker.get().getBd()) {
            String sql = "update user set pwd = AES_ENCRYPT(?, 'software') where email = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, pwd);
                ps.executeUpdate();
            }
            String sql2 = "delete from user_token where email = ?";
            try (PreparedStatement ps = bd.prepareStatement(sql2)) {
                ps.setString(1, email);
                ps.executeUpdate();
            }
        }
    }
}
