package com.github.arsenalfcgunners.gappleperms;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerProfile {
	private UUID uuid;
	private Rank rank;
	private ArrayList <Rank> donorranks; 
	PermissionAttachment pa;
	private Player player;
	
	public PlayerProfile(UUID u, Rank r, GapplePerms plugin){
		uuid = u;
		rank = r;
		player = Bukkit.getPlayer(uuid);
		pa = player.addAttachment(plugin);
		rank = RankManager.getRankOfPlayer(uuid);
		donorranks = RankManager.getDonorRanks(uuid);
		givePerms();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Rank getRank(){
		return rank;
	}
	
	public void givePerms(){
		for(Permission p : RankManager.getPerms(rank)){
			pa.setPermission(p.getName(), true);
		}
		player.recalculatePermissions();
	}
	
	public ArrayList<Rank> getDonorRanks(){
		return donorranks;
	}
	
	public void clearPerms(){
		for(PermissionAttachmentInfo p: player.getEffectivePermissions()){
			pa.unsetPermission(p.getPermission());
		}
		player.recalculatePermissions();
	}
	
	public void promote(Rank r){
		rank = r;
		if(rank.isDonor() && !donorranks.contains(rank)){
			donorranks.add(rank);
		}
		RankManager.setDonorRanks(uuid, donorranks);
		RankManager.setRank(uuid, rank);
		clearPerms();
		givePerms();
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
		
		RankManager.setDonorRanks(uuid, donorranks);
		RankManager.setRank(uuid, rank);
		clearPerms();
		givePerms();
	}
	
	public void refresh(){
		rank = RankManager.getRankOfPlayer(uuid);
		donorranks = RankManager.getDonorRanks(uuid);
		clearPerms();
		givePerms();
	}
}
