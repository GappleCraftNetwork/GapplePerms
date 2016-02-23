package com.github.arsenalfcgunners.gappleperms;

public class Rank {
	private String name;
	private String color;
	private int level;
	private boolean isDonor;
	
	public Rank(String n, String c, int l, boolean id){
		name = n;
		color = c;
		level = l;
		isDonor = id;
	}
	
	public String getName(){
		return name;
	}
		
	public String getColor(){
		return color;
	}
	
	public int getLevel(){
		return level;
	}
	
	public boolean isDonor(){
		return isDonor;
	}
}
