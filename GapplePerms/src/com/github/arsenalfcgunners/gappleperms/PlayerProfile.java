package com.github.arsenalfcgunners.gappleperms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerProfile {
	private Player player;
	private Rank rank;
	private ArrayList <Rank> donorranks; 
	private GapplePerms gp;
	private HashMap<UUID, PermissionAttachment> attachments;
	
	public PlayerProfile(Player p, Rank r, GapplePerms plugin){
		player = p;
		rank = r;
		gp = plugin;
		attachments = new HashMap<UUID, PermissionAttachment>();
		refresh();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Rank getRank(){
		return rank;
	}
	
	public void givePerms(){
		for(Permission p : gp.getRankManager().getPermissions(rank)){
			PermissionAttachment a = player.addAttachment(gp);
			a.setPermission(p, true);
			attachments.put(player.getUniqueId(), a);
		}
	}
	
	public ArrayList<Rank> getDonorRanks(){
		return donorranks;
	}
	
	public void addDonorRank(Rank rank){
		if(!donorranks.contains(rank)){
			donorranks.add(rank);
		}
		gp.getRankManager().setDonorRanks(player, donorranks);
	}
	
	public void delDonorRank(Rank rank){
		if(donorranks.contains(rank)){
			donorranks.remove(rank);
		}
		gp.getRankManager().setDonorRanks(player, donorranks);
	}
	
	public void clearPerms(){
		Iterator<Entry<UUID, PermissionAttachment>> it = attachments.entrySet().iterator();
		while(it.hasNext()) {
			Entry<UUID, PermissionAttachment> a = it.next();
			player.removeAttachment(a.getValue());
		}
		attachments.clear();
	}
	
	public void promote(Rank r){
		rank = r;
		if(rank.getLevel() >= gp.getDonorLevel() && rank.getLevel() < gp.getStaffLevel() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		clearPerms();
		givePerms();
		gp.getRankManager().setDonorRanks(player, donorranks);
		gp.getRankManager().setRank(player.getName(), rank);
	}
	
	public void demote(Rank r){
		rank = r;
		clearPerms();
		givePerms();
		gp.getRankManager().setDonorRanks(player, donorranks);
		gp.getRankManager().setRank(player.getName(), rank);
	}
	
	public void refresh(){
		rank = gp.getRankManager().getRankOfPlayer(player);
		donorranks = gp.getRankManager().getDonorRanks(player.getUniqueId());
		clearPerms();
		givePerms();
	}
}
