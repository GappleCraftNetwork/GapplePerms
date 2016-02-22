package com.github.arsenalfcgunners.gappleperms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class DatabaseManager {
	private String host;
	private String database;
	private String user;
	private String password;
	private Connection c;
	private GapplePerms gp;
		
	public DatabaseManager(GapplePerms plugin, String h, String d, String u, String p){
		host = h;
		database = d;
		user = u;
		password = p;
		gp = plugin;
		
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
			PreparedStatement s = c.prepareStatement(statement);
			s.execute();
			s.close();
		}
		catch(SQLException ex) {
        	gp.getLogger().log(Level.SEVERE, "UPDATE FAILED: "+statement);
			ex.printStackTrace();
			stopServer();
		}
	}
	
	public ResultSet query(String query) {
        ResultSet rs = null;
        try {
            PreparedStatement s = c.prepareStatement(query);
            rs = s.executeQuery();
        } catch (SQLException e) {
        	gp.getLogger().log(Level.SEVERE, "QUERY FAILED: "+query);
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
