package com.github.arsenalfcgunners.gappleperms.commands;

import java.util.ArrayList;
import java.util.UUID;

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

	public RankCMD(GapplePerms plugin) {
		gp = plugin;
		tag = gp.getTag();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("rank")){
			
			if(sender.hasPermission("gappleperms.admin")){
				
				if((args.length == 1 && args[0].equalsIgnoreCase("list"))){						
					String msg = tag+ChatColor.GREEN+"The follwing ranks are available:"+ChatColor.YELLOW;
					
					for(Rank r : RankManager.getRankList()){
						msg += "\n"+r.getColor()+r.getName();
					}
					
					sender.sendMessage(msg);
				}
				
				else if((args.length == 2 && args[0].equalsIgnoreCase("get"))){
					if(Bukkit.getPlayer(args[1]) != null){
						Player p = Bukkit.getPlayer(args[1]);
						Rank r = RankManager.getRankOfPlayer(p.getUniqueId());
						sender.sendMessage(tag+ChatColor.GREEN+"The rank of "+ChatColor.YELLOW+args[1]+ChatColor.GREEN+" is "+r.getColor()+r.getName()+ChatColor.GREEN+".");
					}
					else{
						UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
						if(uuid != null){
							Rank r = RankManager.getRankOfPlayer(uuid);
							sender.sendMessage(tag+ChatColor.GREEN+"The rank of "+ChatColor.YELLOW+args[1]+ChatColor.GREEN+" is "+r.getColor()+r.getName()+ChatColor.GREEN+".");
						}
						else{
							sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The player is not online, and the name could not be found in the Mojang database. If you believe this was an error, it is likely because Mojang's servers are down.");
						}
					}
				}
				
				else if((args.length == 3 && args[0].equalsIgnoreCase("get")) && args[1].equalsIgnoreCase("donorranks")){
					if(Bukkit.getPlayer(args[2]) != null){
						Player p = Bukkit.getPlayer(args[2]);
						ArrayList<Rank> dranks = RankManager.getDonorRanks(p.getUniqueId());
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
						UUID uuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
						if(uuid != null){
							ArrayList<Rank> dranks = RankManager.getDonorRanks(uuid);
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
							sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The player is not online, and the name could not be found in the Mojang database. If you believe this was an error, it is likely because Mojang's servers are down.");
						}
					}
				}
				
				else if(!(sender instanceof Player) && args.length == 4 && args[0].equals("remove") && args[1].equals("donorrank")){
					UUID uuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
					if(uuid != null){
						ArrayList<Rank> donorranks = RankManager.getDonorRanks(uuid);
						
						if(donorranks.contains(RankManager.getRank(args[3]))){
							donorranks.remove(RankManager.getRank(args[3]));
							RankManager.setDonorRanks(uuid, donorranks);
							sender.sendMessage(tag+ChatColor.GREEN+"The donor rank was removed successfully.");
						}
						
						else{
							sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The player does not have that donor rank.");
						}
					}
					
					else{
						sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The player is not online, and the name could not be found in the Mojang database. If you believe this was an error, it is likely because Mojang's servers are down.");
					}
				}
				
				else if(args.length == 3 && args[0].equalsIgnoreCase("set")){
					String rankname = args[2];
					String playername = args[1];
					
					if(RankManager.isRankName(rankname)){
						
						Rank rank = RankManager.getRank(rankname);
						Player player = Bukkit.getPlayer(playername);
															
						if(!(sender instanceof Player) || rank.getLevel() < RankManager.getRankList().size()-2){
							
							if(player == null || !(sender instanceof Player) || RankManager.getRankOfPlayer(player.getUniqueId()).getLevel() < gp.getProfileOfPlayer((Player) sender).getRank().getLevel()){
								
								if(player == null || RankManager.getRankOfPlayer(player.getUniqueId()).getLevel() != rank.getLevel()){
								
									if(!(sender instanceof Player) || rank.getLevel() < gp.getProfileOfPlayer((Player) sender).getRank().getLevel()){
										
										Boolean messagesent = false;
										if (player != null && player.isOnline()) {
										
											if(RankManager.getRankOfPlayer(player.getUniqueId()).getLevel() < rank.getLevel()){
												gp.getProfileOfPlayer(player).promote(rank);
											}
											
											else{
												gp.getProfileOfPlayer(player).demote(rank);
											}
										}
										
										else{
											UUID uuid = Bukkit.getOfflinePlayer(playername).getUniqueId();

											
											if(uuid != null){
												if(RankManager.getRankOfPlayer(uuid).getLevel() < rank.getLevel()){
													RankManager.promoteOfflinePlayer(uuid, rank);
												}
												
												else{
													RankManager.demoteOfflinePlayer(uuid, rank);
												}
											}
											
											else{
												messagesent = true;
												sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The player is not online, and the name could not be found in the Mojang database. If you believe this was an error, it is likely because Mojang's servers are down.");
											}
										}
										if(!messagesent){
											sender.sendMessage(tag+ChatColor.GREEN+"Rank updated successfully.");
										}
									}
									
									else{
										sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: You cannot set a rank that is higher or equal to your own rank.");
									}
								}
								
								else{
									sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: The new rank must be different than the current one.");
								}
							}
							
							else{
								sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: You can only change the rank of a player with a lower rank than yourself.");
							}
						}
						
						else{
							sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: That rank must be set from the console for security reasons.");
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
}
