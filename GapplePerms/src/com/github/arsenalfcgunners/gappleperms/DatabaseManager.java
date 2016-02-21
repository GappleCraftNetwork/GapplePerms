package com.github.arsenalfcgunners.gappleperms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class DatabaseManager {
	private String host;
	private String database;
	private String user;
	private String password;
	private Connection c;
	private Statement s;
		
	public DatabaseManager(String h, String d, String u, String p){
		host = h;
		database = d;
		user = u;
		password = p;
		
		try {
			c = DriverManager.getConnection(host + database + "?autoReconnect=true", user, password);
		}
		
		catch(SQLException ex) {
			ex.printStackTrace();
			stopServer();
		}
	}
	
	public void executeUpdate(String statement){
		try {
			s = c.createStatement();
			s.executeUpdate(statement);
			s.close();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			stopServer();
		}
	}
	
	public ResultSet query(String query) {
        ResultSet rs = null;
        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
        } catch (SQLException e) {
			e.printStackTrace();
			stopServer();
        }
        return rs;
    }
	
	public void stopServer(){
		Bukkit.getLogger().info("Database Failure! Stopping server.");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}
}
