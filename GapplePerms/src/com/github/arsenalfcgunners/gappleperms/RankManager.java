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
	private String host;
	private String database;
	private String user;
	private String password;
	private GapplePerms gp;
	private Connection c;
	private ArrayList<Rank> ranks;

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

	public void addRanks() {
		ranks.add(new Rank("Owner", ChatColor.BOLD + "" + ChatColor.DARK_RED, 10, false));
		ranks.add(new Rank("Dev", ChatColor.BOLD + "" + ChatColor.DARK_PURPLE, 9, false));
		ranks.add(new Rank("Admin", ChatColor.BOLD + "" + ChatColor.DARK_RED, 8, false));
		ranks.add(new Rank("SrMod", ChatColor.DARK_RED + "", 7, false));
		ranks.add(new Rank("Mod", ChatColor.RED + "", 6, false));
		ranks.add(new Rank("VIP", ChatColor.GOLD + "", 5, true));
		ranks.add(new Rank("Builder", ChatColor.BLUE + "", 4, true));
		ranks.add(new Rank("Diamond", ChatColor.DARK_AQUA + "", 3, true));
		ranks.add(new Rank("Emerald", ChatColor.GREEN + "", 2, true));
		ranks.add(new Rank("Amethyst", ChatColor.LIGHT_PURPLE + "", 1, true));
		ranks.add(new Rank("Default", ChatColor.YELLOW + "", 0, false));
	}

	public ArrayList<Rank> getRankList() {
		return ranks;
	}

	public ArrayList<Permission> getPerms(String rankname) {
		ArrayList<Permission> perms = new ArrayList<Permission>();
		boolean found = false;

		for (int i = 0; i < ranks.size(); i++) {

			if (ranks.get(i).getName().equals(rankname) || found) {
				found = true;

				for (Permission p : getPermissions(ranks.get(i))) {
					perms.add(p);
				}
			}
		}
		return perms;
	}

	public boolean hasPermission(String rankname, String permission) {
		if (getPerms(rankname).contains(new Permission(permission))) {
			return true;

		}
		return false;
	}

	public void addPermission(Rank rank, String permission) {
		ArrayList<Permission> perms = getPermissions(rank);
		perms.add(new Permission(permission));
		setPermissions(rank, perms);
	}

	public void delPermission(Rank rank, String permission) {
		ArrayList<Permission> perms = getPermissions(rank);
		perms.remove(new Permission(permission));
		setPermissions(rank, perms);
	}

	public boolean isRankName(String name) {
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public Rank getRank(String name) {
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(name)) {
				return rank;
			}
		}
		return ranks.get(ranks.size() - 1);
	}

	public void promoteOfflinePlayer(UUID uuid, Rank rank) {
		ArrayList<Rank> donorranks = getDonorRanks(uuid);
		if (rank.isDonor() && !donorranks.contains(rank)) {
			donorranks.add(rank);
		}
		setRank(uuid, rank);
	}

	public void demoteOfflinePlayer(UUID uuid, Rank rank) {
		ArrayList<Rank> donorranks = getDonorRanks(uuid);
		for (int i = 0; i < donorranks.size(); i++) {
			if (donorranks.get(i).getLevel() >= rank.getLevel()) {
				donorranks.remove(i);
			}
		}
		setRank(uuid, rank);
	}

	public void setDonorRanks(UUID uuid, ArrayList<Rank> donorranks) {
		String str = "";

		for (Rank rank : donorranks) {
			str += rank.getName() + ",";
		}

		if (str.equals("")) {
			str = "none";
		}

		executeUpdate("UPDATE PlayerRanks SET DonorRanks='" + str + "' WHERE UUID='" + uuid.toString() + "'");

	}

	public void executeUpdate(String statement) {
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

	public ArrayList<Rank> getDonorRanks(UUID uuid) {
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

	public void setRank(UUID uuid, Rank rank) {
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

	public Rank getRankOfPlayer(UUID uuid) {
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Rank FROM PlayerRanks WHERE UUID ='" + uuid.toString() + "';";

		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();
			if (rs.next()) {
				return getRank(rs.getString("Rank"));
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

		return ranks.get(ranks.size() - 1);
	}

	public ArrayList<Permission> getPermissions(Rank rank) {
		ArrayList<Permission> perms = new ArrayList<Permission>();
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Permissions FROM Permissions WHERE Rank ='" + rank.getName() + "';";

		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();

			String str = "";

			if (rs.next()) {
				str = rs.getString("Permissions");
			}

			while (str.indexOf(",") > -1) {
				perms.add(new Permission((str.substring(str.lastIndexOf(",")))));
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

		return perms;
	}

	public void setPermissions(Rank rank, ArrayList<Permission> perms) {
		PreparedStatement s = null;
		ResultSet rs = null;
		String query = "SELECT Permissions FROM Permissions WHERE Rank ='" + rank.getName() + "';";

		try {
			openConnection();
			s = c.prepareStatement(query);
			rs = s.executeQuery();

			String str = "";

			for (Permission p : perms) {
				str += p.getName() + ",";
			}

			if (rs.next()) {
				executeUpdate("UPDATE Permissions SET Permissions='" + str + "' WHERE Rank='" + rank.getName() + "'");
			} else {
				executeUpdate(
						"INSERT INTO Permissions (Rank, Permissions) VALUES ('" + rank.getName() + "','" + str + "')");
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

	public void stopServer() {
		Bukkit.getLogger().info("Database Failure! Stopping server.");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}

	public void openConnection() {
		try {
			c = DriverManager.getConnection(host + database + "?autoReconnect=true", user, password);
		}

		catch (SQLException ex) {
			ex.printStackTrace();
			stopServer();
		}
	}

	public void closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
