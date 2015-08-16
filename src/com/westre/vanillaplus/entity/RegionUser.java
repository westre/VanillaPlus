package com.westre.vanillaplus.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.menu.IconMenu;
import com.westre.vanillaplus.region.Region;
import com.westre.vanillaplus.region.RegionManager;
import com.westre.vanillaplus.task.InsertRegionTask;

public class RegionUser {
	
	private User user;
	private int zoneStep = 0;
	private Region region;
	private Main plugin; 
	
	private int level, regionId;
	
	public RegionUser(User user, Main plugin) {
		this.user = user;
		this.plugin = plugin;
	}
	
	public void addZone(Location location) {
		if(zoneStep == 0) {
			region = new Region();
			region.setPointA(location);
			user.getPlayer().sendMessage("Point A set");
			zoneStep++;
		}
		else if(zoneStep == 1) {
			region.setPointB(location);
			user.getPlayer().sendMessage("Point B set");
			zoneStep = 0;
			
			IconMenu regionMenu = new IconMenu("Options Menu", 9, new IconMenu.OptionClickEventHandler() {
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	                event.getPlayer().sendMessage("You have chosen " + event.getName());
	                
	                if(event.getName().contains("ACCEPT")) {
	                	region.createZone();
	                	
	                	int[] loc = region.getLocation();
	                	RegionManager.addRegion(loc[0] + "," + loc[1] + "," + loc[2] + "," + loc[3] + "," + loc[4] + "," + loc[5], region);
	                	
	                	ArrayList<String> currentRoster = region.getAdministratorRoster();
	                	currentRoster.add(event.getPlayer().getName());
	                	region.setAdministratorRoster(currentRoster);
	                	
	                	region.setName(event.getPlayer().getName() + "'s region");

	                	new InsertRegionTask(event.getPlayer().getName() + "'s region", "undefined", loc[0], loc[1], loc[2], loc[3], loc[4], loc[5], user.getUserId(), event.getPlayer().getName(), region).runTaskAsynchronously(plugin);
	                	event.getPlayer().sendMessage("Region registered");
	                	
	                	event.getPlayer().getInventory().addItem(new CustomItemStack(Material.SPONGE, 1, "§9Information Block", "Freetopia Regional Information Block").getItemStack());
	                	event.getPlayer().sendMessage("You have been given an information block. Place it within the boundaries of your region.");
	                }
	                
	                event.setWillDestroy(true);
	                user.unRegisterAsRegionUser();
	            }
	        }, plugin)
	        .setOption(0, new ItemStack(Material.PAPER), "§r§l§fInformation", "§7Region Admin: " + user.getPlayer().getName(), "§7Region Blocks: " + region.calculateBlocks(), "§7Region Price: " + region.calculateBlocks() * 2 + " TPN")     
	        .setOption(7, new ItemStack(Material.PAPER), "§r§aACCEPT")
	        .setOption(8, new ItemStack(Material.PAPER), "§r§cDECLINE");
			
			regionMenu.open(user.getPlayer());
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		user.getPlayer().sendMessage("- Administrative Level: " + level);
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	
}
