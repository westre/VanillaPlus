package com.westre.vanillaplus.main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.westre.vanillaplus.entity.User;

public class PlayerChat implements Listener {
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		User user = User.getPlayer(player);
		
		if(user.getAdminUser() != null && user.getAdminUser().isOnDuty()) {
			event.setFormat(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "G" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN + player.getName() + ": " + event.getMessage());
		}
		else {
			event.setFormat(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "G" + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + player.getName() + ": " + event.getMessage());
		}
	}
}
