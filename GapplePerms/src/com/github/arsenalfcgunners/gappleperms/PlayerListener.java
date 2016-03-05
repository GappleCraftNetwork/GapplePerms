package com.github.arsenalfcgunners.gappleperms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener{
	GapplePerms gp;
	
	public PlayerListener(GapplePerms plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		gp = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerLoginEvent e){
		final Player player = e.getPlayer();
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(gp, new Runnable() { // Perms can only be added after a slight delay.
            public void run() {
            	if(player.isOnline()){ // Prevents a null pointer if the player gets kicked while joining.
	            	PlayerProfile profile = new PlayerProfile(player.getUniqueId(), gp.getRankManager().getRankOfPlayer(player.getUniqueId()), gp);
	        		gp.addPlayerProfile(profile);
            	}
            }
        }, 2L);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e){
		gp.removePlayerProfile(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event){
		if(event.getMessage().contains(" ")){	
			if(event.getMessage().substring(0, event.getMessage().indexOf(" ")).equalsIgnoreCase("/op")){
				event.getPlayer().sendMessage(gp.getTag()+ChatColor.YELLOW+"ERROR: OP must be assigned from the console.");
				event.setCancelled(true);
			}
		}
	}
}
