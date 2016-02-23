package com.github.arsenalfcgunners.gappleperms.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.arsenalfcgunners.gappleperms.DatabaseManager;
import com.github.arsenalfcgunners.gappleperms.GapplePerms;

// Not this will be removed, for testing purposes only!
public class ResetDB implements CommandExecutor{
	private GapplePerms gp;
	private DatabaseManager dm;

	public ResetDB(GapplePerms plugin) {
		gp = plugin;
		dm = gp.getDatabaseManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("resetdb")){
			
			if(!(sender instanceof Player)){
				dm.executeUpdate("TRUNCATE TABLE PlayerRanks");
			}
			return true;
		}
		return false;
	}
}
