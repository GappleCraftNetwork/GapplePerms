package com.github.arsenalfcgunners.gappleperms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

public class RankManager {
	private static String host;
	private static String database;
	private static String user;
	private static String password;
	private static GapplePerms gp;
	private static Connection c;
	private static ArrayList<Rank> ranks;

	public RankManager(GapplePerms plugin, String h, String d, String u, String p) {
		host = h;
		database = d;
		user = u;
		password = p;
		gp = plugin;
		c = null;
		ranks = new ArrayList<Rank>();

		addRanks();
	}

	private void addRanks() {
		ranks.add(new Rank("Owner", ChatColor.DARK_RED, 9, false));
		ranks.add(new Rank("Dev",ChatColor.BLUE, 8, false));
		ranks.add(new Rank("Admin", ChatColor.DARK_RED, 7, false));
		ranks.add(new Rank("SrMod", ChatColor.RED, 6, false));
		ranks.add(new Rank("Mod", ChatColor.GOLD, 5, false));
		ranks.add(new Rank("VIP", ChatColor.AQUA, 4, true));
		ranks.add(new Rank("Diamond", ChatColor.DARK_AQUA, 3, true));
		ranks.add(new Rank("Emerald", ChatColor.GREEN, 2, true));
		ranks.add(new Rank("Amethyst", ChatColor.LIGHT_PURPLE, 1, true));
		ranks.add(new Rank("Default", ChatColor.YELLOW, 0, false));
	}

	public synchronized static ArrayList<Rank> getRankList() {
		return ranks;
	}

	public static ArrayList<Permission> getPerms(Rank rank) {
		ArrayList<Permission> perms = new ArrayList<Permission>();
		
		for (Rank r : ranks) {

			if (r.getLevel() <= rank.getLevel()) {
				for (String p : getPermissionsFromDB(r)) {
					perms.add(new Permission(p));
				}
			}
		}
		
		return perms;
	}

	public synchronized static int hasPermission(Rank rank, String permission) {		
		for(String p : getPermissionsFromDB(rank)){
			if(p.equals(permission)){
				return 1;
			}
		}
		
		for(Permission p : getPerms(rank)){
			if(p.getName().equals(permission)){
				return 2;
			}
		}
		
		return 3;
	}

	public static void addPermission(Rank rank, String permission) {
		List<String> perms = getPermissionsFromDB(rank);
		perms.add(permission);
		setPermissions(rank, perms);
	}

	public static void delPermission(Rank rank, String permission) {
		List<String> perms = getPermissionsFromDB(rank);
		for(int i = perms.size()-1; i >= 0; i--){
			if(perms.get(i).equals(permission)){
				perms.remove(i);
			}
		}
		setPermissions(rank, perms);
	}

	public synchronized static boolean isRankName(String name) {
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
				
		return false;
	}

