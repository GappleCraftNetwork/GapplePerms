package com.github.arsenalfcgunners.gappleperms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

@SuppressWarnings("deprecation")
public class RankManager {
	private ArrayList<Rank> ranks;
	private GapplePerms gp;
	
	public RankManager(GapplePerms plugin){
		ranks = new ArrayList<Rank>();
		addRanks();
		gp = plugin;
	}
	
	public void addRanks(){
		ranks.add(new Rank("Owner", ChatColor.DARK_RED, 10));
		ranks.add(new Rank("Dev", ChatColor.GRAY, 9));
		ranks.add(new Rank("Admin", ChatColor.DARK_RED, 8));
		ranks.add(new Rank("SrMod", ChatColor.RED, 7));
		ranks.add(new Rank("Mod", ChatColor.LIGHT_PURPLE, 6));
		ranks.add(new Rank("VIP", ChatColor.GOLD, 5));
		ranks.add(new Rank("Builder", ChatColor.BLUE, 4));
		ranks.add(new Rank("Diamond", ChatColor.DARK_AQUA, 3));
		ranks.add(new Rank("Emerald", ChatColor.GREEN, 2));
		ranks.add(new Rank("Amethyst", ChatColor.DARK_PURPLE, 1));
		ranks.add(new Rank("Default", ChatColor.YELLOW, 0));
	}
	
	public ArrayList<Rank> getRankList(){
		return ranks;
	}
	
	public ArrayList<Permission> getPerms(String rankname){
		ArrayList<Permission> perms = new ArrayList<Permission>();
		boolean found = false;
		
		for(int i = 0; i < ranks.size(); i++){
			
			if(ranks.get(i).getName().equals(rankname) || found){
				found = true;
				
				for(Permission p : gp.getRankManager().getPermissions(ranks.get(i))){
					perms.add(p);
				}
			}
		}
		return perms;
	}
	
	public boolean hasPermission(String rankname, String permission){
		if(getPerms(rankname).contains(new Permission(permission))){
			return true;
		
		}
		return false;
	}
	
	public void addPermission(Rank rank, String permission){
		ArrayList<Permission> perms = getPermissions(rank);
		perms.add(new Permission(permission));
		setPermissions(rank, perms);
	}
	
	public void delPermission(Rank rank, String permission){
		ArrayList<Permission> perms = getPermissions(rank);
		perms.remove(new Permission(permission));
		setPermissions(rank, perms);
	}
	
	public ArrayList<Permission> getPermissions(Rank rank){
		ResultSet rs = gp.getDatabaseManager().query("SELECT "+rank.getName()+" FROM Permissions");
		String str = "";
		ArrayList<Permission> perms = new ArrayList<Permission>();
		
		try {
			str = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		while(str.indexOf(",") > -1){
			perms.add(new Permission((str.substring(str.lastIndexOf(",")))));
		}
		
		return perms;
	}
	
	public void setPermissions(Rank rank, ArrayList<Permission> perms){
		String str = "";
		
		for(Permission p : perms){
			str += p.getName()+",";
		}
		
		gp.getDatabaseManager().executeUpdate("INSERT INTO Permissions(+"+rank.getName()+") VALUES (" + str + ")");
	}
	
	public boolean isRankName(String name){
		for(Rank rank : ranks){
			if(rank.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public Rank getRank(String name){
		for(Rank rank : ranks){
			if(rank.getName().equalsIgnoreCase(name)){
				return rank;
			}
		}
		return ranks.get(0);
	}
	
	public void setRank(String playername, Rank rank){
		Player player = Bukkit.getPlayer(playername);
		gp.getDatabaseManager().executeUpdate("INSERT INTO PlayerRanks(+"+player.getUniqueId()+") VALUES (" + rank.getName() + ")");
	}
	
	public Rank getRankOfPlayer(Player player){
		ResultSet rs = gp.getDatabaseManager().query("SELECT "+player.getUniqueId()+" FROM PlayerRanks");
		
		try {
			return gp.getRankManager().getRank(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void promoteOfflinePlayer(String playername, Rank rank){
		ArrayList<Rank> donorranks = getDonorRanks(Bukkit.getOfflinePlayer(playername).getUniqueId());
		if(rank.getLevel() >= gp.getDonorLevel() && rank.getLevel() < gp.getStaffLevel() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		setRank(playername, rank);
	}
	
	public void demoteOfflinePlayer(String playername, Rank rank){
		ArrayList<Rank> donorranks = getDonorRanks(Bukkit.getOfflinePlayer(playername).getUniqueId());
		for(int i = 0; i < donorranks.size(); i++){
			if(donorranks.get(i).getLevel() >= rank.getLevel()){
				donorranks.remove(i);
			}
		}
		setRank(playername, rank);
	}
	
	public ArrayList<Rank> getDonorRanks(UUID uuid){
		ResultSet rs = gp.getDatabaseManager().query("SELECT "+uuid.toString()+" FROM DonorRanks");
		String str = "";
		ArrayList<Rank> dranks = new ArrayList<Rank>();
		
		try {
			str = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		while(str.indexOf(",") > -1){
			dranks.add(getRank(str.substring(str.lastIndexOf(","))));
		}
		
		return dranks;
	}

	public void setDonorRanks(Player player, ArrayList<Rank> donorranks) {
		String str = "";
		
		for(Rank rank : donorranks){
			str += rank.getName()+",";
		}
		
		gp.getDatabaseManager().executeUpdate("INSERT INTO PlayerRanks(+"+player.getUniqueId()+") VALUES (" + str + ")");
	}	
}
