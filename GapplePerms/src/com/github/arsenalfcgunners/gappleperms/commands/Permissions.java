package com.github.arsenalfcgunners.gappleperms.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.github.arsenalfcgunners.gappleperms.GapplePerms;
import com.github.arsenalfcgunners.gappleperms.RankManager;

public class Permissions implements CommandExecutor {
	private GapplePerms gp;
	private String tag;
	private RankManager rm;

	public Permissions(GapplePerms plugin) {
		gp = plugin;
		tag = gp.getTag();
		rm = gp.getRankManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("permission")){
		
			if(sender.hasPermission("gappleperms.admin")){
				
				if((args.length == 2 && args[0].equalsIgnoreCase("list")) || (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("has")))){
						
					if((args.length == 2 && rm.isRankName(args[1])) || (args.length == 3 && rm.isRankName(args[2]))){
						
						if(args.length == 2 && args[0].equalsIgnoreCase("list")){
							String msg = tag+ChatColor.GREEN+"The rank "+rm.getRank(args[1]).getColor() + rm.getRank(args[1]).getName()+ChatColor.GREEN+" has the following permissions:";
							
							ArrayList<Permission> compare = rm.getPermissionsFromDB(rm.getRank(args[1]));
							
							for(Permission p : rm.getPerms(rm.getRank(args[1]))){
								if(compare.contains(p)){
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
							if(rm.hasPermission(rm.getRank(args[2]), args[1]) != 2){
								rm.addPermission(rm.getRank(args[2]), args[1]);
								sender.sendMessage(tag+ChatColor.GREEN+"The permission was added successfully!");
							}
							else{
								sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: That rank already has that permission.");
							}
						}
						
						else if(args.length == 3 && args[0].equalsIgnoreCase("delete")){
							if(rm.hasPermission(rm.getRank(args[2]), args[1]) == 1){
								rm.delPermission(rm.getRank(args[2]), args[1]);
								sender.sendMessage(tag+ChatColor.GREEN+"The permission was removed successfully!");
							}
							else{
								sender.sendMessage(tag+ChatColor.YELLOW+"ERROR: That rank does not have that permission. If the permission is inherited remove the permission from the base rank.");
							}
						}
						
						else if(args.length == 3 && args[0].equalsIgnoreCase("has")){
							int i = rm.hasPermission(rm.getRank(args[2]), args[1]);
							
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
