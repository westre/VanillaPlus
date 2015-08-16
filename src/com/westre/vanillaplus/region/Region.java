package com.westre.vanillaplus.region;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;


public class Region {
	private String name, description;
	private Location loc1, loc2;
	private int id, minX, minY, minZ, maxX, maxY, maxZ;	
	
	// offline roster
	private ArrayList<String> administratorRoster = new ArrayList<String>();
	private ArrayList<String> moderatorRoster = new ArrayList<String>();
	private ArrayList<String> residentRoster = new ArrayList<String>();
	
	// now in the city
	private ArrayList<String> onlineResidents = new ArrayList<String>();
	private ArrayList<String> onlineGuests = new ArrayList<String>();
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setPointA(Location loc1) {
		this.loc1 = loc1;
	}
	
	public void setPointB(Location loc2) {
		this.loc2 = loc2;
		
		this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
		this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	}
	
	public static boolean isEntityInside(Location loc, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		double px = loc.getX();
		double pz = loc.getZ();
		double py = loc.getY();
		
		if((px >= minX && px <= maxX) || (px <= minX && px >= maxX)) {
			if((pz >= minZ && pz <= maxZ) || (pz <= minZ && pz >= maxZ)) {
				if((py >= minY && py <= maxY) || (py <= minY && py >= maxY))
					return true;
			}
		}
		return false;
	}
	
	public int[] getLocation() {
		return new int[] { this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ };
	}
	
	public int calculateBlocks() {
		int blocks = 0;
		
		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				for(int z = minZ; z <= maxZ; z++){
					blocks++;
				}
			}
		}
		
		return blocks;
	}

	public void createZone() {
		for(int x = minX; x <= maxX; x++) {
			int highestY1 = loc1.getWorld().getHighestBlockYAt(x, minZ);
			int highestY2 = loc1.getWorld().getHighestBlockYAt(x, maxZ);
					
			loc1.getWorld().getBlockAt(x, highestY1, minZ).setType(Material.GLASS);
			loc1.getWorld().getBlockAt(x, highestY2, maxZ).setType(Material.GLASS);
			for(int y = minY; y <= maxY; y++ ) {
				for(int z = minZ; z <= maxZ; z++) {
					int highestY3 = loc1.getWorld().getHighestBlockYAt(minX, z);
					int highestY4 = loc1.getWorld().getHighestBlockYAt(maxX, z);
					
					loc1.getWorld().getBlockAt(minX, highestY3, z).setType(Material.GLASS);
					loc1.getWorld().getBlockAt(maxX, highestY4, z).setType(Material.GLASS);
					//Block block = loc1.getWorld().getBlockAt(x, y, z);				
					//block.setType(Material.COBBLESTONE);
					
					//int Ycoord = loc1.getWorld().getHighestBlockYAt(x, z);
					//loc1.getWorld().getBlockAt(x, Ycoord-1, z).setType(Material.GLASS);
				}
			}
		}
	}

	public ArrayList<String> getAdministratorRoster() {
		return administratorRoster;
	}

	public void setAdministratorRoster(ArrayList<String> administratorRoster) {
		this.administratorRoster = administratorRoster;
	}

	public ArrayList<String> getModeratorRoster() {
		return moderatorRoster;
	}

	public void setModeratorRoster(ArrayList<String> moderatorRoster) {
		this.moderatorRoster = moderatorRoster;
	}

	public ArrayList<String> getResidentRoster() {
		return residentRoster;
	}

	public void setResidentRoster(ArrayList<String> residentRoster) {
		this.residentRoster = residentRoster;
	}

	public ArrayList<String> getOnlineResidents() {
		return onlineResidents;
	}

	public void setOnlineResidents(ArrayList<String> onlineResidents) {
		this.onlineResidents = onlineResidents;
	}

	public ArrayList<String> getOnlineGuests() {
		return onlineGuests;
	}

	public void setOnlineGuests(ArrayList<String> onlineGuests) {
		this.onlineGuests = onlineGuests;
	}
	
	public void addOnlineResident(String name) {
		this.onlineResidents.add(name);
		
		for(String residents : this.getOnlineResidents()) {
			Bukkit.broadcastMessage("ACTIVE IN THIS REGION: " + residents);
		}
	}
	
	public void removeOnlineResident(String name) {
		this.onlineResidents.remove(name);
	}
	
	public boolean isPlayerInArrayList(String name) {
		if(this.onlineResidents.contains(name) || this.onlineGuests.contains(name) ) {
			return true;
		}
		return false;
	}
	
	public void addOnlineGuest(String name) {
		this.onlineGuests.add(name);
		
		for(String residents : this.getOnlineResidents()) {
			Bukkit.broadcastMessage("ACTIVE GUESTS IN THIS REGION: " + residents);
		}
	}
	
	public void removeOnlineGuest(String name) {
		this.onlineGuests.remove(name);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Location getLoc2() {
		return loc2;
	}

	public void setLoc2(Location loc2) {
		this.loc2 = loc2;
	}
}
