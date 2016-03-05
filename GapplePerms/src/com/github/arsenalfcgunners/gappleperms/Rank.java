package com.github.arsenalfcgunners.gappleperms;

import org.bukkit.ChatColor;

public class Rank {
	private String name;
	private ChatColor color;
	private int level;
	private boolean isDonor;
	
	public Rank(String n, ChatColor c, int l, boolean id){
		name = n;
		color = c;
		level = l;
		isDonor = id;
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
	
	public boolean isDonor(){
		return isDonor;
	}
}