	public synchronized static Rank getRank(String name) {
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(name)) {
				return rank;
			}
		}
		
		return ranks.get(ranks.size() - 1);
	}

	public static void promoteOfflinePlayer(UUID uuid, Rank rank) {
		ArrayList<Rank> donorranks = getDonorRanks(uuid);
		if (rank.isDonor() && !donorranks.contains(rank)) {
			donorranks.add(rank);
		}
		
		setRank(uuid, rank);
		setDonorRanks(uuid, donorranks);
	}

	public static void demoteOfflinePlayer(UUID uuid, Rank rank) {
		ArrayList<Rank> donorranks = getDonorRanks(uuid);
		for (int i = donorranks.size()-1; i >= 0; i--) {
			if (donorranks.get(i).getLevel() > rank.getLevel()) {
				donorranks.remove(i);
			}
		}
		
		if(rank.isDonor() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		
		setRank(uuid, rank);
		setDonorRanks(uuid, donorranks);
	}

	public static void setDonorRanks(UUID uuid, ArrayList<Rank> donorranks) {
		String str = "";

		for (Rank rank : donorranks) {
			str += rank.getName() + ",";
		}

		if (str.equals("")) {
			str = "none";
		}

		executeUpdate("UPDATE PlayerRanks SET DonorRanks='" + str + "' WHERE UUID='" + uuid.toString() + "'");

	}

	public synchronized static ArrayList<Rank> getDonorRanks(UUID uuid) {
		ArrayList<Rank> dranks = new ArrayList<Rank>();
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT DonorRanks FROM PlayerRanks WHERE UUID= '" + uuid.toString() + "';";
		
		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();

			String str = "";

			if (rs.next()) {
				str = rs.getString("DonorRanks");
			}

			List<String> stringlist = Arrays.asList(str.split(","));

			for (String r : stringlist) {
				if (!r.equals("none")) {
					dranks.add(getRank(r));
				}
			}

			return dranks;
		} catch (SQLException e) {
			gp.getLogger().log(Level.SEVERE, "QUERY FAILED: " + query);
			e.printStackTrace();
			stopServer();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (c != null) {
				closeConnection();
			}
		}
				
		return dranks;
	}

	public static void setRank(UUID uuid, Rank rank) {
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Rank FROM PlayerRanks WHERE UUID ='" + uuid.toString() + "';";

		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();

			if (rs.next()) {
				executeUpdate(
						"UPDATE PlayerRanks SET Rank='" + rank.getName() + "' WHERE UUID='" + uuid.toString() + "'");
			} else {
				executeUpdate("INSERT INTO PlayerRanks (uuid, rank, donorranks) VALUES ('" + uuid.toString()
						+ "','Default','none')");
			}
		} catch (SQLException e) {
			gp.getLogger().log(Level.SEVERE, "QUERY FAILED: " + query);
			e.printStackTrace();
			stopServer();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (c != null) {
				closeConnection();
			}
		}

	}

	public synchronized static Rank getRankOfPlayer(UUID uuid) {
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Rank FROM PlayerRanks WHERE UUID ='" + uuid.toString() + "';";
		
		Rank rank = ranks.get(ranks.size() - 1);
		
		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();
			if (rs.next()) {
				rank = getRank(rs.getString("Rank"));
			} else {
				executeUpdate("INSERT INTO PlayerRanks (uuid, rank, donorranks) VALUES ('" + uuid.toString()
						+ "','Default','none')");
			}
		} catch (SQLException e) {
			gp.getLogger().log(Level.SEVERE, "QUERY FAILED: " + query);
			e.printStackTrace();
			stopServer();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (c != null) {
				closeConnection();
			}
			
		}
		
		Rank highest = ranks.get(ranks.size()-1);
		
		if(rank.isDonor() && !getDonorRanks(uuid).contains(rank)){			
			for(Rank r : getDonorRanks(uuid)){
				if(r.getLevel() > highest.getLevel()){
					highest = r;
				}
			}
			
			rank = highest;
			setRank(uuid, highest);
		}
		
		return rank;
	}

	public synchronized static List<String> getPermissionsFromDB(Rank rank) {
		List<String> stringlist = new ArrayList<String>();
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Permissions FROM Perms WHERE Rank= '" + rank.getName() + "';";

		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();

			String str = "";

			if (rs.next()) {
				str = rs.getString("Permissions");
			}
			
			stringlist = Arrays.asList(str.split(","));	
			
		} catch (SQLException e) {
			gp.getLogger().log(Level.SEVERE, "QUERY FAILED: " + query);
			e.printStackTrace();
			stopServer();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (c != null) {
				closeConnection();
			}
		}
					
		return stringlist;
	}

	public static void setPermissions(Rank rank, List<String> p) {
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Permissions FROM Perms WHERE Rank='" + rank.getName() + "';";

		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();

			String str = "";

			for (String perm : p) {
				str += perm + ",";
			}

			if (rs.next()) {
				executeUpdate("UPDATE Perms SET Permissions='" + str + "' WHERE Rank='" + rank.getName() + "'");
			} else {
				executeUpdate(
						"INSERT INTO Perms (Rank, Permissions) VALUES ('" + rank.getName() + "','" + str + "')");
			}
		} catch (SQLException e) {
			gp.getLogger().log(Level.SEVERE, "QUERY FAILED: " + query);
			e.printStackTrace();
			stopServer();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (c != null) {
				closeConnection();
			}
		}
	}

	public static void stopServer() {
		Bukkit.getLogger().info("Database Failure! Stopping server.");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}

	public static void openConnection() {
		try {
			c = DriverManager.getConnection(host + database + "?autoReconnect=true", user, password);
		}

		catch (SQLException ex) {
			ex.printStackTrace();
			stopServer();
		}
	}

	public static void closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeUpdate(String statement) {
		PreparedStatement s = null;
		try {
			openConnection();
			s = c.prepareStatement(statement);
			s.execute();
		} catch (SQLException ex) {
			gp.getLogger().log(Level.SEVERE, "UPDATE FAILED: " + statement);
			ex.printStackTrace();
			stopServer();
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (c != null) {
				closeConnection();
			}
		}
	}
}
