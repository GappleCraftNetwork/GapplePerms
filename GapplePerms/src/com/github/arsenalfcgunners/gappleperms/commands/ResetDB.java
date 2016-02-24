package com.github.arsenalfcgunners.gappleperms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.arsenalfcgunners.gappleperms.GapplePerms;
import com.github.arsenalfcgunners.gappleperms.RankManager;

// Not this will be removed, for testing purposes only!
public class ResetDB implements CommandExecutor{
	private GapplePerms gp;
	private RankManager rm;

	public ResetDB(GapplePerms plugin) {
		gp = plugin;
		rm = gp.getRankManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("resetdb")){
			
			if(!(sender instanceof Player)){
				rm.executeUpdate("TRUNCATE TABLE PlayerRanks");
			}
			return true;
		}
		return false;
	}
}
