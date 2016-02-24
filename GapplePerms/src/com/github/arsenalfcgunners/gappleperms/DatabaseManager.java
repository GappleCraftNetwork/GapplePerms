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
	private GapplePerms gp;
	private Connection c;
		
	public DatabaseManager(GapplePerms plugin, String h, String d, String u, String p){
		host = h;
		database = d;
		user = u;
		password = p;
		gp = plugin;
		c = null;
	}
	
	public void executeUpdate(String statement){
		PreparedStatement s = null;
		try {
			s = c.prepareStatement(statement);
			s.execute();
			s.close();
		} catch(SQLException ex) {
        	gp.getLogger().log(Level.SEVERE, "UPDATE FAILED: "+statement);
			ex.printStackTrace();
			stopServer();
		} finally {
        	if(s != null){
        		try { s.close(); } catch (SQLException e) { e.printStackTrace(); }
        	}
        	if(c != null){
        		closeConnection();
        	}
		}
	}
	
	public ResultSet query(String query) {
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
        	openConnection();
            s = c.prepareStatement(query);
            rs = s.executeQuery();
            return rs;
        } catch (SQLException e) {
        	gp.getLogger().log(Level.SEVERE, "QUERY FAILED: "+query);
			e.printStackTrace();
			stopServer();
        } finally {
        	if(rs != null){
        		try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        	}
        	if(s != null){
        		try { s.close(); } catch (SQLException e) { e.printStackTrace(); }
        	}
        	if(c != null){
        		closeConnection();
        	}
        }
        return rs;
    }
	
	public void stopServer(){
		Bukkit.getLogger().info("Database Failure! Stopping server.");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}
	
	public void openConnection(){
		try {
			c = DriverManager.getConnection(host + database + "?autoReconnect=true", user, password);
		}
		
		catch(SQLException ex) {
			ex.printStackTrace();
			stopServer();
		}
	}
	
	public void closeConnection(){
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
