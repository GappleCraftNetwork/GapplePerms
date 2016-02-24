package com.github.arsenalfcgunners.gappleperms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

public class RankManager {
	private ArrayList<Rank> ranks;
	private GapplePerms gp;
	
	public RankManager(GapplePerms plugin){
		ranks = new ArrayList<Rank>();
		addRanks();
		gp = plugin;
	}
	
	public void addRanks(){
		ranks.add(new Rank("Owner", ChatColor.BOLD+""+ChatColor.DARK_RED, 10, false));
		ranks.add(new Rank("Dev", ChatColor.BOLD+""+ChatColor.DARK_PURPLE, 9, false));
		ranks.add(new Rank("Admin", ChatColor.BOLD+""+ChatColor.DARK_RED, 8, false));
		ranks.add(new Rank("SrMod", ChatColor.DARK_RED+"", 7, false));
		ranks.add(new Rank("Mod", ChatColor.RED+"", 6, false));
		ranks.add(new Rank("VIP", ChatColor.GOLD+"", 5, true));
		ranks.add(new Rank("Builder", ChatColor.BLUE+"", 4, true));
		ranks.add(new Rank("Diamond", ChatColor.DARK_AQUA+"", 3, true));
		ranks.add(new Rank("Emerald", ChatColor.GREEN+"", 2, true));
		ranks.add(new Rank("Amethyst", ChatColor.LIGHT_PURPLE+"", 1, true));
		ranks.add(new Rank("Default", ChatColor.YELLOW+"", 0, false));
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
		ResultSet rs = gp.getDatabaseManager().query("SELECT Permissions FROM Permissions WHERE Rank ='"+rank.getName()+"';");
		String str = "";
		ArrayList<Permission> perms = new ArrayList<Permission>();
		
		try {
			if(rs.next()){
				str = rs.getString("Permissions");
			}
			rs.close();
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
		
		ResultSet rs = gp.getDatabaseManager().query("SELECT Permissions FROM Permissions WHERE Rank ='"+rank.getName()+"';");
		
		try {
			if(rs.next()){
				gp.getDatabaseManager().executeUpdate("UPDATE Permissions SET Permissions='"+str+"' WHERE Rank='"+rank.getName()+"'");
			}
			else{
				gp.getDatabaseManager().executeUpdate("INSERT INTO Permissions (Rank, Permissions) VALUES ('" + rank.getName() + "','"+str+"')");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		return ranks.get(ranks.size()-1);
	}
	
	public void setRank(UUID uuid, Rank rank){
		ResultSet rs = gp.getDatabaseManager().query("SELECT Rank FROM PlayerRanks WHERE UUID ='"+uuid.toString()+"';");

		try {
			if(rs.next()){
				gp.getDatabaseManager().executeUpdate("UPDATE PlayerRanks SET Rank='"+rank.getName()+"' WHERE UUID='"+uuid.toString()+"'");
			}
			else{
				gp.getDatabaseManager().executeUpdate("INSERT INTO PlayerRanks (uuid, rank, donorranks) VALUES ('" + uuid.toString() + "','Default','none')");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public Rank getRankOfPlayer(UUID uuid){
		ResultSet rs = gp.getDatabaseManager().query("SELECT Rank FROM PlayerRanks WHERE UUID ='"+uuid.toString()+"';");
		
		try {
			if(rs.next()){
				return gp.getRankManager().getRank(rs.getString("Rank"));
			}
			else{
				gp.getDatabaseManager().executeUpdate("INSERT INTO PlayerRanks (uuid, rank, donorranks) VALUES ('" + uuid.toString() + "','Default','none')");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ranks.get(ranks.size()-1);
	}
	
	public void promoteOfflinePlayer(UUID uuid, Rank rank){
		ArrayList<Rank> donorranks = getDonorRanks(uuid);
		if(rank.isDonor() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		setRank(uuid, rank);
	}
	
	public void demoteOfflinePlayer(UUID uuid, Rank rank){
		ArrayList<Rank> donorranks = getDonorRanks(uuid);
		for(int i = 0; i < donorranks.size(); i++){
			if(donorranks.get(i).getLevel() >= rank.getLevel()){
				donorranks.remove(i);
			}
		}
		setRank(uuid, rank);
	}
	
	public ArrayList<Rank> getDonorRanks(UUID uuid){
		ResultSet rs = gp.getDatabaseManager().query("SELECT DonorRanks FROM PlayerRanks WHERE UUID= '"+uuid.toString()+"';");
		String str = "";
		ArrayList<Rank> dranks = new ArrayList<Rank>();
		
		try {
			if(rs.next()){
				str = rs.getString("DonorRanks");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		List<String> stringlist= Arrays.asList(str.split(","));
		
		for(String r : stringlist){
			if(!r.equals("none")){
				dranks.add(getRank(r));
			}
		}
		
		return dranks;
	}

	public void setDonorRanks(UUID uuid, ArrayList<Rank> donorranks) {
		String str = "";
		
		for(Rank rank : donorranks){
			str += rank.getName()+",";
		}
		
		if(str.equals("")){
			str = "none";
		}
		
		gp.getDatabaseManager().executeUpdate("UPDATE PlayerRanks SET DonorRanks='"+str+"' WHERE UUID='"+uuid.toString()+"'");
		
	}	
}
