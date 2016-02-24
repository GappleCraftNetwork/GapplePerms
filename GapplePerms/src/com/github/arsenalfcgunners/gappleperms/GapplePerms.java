package com.github.arsenalfcgunners.gappleperms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.arsenalfcgunners.gappleperms.commands.Demote;
import com.github.arsenalfcgunners.gappleperms.commands.Permissions;
import com.github.arsenalfcgunners.gappleperms.commands.Promote;
import com.github.arsenalfcgunners.gappleperms.commands.RankCMD;
import com.github.arsenalfcgunners.gappleperms.commands.ResetDB;

public class GapplePerms extends JavaPlugin{
	private RankManager rm;
	private ArrayList<PlayerProfile> pp;
	private String tag;
	private Timer timer;
	
	@Override
	public void onEnable(){
		pp = new ArrayList<PlayerProfile>();
		tag = ChatColor.GRAY+"["+ChatColor.GOLD+"GapplePerms"+ChatColor.GRAY+"] ";
		
		//Listeners
		new PlayerListener(this);
		
		//Rank Manager
		rm = new RankManager(this, "jdbc:mysql://167.114.208.215:3306/", "ihdb_175", "ihdb_175", "22fdf2acbd");
		
		//Command Executers
		getCommand("promote").setExecutor(new Promote(this));
		getCommand("demote").setExecutor(new Demote(this));
		getCommand("permission").setExecutor(new Permissions(this));
		getCommand("rank").setExecutor(new RankCMD(this));
		getCommand("resetdb").setExecutor(new ResetDB(this));
		
		for(Player player : Bukkit.getOnlinePlayers()){
			addPlayerProfile(player);
		}
		
		timer = new Timer();
		timer.schedule(new RefreshTimer(this), 0, 1000);
	}
	
	@Override
	public void onDisable(){
		timer.cancel();
	}
	
	public RankManager getRankManager(){
		return rm;
	}
	
	public void removeDonor(Rank r, Player p){
		PlayerProfile profile= getProfileOfPlayer(p);
		ArrayList<Rank> donorranks = profile.getDonorRanks();
		donorranks.remove(donorranks.indexOf(r));
		if(donorranks.isEmpty()){
			profile.demote(rm.getRank("default"));
		}
		else{
			Rank highest = rm.getRank("default");
			for(Rank rank : donorranks){
				if(rank.getLevel() > highest.getLevel()){
					highest = rank;
				}
			}
			profile.demote(highest);
		}
		rm.setDonorRanks(p.getUniqueId(), donorranks);
	}
	
	public PlayerProfile getProfileOfPlayer(Player p){
		for(PlayerProfile profile : pp){
			if(profile.getPlayer().equals(p)){
				return profile;
			}
		}
		return null;
	}
	
	public void addPlayerProfile(Player p){
		PlayerProfile profile = new PlayerProfile(p.getUniqueId(), rm.getRankOfPlayer(p.getUniqueId()), this);
		profile.givePerms();
		pp.add(profile);
	}
	
	public void addPlayerProfile(PlayerProfile profile){
		profile.givePerms();
		pp.add(profile);
	}
	
	public void removePlayerProfile(Player p){
		Iterator<PlayerProfile> ipp = pp.iterator();
		while(ipp.hasNext()){
			PlayerProfile profile = ipp.next();
			if(profile.getPlayer().equals(p)){
				profile.clearPerms();
				ipp.remove();
			}
		}
	}
	
	public ArrayList<PlayerProfile> getPlayerProfiles(){
		return pp;
	}
	
	public String getTag(){
		return tag;
	}
}
