package com.github.arsenalfcgunners.gappleperms;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerProfile {
	private UUID uuid;
	private Rank rank;
	private ArrayList <Rank> donorranks; 
	private GapplePerms gp;
	private PermissionAttachment pa;
	private ArrayList<Permission> perms;
	
	public PlayerProfile(UUID u, Rank r, GapplePerms plugin){
		uuid = u;
		rank = r;
		gp = plugin;
		pa = null;
		perms = new ArrayList<Permission>();
		rank = gp.getRankManager().getRankOfPlayer(uuid);
		donorranks = gp.getRankManager().getDonorRanks(uuid);
		givePerms();
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}
	
	public Rank getRank(){
		return rank;
	}
	
	public void givePerms(){
		pa = Bukkit.getPlayer(uuid).addAttachment(gp);
		for(Permission p : gp.getRankManager().getPerms(rank.getName())){
			pa.setPermission(p.toString(), true);
			perms.add(p);
		}
	}
	
	public ArrayList<Rank> getDonorRanks(){
		return donorranks;
	}
	
	public void clearPerms(){
		for(int i = perms.size() - 1; i >= 0; i--){
			pa.unsetPermission(perms.get(i).toString());			
		}
		Bukkit.getPlayer(uuid).removeAttachment(pa);
	}
	
	public void promote(Rank r){
		rank = r;
		if(rank.isDonor() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		gp.getRankManager().setDonorRanks(uuid, donorranks);
		gp.getRankManager().setRank(uuid, rank);
		clearPerms();
		givePerms();
		gp.getLogger().log(Level.WARNING, "Online player promoted.");
	}
	
	public void demote(Rank r){
		rank = r;
		
		for (int i = donorranks.size()-1; i >= 0; i--) {
			if (donorranks.get(i).getLevel() > rank.getLevel()) {
				donorranks.remove(i);
			}
		}
		
		if(rank.isDonor() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		
		gp.getRankManager().setDonorRanks(uuid, donorranks);
		gp.getRankManager().setRank(uuid, rank);
		clearPerms();
		givePerms();
		gp.getLogger().log(Level.WARNING, "Online player demoted.");
	}
	
	public void refresh(){
		rank = gp.getRankManager().getRankOfPlayer(uuid);
		donorranks = gp.getRankManager().getDonorRanks(uuid);
		clearPerms();
		givePerms();
	}
}
