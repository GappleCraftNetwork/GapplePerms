package com.github.arsenalfcgunners.gappleperms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener{
	GapplePerms gp;
	
	public PlayerListener(GapplePerms plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		gp = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerLoginEvent e){
		PlayerProfile profile = new PlayerProfile(e.getPlayer().getUniqueId(), gp.getRankManager().getRankOfPlayer(e.getPlayer().getUniqueId()), gp);
		gp.addPlayerProfile(profile);	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e){
		gp.removePlayerProfile(e.getPlayer());
	}
}
