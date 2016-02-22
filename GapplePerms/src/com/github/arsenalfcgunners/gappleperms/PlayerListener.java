package com.github.arsenalfcgunners.gappleperms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener{
	GapplePerms gp;
	
	public PlayerListener(GapplePerms plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		gp = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerPreLoginEvent e){
		PlayerProfile profile = new PlayerProfile(e.getUniqueId(), gp.getRankManager().getRankOfPlayer(e.getUniqueId()), gp);
		gp.addPlayerProfile(profile);		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e){
		gp.removePlayerProfile(e.getPlayer());
	}
}
