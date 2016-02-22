package com.github.arsenalfcgunners.gappleperms.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("rank")){
			
			if(sender.hasPermission("gappleperms.changeperm")){
				
				if((args.length == 1 && args[0].equalsIgnoreCase("list"))){						
					String msg = tag+ChatColor.GREEN+"The follwing ranks are available:"+ChatColor.YELLOW;
					
					for(Rank r : rm.getRankList()){
						msg += "\n"+r.getName();
					}
					
					sender.sendMessage(msg);
				}
				
				else if((args.length == 2 && args[0].equalsIgnoreCase("get"))){
					sender.sendMessage(tag+"Coming soon!");
				}
				
				else{
					sender.sendMessage(tag+ChatColor.RED+"/rank list");
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
