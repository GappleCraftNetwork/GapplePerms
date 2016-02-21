package com.github.arsenalfcgunners.gappleperms.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.arsenalfcgunners.gappleperms.GapplePerms;
import com.github.arsenalfcgunners.gappleperms.RankManager;

public class Demote implements CommandExecutor{
	private GapplePerms gp;
	private String tag;
	private RankManager rm;
	
	public Demote(GapplePerms plugin){
		gp = plugin;
		tag = gp.getTag();
		rm = gp.getRankManager();
	}
	
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("demote")){
			
			if(args.length == 2){
				String rankname = args[1];
				String playername = args[0];
				
				if(rm.isRankName(rankname)){
					
					if(sender instanceof Player){
						Player player = (Player) sender;
						
						if(player.hasPermission("gappleperms.changerank")){
									
								if(Bukkit.getPlayer(playername) == null || rm.getRankOfPlayer(Bukkit.getPlayer(playername)).getLevel() < gp.getProfileOfPlayer(player).getRank().getLevel()){
									
									if(Bukkit.getPlayer(playername) == null || rm.getRankOfPlayer(Bukkit.getPlayer(playername)).getLevel() < rm.getRank(rankname).getLevel()){
									
										if(rm.getRank(rankname).getLevel() < gp.getProfileOfPlayer(player).getRank().getLevel()){
											assignRank(playername, rankname);
										}
										
										else{
											player.sendMessage(tag+ChatColor.YELLOW+"ERROR: You cannot demote a rank that is higher or equal to your own rank.");
										}
									}
									
									else{
										player.sendMessage(tag+ChatColor.YELLOW+"ERROR: The new rank must be lower than the old one. Use /promote to promote a rank.");
									}
								}
								
								else{
									player.sendMessage(tag+ChatColor.YELLOW+"ERROR: You can only change the rank of a player with a lower rank than yourself.");
								}
						}
						
						else{
							player.sendMessage(tag+ChatColor.YELLOW+"ERROR: You don't have permission.");
						}
					}
					
					else{
						assignRank(playername, rankname);
					}
				}
				
				else{
					sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: Rank not found!");
				}
			}
			
			else{
				sender.sendMessage(tag+ChatColor.RED+"/demote <player> <rank>");
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void assignRank(String playername, String rankname){
		if(Bukkit.getPlayer(playername) != null && Bukkit.getPlayer(playername).isOnline()){
			gp.getProfileOfPlayer(Bukkit.getPlayer(playername)).demote(rm.getRank(rankname));
		}
		
		else{
			rm.demoteOfflinePlayer(playername, rm.getRank(rankname));
		}
	}
}

