package com.westre.vanillaplus.region;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class RegionManager {
	
	private static HashMap<String, Region> region = new HashMap<String, Region>();
	
	public static void addRegion(String location, Region faction) {
		region.put(location, faction);
	}
	
	public static HashMap<String, Region> getRegions() {
		return region;
	}

	public static Region getRegion(int id) {
		for(Region value : region.values()) {
			if(value.getId() == id) return value;
        }
		return null;
	}
	
	public static void outputAllRegions() {
		for (Map.Entry<String, Region> entry : region.entrySet()) {
		    String key = entry.getKey();
		    //Region value = entry.getValue();
		    
		    Bukkit.broadcastMessage("Key: " + key);
		    //Bukkit.broadcastMessage("Value (Faction Money): " + value.getMoney());
		}
	}
}
