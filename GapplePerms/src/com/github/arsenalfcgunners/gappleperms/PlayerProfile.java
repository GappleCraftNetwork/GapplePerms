package com.github.arsenalfcgunners.gappleperms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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
	private HashMap<UUID, PermissionAttachment> attachments;
	
	public PlayerProfile(UUID u, Rank r, GapplePerms plugin){
		uuid = u;
		rank = r;
		gp = plugin;
		attachments = new HashMap<UUID, PermissionAttachment>();
		refresh();
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}
	
	public Rank getRank(){
		return rank;
	}
	
	public void givePerms(){
		for(Permission p : gp.getRankManager().getPermissions(rank)){
			PermissionAttachment a = Bukkit.getPlayer(uuid).addAttachment(gp);
			a.setPermission(p, true);
			attachments.put(uuid, a);
		}
	}
	
	public ArrayList<Rank> getDonorRanks(){
		return donorranks;
	}
	
	public void addDonorRank(Rank rank){
		if(!donorranks.contains(rank)){
			donorranks.add(rank);
		}
		gp.getRankManager().setDonorRanks(uuid, donorranks);
	}
	
	public void delDonorRank(Rank rank){
		if(donorranks.contains(rank)){
			donorranks.remove(rank);
		}
		gp.getRankManager().setDonorRanks(uuid, donorranks);
	}
	
	public void clearPerms(){
		Iterator<Entry<UUID, PermissionAttachment>> it = attachments.entrySet().iterator();
		while(it.hasNext()) {
			Entry<UUID, PermissionAttachment> a = it.next();
			Bukkit.getPlayer(uuid).removeAttachment(a.getValue());
		}
		attachments.clear();
	}
	
	public void promote(Rank r){
		rank = r;
		if(rank.isDonor() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		clearPerms();
		givePerms();
		gp.getRankManager().setDonorRanks(uuid, donorranks);
		gp.getRankManager().setRank(uuid, rank);
	}
	
	public void demote(Rank r){
		rank = r;
		clearPerms();
		givePerms();
		gp.getRankManager().setDonorRanks(uuid, donorranks);
		gp.getRankManager().setRank(uuid, rank);
	}
	
	public void refresh(){
		rank = gp.getRankManager().getRankOfPlayer(uuid);
		donorranks = gp.getRankManager().getDonorRanks(uuid);
		clearPerms();
		givePerms();
	}
}
