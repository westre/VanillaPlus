package com.westre.vanillaplus.entity;

public class RegisteredBlock {
	
	private int userid;
	private String username;
	private long date;
	private String type, world;
	private int[] location;
	private int isPrivate, state;
	
	public static enum BlockProperty {
		DATABASE_TO_LIST,
		LIST_TO_DATABASE;
	}
	
	public RegisteredBlock(BlockProperty blockProperty, int userid, String username, long date, String type, int[] location, String world, int isPrivate, int state) {
		this.userid = userid;
		this.username = username;
		this.date = date;
		this.type = type;
		this.location = location;
		this.world = world;
		this.isPrivate = isPrivate;
		this.state = state;
		
		BlockManager.addBlock(blockProperty, location, this);
	}

	public int[] getLocation() {
		return location;
	}

	public int getUserid() {
		return userid;
	}

	public long getDate() {
		return date;
	}

	public String getType() {
		return type;
	}

	public String getWorld() {
		return world;
	}

	public boolean isPrivate() {
		if(this.isPrivate == 0) return true;
		else return false;
	}

	public void setPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUsername() {
		return username;
	}
}
