package com.github.arsenalfcgunners.gappleperms;

import org.bukkit.ChatColor;

public class Rank {
	private String name;
	private ChatColor color;
	private int level;
	
	public Rank(String n, ChatColor c, int l){
		name = n;
		color = c;
		level = l;
	}
	
	public String getName(){
		return name;
	}
		
	public ChatColor getColor(){
		return color;
	}
	
	public int getLevel(){
		return level;
	}
}
