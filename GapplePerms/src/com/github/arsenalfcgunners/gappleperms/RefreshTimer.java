package com.github.arsenalfcgunners.gappleperms;

import java.util.TimerTask;

public class RefreshTimer extends TimerTask{

	GapplePerms gp;
	int time;
	
	public RefreshTimer(GapplePerms plugin){
		gp = plugin;
		time = 5;
	}
	
	@Override
	public void run() {
		time --;
		if(time <= 0){
			time = 5;
			for(PlayerProfile pp : gp.getPlayerProfiles()){
				pp.refresh();
			}
		}
	}

}
