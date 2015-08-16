package com.westre.vanillaplus.main;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.User;

public class AdminCommand implements CommandExecutor {
	
	//private Main plugin;
	
	public AdminCommand(Main plugin) {
		//this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		User user = User.getPlayer(player);
		
		if(label.equalsIgnoreCase("a")) {
			if(args.length >= 1 && user.getAdminUser().getAdminId() >= 2 || player.isOp()) {
				User pLoop;		
				int startArg = 0; // 1st arg
				StringBuilder sb = new StringBuilder();
				
				for(int i = startArg; i < args.length; i++) {
				    sb.append(args[i] + " ");
				}
				sb.deleteCharAt(sb.length() - 1); 
				String message = sb.toString();		
				
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					pLoop = User.getPlayer(p);
					if(pLoop.getAdminUser().getAdminId() >= 4 || p.isOp()) {
						p.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED + "A" + ChatColor.DARK_RED + "] " + ChatColor.RED + player.getName() + ": " + message);
					}
				}
			}
		}
		
		if(label.equalsIgnoreCase("admin")) {
			if(args.length >= 1) {
				if(args[0].equals("on") && user.getAdminUser().getAdminId() >= 2 && !user.getAdminUser().isOnDuty()) {
					user.getAdminUser().setOnDuty(true);
					player.sendMessage("You are currently on duty.");
				}
				else if(args[0].equals("off") && user.getAdminUser().getAdminId() >= 2 && user.getAdminUser().isOnDuty()) {
					user.getAdminUser().setOnDuty(false);
					player.sendMessage("You are now off duty");
				}
			}	
		}
		return false;
	}

}
