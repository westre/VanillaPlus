package com.westre.vanillaplus.main;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.task.LoginTask;
import com.westre.vanillaplus.task.UpdatePlayerDataTask;

public class PlayerConnection implements Listener {
	
	private final Main plugin;
	private User player;
	
	public PlayerConnection(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerConnect(final PlayerJoinEvent e) {
		player = new User(e.getPlayer(), plugin);
		new LoginTask(player).runTaskAsynchronously(plugin);
		
		e.getPlayer().sendMessage("Welcome to Freetopia.");
		e.getPlayer().sendMessage("This server/plugin is currently in beta, please forward all bug reports to the developer by using " + ChatColor.RED + "/bug [report]" + ChatColor.WHITE + ", thank you and have fun.");
		
		new BukkitRunnable() {
			public void run() {
				User user = User.getPlayer(e.getPlayer());
				if(user.getTempBankAccountValue() > 0)
					e.getPlayer().sendMessage("While you were offline, you have accumulated a total of " + ChatColor.AQUA + user.getTempBankAccountValue() + " TPN" + ChatColor.WHITE + ". Deposit at the bank to use this amount.");
			}			
		}.runTaskLaterAsynchronously(plugin, 100L);
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		player = User.getPlayer(e.getPlayer());
		new UpdatePlayerDataTask(player).runTaskAsynchronously(plugin);
		User.removePlayer(e.getPlayer());
	}
}
