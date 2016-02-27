package com.github.arsenalfcgunners.gappleperms.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.arsenalfcgunners.gappleperms.GapplePerms;
import com.github.arsenalfcgunners.gappleperms.Rank;
import com.github.arsenalfcgunners.gappleperms.RankManager;

public class RankCMD implements CommandExecutor {
	private GapplePerms gp;
	private String tag;
	private RankManager rm;

	public RankCMD(GapplePerms plugin) {
		gp = plugin;
		tag = gp.getTag();
		rm = gp.getRankManager();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("rank")){
			
			if(sender.hasPermission("gappleperms.changeperm")){
				
				if((args.length == 1 && args[0].equalsIgnoreCase("list"))){						
					String msg = tag+ChatColor.GREEN+"The follwing ranks are available:"+ChatColor.YELLOW;
					
					for(Rank r : rm.getRankList()){
						msg += "\n"+r.getColor()+r.getName();
					}
					
					sender.sendMessage(msg);
				}
				
				else if((args.length == 2 && args[0].equalsIgnoreCase("get"))){
					if(Bukkit.getPlayer(args[1]) != null){
						Player p = Bukkit.getPlayer(args[1]);
						Rank r = rm.getRankOfPlayer(p.getUniqueId());
						sender.sendMessage(tag+ChatColor.GREEN+"The rank of "+ChatColor.YELLOW+args[1]+ChatColor.GREEN+" is "+r.getColor()+r.getName()+ChatColor.GREEN+".");
					}
					else{
						sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: Player not found!");
					}
				}
				
				else if((args.length == 3 && args[0].equalsIgnoreCase("get")) && args[1].equalsIgnoreCase("donorranks")){
					if(Bukkit.getPlayer(args[2]) != null){
						Player p = Bukkit.getPlayer(args[2]);
						ArrayList<Rank> dranks = rm.getDonorRanks(p.getUniqueId());
						if(dranks.size() == 0){
							sender.sendMessage(tag+ChatColor.RED+"That player does not have any donor ranks.");
						}
						else{
							String str = tag+ChatColor.GREEN+"The donor rank(s) of "+ChatColor.YELLOW+args[2]+ChatColor.GREEN+" are:";
							
							for(Rank r : dranks){
								str += "\n"+r.getColor()+r.getName();
							}
							
							sender.sendMessage(str);
						}
					}
					else{
						sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: Player not found!");
					}
				}
				
				else if(args.length == 3 && args[0].equalsIgnoreCase("set")){
					String rankname = args[2];
					String playername = args[1];
					
					if(rm.isRankName(rankname)){
						
						if(sender instanceof Player){
							Player player = (Player) sender;
															
							if(rm.getRank(rankname).getLevel() < rm.getRankList().size()-2){
								
								if(Bukkit.getPlayer(playername) == null || rm.getRankOfPlayer(Bukkit.getPlayer(playername).getUniqueId()).getLevel() < gp.getProfileOfPlayer(player).getRank().getLevel()){
									
									if(Bukkit.getPlayer(playername) == null || rm.getRankOfPlayer(Bukkit.getPlayer(playername).getUniqueId()).getLevel() != rm.getRank(rankname).getLevel()){
									
										if(rm.getRank(rankname).getLevel() < gp.getProfileOfPlayer(player).getRank().getLevel()){
											assignRank(playername, rankname);
											sender.sendMessage(tag+ChatColor.GREEN+"Rank updated successfully.");
										}
										
										else{
											player.sendMessage(tag+ChatColor.YELLOW+"ERROR: You cannot set a rank that is higher or equal to your own rank.");
										}
									}
									
									else{
										player.sendMessage(tag+ChatColor.YELLOW+"ERROR: The new rank must be different than the current one.");
									}
								}
								
								else{
									player.sendMessage(tag+ChatColor.YELLOW+"ERROR: You can only change the rank of a player with a lower rank than yourself.");
								}
							}
							
							else{
								player.sendMessage(tag+ChatColor.YELLOW+"ERROR: That rank must be set from the console for security reasons.");
							}
						}
							
						else{
							if(Bukkit.getOfflinePlayer(playername).getUniqueId() != null){
								if(rm.getRankOfPlayer(Bukkit.getOfflinePlayer(playername).getUniqueId()).getLevel() != rm.getRank(rankname).getLevel()){
									assignRank(playername, rankname);
									sender.sendMessage(tag+ChatColor.GREEN+"Rank updated successfully.");
								}
								else{
									sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The new rank must be different than the old one.");
								}
							}
							
							else{
								sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: Player not found!");
							}
						}
					
					}
					
					else{
						sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: Rank not found!");
					}
				}
				
				else{
					sender.sendMessage(tag+ChatColor.RED+"/rank list, /rank <set> <player> <rank>, /rank get <player>, /rank get donorranks <player>");
				}
				
			}
			
			else{
				sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: You don't have permission.");
			}
			return true;
		}		
		return false;

	}

	@SuppressWarnings("deprecation")
	public void assignRank(String playername, String rankname) {
		if (Bukkit.getPlayer(playername) != null && Bukkit.getPlayer(playername).isOnline()) {
			if(rm.getRankOfPlayer(Bukkit.getPlayer(playername).getUniqueId()).getLevel() < rm.getRank(rankname).getLevel()){
				gp.getProfileOfPlayer(Bukkit.getPlayer(playername)).promote(rm.getRank(rankname));
			}
			else{
				gp.getProfileOfPlayer(Bukkit.getPlayer(playername)).demote(rm.getRank(rankname));
			}
		}

		else {
			if(rm.getRankOfPlayer(Bukkit.getOfflinePlayer(playername).getUniqueId()).getLevel() < rm.getRank(rankname).getLevel()){
				rm.promoteOfflinePlayer(Bukkit.getOfflinePlayer(playername).getUniqueId(), rm.getRank(rankname));
			}
			else{
				rm.demoteOfflinePlayer(Bukkit.getOfflinePlayer(playername).getUniqueId(), rm.getRank(rankname));
			}
		}
	}
}
