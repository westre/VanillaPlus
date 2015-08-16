package com.westre.vanillaplus.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.Freetopia;

public class TimeTask extends BukkitRunnable {

	public void run() {
		
		World world = Bukkit.getWorlds().get(0);
		//		for(World world : Bukkit.getServer().getWorlds()) {
		//Bukkit.broadcastMessage(GameUtils.parseTime(world.getTime()));
		if(world.getTime() >= 17000 && world.getTime() <= 24000) {
			if(!Freetopia.bankClosed) {
				Freetopia.bankClosed = true;
				Bukkit.broadcastMessage(ChatColor.GRAY + "The bank has been closed, it will open once again at 6AM.");
			}
		}
		else {
			if(Freetopia.bankClosed) {
				Bukkit.broadcastMessage(ChatColor.GRAY + "The bank has been opened once again.");
				Freetopia.bankClosed = false;
			}
		}
		
		if(world.getTime() >= 18000 && world.getTime() <= 24000 || world.getTime() >= 0 && world.getTime() <= 2000) {
			if(!Freetopia.marketClosed) {
				Freetopia.marketClosed = true;
				Bukkit.broadcastMessage(ChatColor.GRAY + "The value of the market has been frozen, it will unfreeze once again at 8AM.");
			}
		}
		else {
			if(Freetopia.marketClosed) {
				Bukkit.broadcastMessage(ChatColor.GRAY + "The market has been opened once again.");
				Freetopia.marketClosed = false;
			}
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			User user = User.getPlayer(player);
			
			if(user.getMinutesPlayed() >= 60 && user.getLevelId() == 1) {
				user.setLevelId(2);
				player.sendMessage(ChatColor.DARK_GREEN + "Your playtime on this server is currently 1 hour, thanks for your dedication.");
			}
			else if(user.getMinutesPlayed() >= 180 && user.getLevelId() == 2) {
				user.setLevelId(3);
				player.sendMessage(ChatColor.DARK_GREEN + "Your playtime on this server is currently 3 hours, thanks for your dedication.");
			}
			else if(user.getMinutesPlayed() >= 420 && user.getLevelId() == 3) { // 7 hours
				user.setLevelId(4);
				player.sendMessage(ChatColor.DARK_GREEN + "Your playtime on this server is currently 7 hours, thanks for your dedication.");
			}
			else if(user.getMinutesPlayed() >= 1200 && user.getLevelId() == 4) { // 20 hours
				user.setLevelId(5);
				player.sendMessage(ChatColor.DARK_GREEN + "Your playtime on this server is currently 20 hours, thanks for your dedication.");
			}
			else if(user.getMinutesPlayed() >= 2880 && user.getLevelId() == 5) { // 48 hours
				user.setLevelId(6);
				player.sendMessage(ChatColor.DARK_GREEN + "Your playtime on this server is currently 48 hours, thanks for your dedication.");
			}
			else if(user.getMinutesPlayed() >= 6000 && user.getLevelId() == 5) { // 100 hours
				user.setLevelId(7);
				player.sendMessage(ChatColor.DARK_GREEN + "Your playtime on this server is currently 100 hours, thanks for your dedication.");
			}
		}
		/*
		for(Player player : Bukkit.getOnlinePlayers()) {
			ItemStack[] items = player.getInventory().getContents();
			for(ItemStack item : items) {
				if(item != null && item.getType() != Material.AIR) {
					if(item.getType() == Material.WATCH) {
						for(World world : Bukkit.getServer().getWorlds()) {
							if(world.getTime() >= 20000 && world.getTime() <= 24000 || world.getTime() >= 0 && world.getTime() <= 8000) {
								if(!Freetopia.marketClosed) {
									Freetopia.marketClosed = true;
									Bukkit.broadcastMessage(ChatColor.GRAY + "The market has been closed, it will open at 8AM.");
								}
							}
							else {
								if(Freetopia.marketClosed) {
									Bukkit.broadcastMessage(ChatColor.GRAY + "The market has been opened once again.");
									Freetopia.marketClosed = true;
								}
							}
				        	Bukkit.broadcastMessage("Time: " + world.getTime());
				        	//GameUtils.parseTime(world.getTime());
				        	break;
				        }
						break;
					}
				}	
			}
		}	*/
	}
}
