package com.github.arsenalfcgunners.gappleperms.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.github.arsenalfcgunners.gappleperms.GapplePerms;
import com.github.arsenalfcgunners.gappleperms.Rank;
import com.github.arsenalfcgunners.gappleperms.RankManager;

public class Permissions implements CommandExecutor {
	private GapplePerms gp;
	private String tag;

	public Permissions(GapplePerms plugin) {
		gp = plugin;
		tag = gp.getTag();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("permission")){
		
			if(sender.hasPermission("gappleperms.admin")){
				
				if((args.length == 2 && args[0].equalsIgnoreCase("list")) || (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("has")))){
						
					if((args.length == 2 && RankManager.isRankName(args[1])) || (args.length == 3 && RankManager.isRankName(args[2]))){
						
						if(args.length == 2 && args[0].equalsIgnoreCase("list")){
							Rank rank = RankManager.getRank(args[1]);
							String msg = tag+ChatColor.GREEN+"The rank "+rank.getColor() + rank.getName()+ChatColor.GREEN+" has the following permissions:";
														
							for(Permission p : RankManager.getPerms(rank)){
								if(RankManager.hasPermission(rank, p.getName()) == 1){
									msg += "\n"+ChatColor.GREEN+p.getName();
								}
								else{
									msg += "\n"+ChatColor.DARK_AQUA+p.getName();
								}
							}
							
							msg += "\n"+ChatColor.YELLOW+ChatColor.ITALIC+"NOTE: Inherited permissions are aqua, navitive permissions are green.";
							
							sender.sendMessage(msg);
						}
						
						else if(args.length == 3 && args[0].equalsIgnoreCase("add")){
							Rank rank = RankManager.getRank(args[2]);
							if(RankManager.hasPermission(rank, args[1]) == 3){
								RankManager.addPermission(rank, args[1]);
								sender.sendMessage(tag+ChatColor.GREEN+"The permission was added successfully!");
							}
							else{
								sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: That rank already has that permission.");
							}
						}
						
						else if(args.length == 3 && args[0].equalsIgnoreCase("delete")){
							Rank rank = RankManager.getRank(args[2]);
							if(RankManager.hasPermission(rank, args[1]) == 1){
								RankManager.delPermission(rank, args[1]);
								sender.sendMessage(tag+ChatColor.GREEN+"The permission was removed successfully!");
							}
							else{
								sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: That rank does not have that permission. If the permission is inherited remove the permission from the base rank.");
							}
						}
						
						else if(args.length == 3 && args[0].equalsIgnoreCase("has")){
							int i = RankManager.hasPermission(RankManager.getRank(args[2]), args[1]);
							
							if(i == 1){
								sender.sendMessage(tag+ChatColor.DARK_GREEN+"That rank has that permission!");
							}
							
							else if(i == 2){
								sender.sendMessage(tag+ChatColor.GREEN+"That rank inherits that permission!");
							}
							
							else{
								sender.sendMessage(tag+ChatColor.RED+"That rank does not have that permission!");
							}
						}
						
						else{
							sender.sendMessage(tag+ChatColor.RED+"An unexpected error has occured. Please report this to a developer.");
						}
					}
					
					else{
						sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: Rank not found!");
					}
				}
				
				else{
					sender.sendMessage(tag+ChatColor.RED+"/permission <add/delete/has> <permission> <rank> or /permission list <rank>");
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
