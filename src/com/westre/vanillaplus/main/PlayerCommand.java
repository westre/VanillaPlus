package com.westre.vanillaplus.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.menu.IconMenu;
import com.westre.vanillaplus.misc.GameUtils;
import com.westre.vanillaplus.region.Region;
import com.westre.vanillaplus.region.RegionManager;
import com.westre.vanillaplus.task.InsertLogTask;
import com.westre.vanillaplus.task.UpdateRegionTask;
import com.westre.vanillaplus.task.UpdateRegionTask.RegionUpdate;

public class PlayerCommand implements CommandExecutor {
	
	private Main plugin;
	
	public PlayerCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		final Player player = (Player) sender;
		
		if(label.equalsIgnoreCase("sell")) {
			ItemStack item = player.getItemInHand();
			ItemMeta itemMeta = item.getItemMeta();
			
			if(Integer.parseInt(args[0]) < 0) {
				player.sendMessage("No.... just no.");
				return false;
			}
			
			List<String> lores = itemMeta.getLore();
			if(lores != null) {
				
				// pre-check, if it's already on a sell status
				for(String lore : lores) {
			        if(lore.contains("Price")) {
			            player.sendMessage("This one is already on a sell status! /unsell it first.");
			            return false;
			        }
			    }
				
				if(args.length >= 2 && lores.size() < 7) {
					int startArg = 1; // 2nd arg
					StringBuilder sb = new StringBuilder();
					
					for(int i = startArg; i < args.length; i++) {
					    sb.append(args[i] + " ");
					}
					sb.deleteCharAt(sb.length() - 1); 
					String description = sb.toString();	
					
					lores.add("§7§l|||");
					lores.add("§aPrice: " + args[0]);
					lores.add("§b'" + description + "'");
					itemMeta.setLore(lores);
				}
				else if(args.length == 1 && lores.size() < 8) {
					lores.add("§7§l|||");
					lores.add("§aPrice: " + args[0]);
					itemMeta.setLore(lores);
				}
				else if(args.length == 1 && lores.size() >= 8 && lores.size() < 9) {
					lores.add("§aPrice: " + args[0]);
					itemMeta.setLore(lores);
				}
			}
			else {
				List<String> newlore = new ArrayList<String>();
				
				if(args.length >= 2) {
					int startArg = 1; // 2nd arg
					StringBuilder sb = new StringBuilder();
					
					for(int i = startArg; i < args.length; i++) {
					    sb.append(args[i] + " ");
					}
					sb.deleteCharAt(sb.length() - 1); 
					String description = sb.toString();	
					
					newlore.add("§7§l|||");
					newlore.add("§aPrice: " + args[0]);
					newlore.add("§b'" + description + "'");
					itemMeta.setLore(newlore);
				}
				else if(args.length == 1) {
					newlore.add("§7§l|||");
					newlore.add("§aPrice: " + args[0]);
					itemMeta.setLore(newlore);
				}
			}							
			item.setItemMeta(itemMeta);
		}
		
		if(label.equalsIgnoreCase("money")) {
			User user = User.getPlayer(player);
			
			if(user.getAdminUser().getAdminId() >= 5) {
				user.giveMoney(500);
			}		
			
			player.sendMessage("Your money: " + user.getMoney());
		}
		
		if(label.equalsIgnoreCase("options")) {
			User user = User.getPlayer(player);
			int inventoryCount = 27;
			
			if(user.getAdminUser() != null) inventoryCount = 36;
			
			IconMenu menu = new IconMenu("Options Menu", inventoryCount, new IconMenu.OptionClickEventHandler() {
				User user = User.getPlayer(player);
	            @Override
	            public void onOptionClick(IconMenu.OptionClickEvent event) {
	                if(event.getName().contains("Accept the region invite!")) {
	                	Region region = RegionManager.getRegion(user.getInvitedRegionId());
	                	
	                	ArrayList<String> residents = region.getResidentRoster();
	                	residents.add(event.getPlayer().getName());
	                	
	                	region.setResidentRoster(residents);
	                	new UpdateRegionTask(RegionUpdate.ADD_MEMBER, region, user).runTaskAsynchronously(plugin); 
	                	
	                	ArrayList<String> guests = region.getOnlineGuests();
	                	
	                	if(guests.size() > 0) {
	                		for(String names : guests) {
		                		if(names.contains(event.getPlayer().getName())) {
		                			guests.remove(names);
		                			break;
		                		}
		                	}  		
	                		region.setOnlineGuests(guests);
	                	}
	                		
	                	Bukkit.broadcastMessage(event.getPlayer().getName() + " accepted the invite");
	                	event.getPlayer().sendMessage("Welcome!");
	                }
	                else if(event.getName().contains("Deny the region invite!")) {
	                	user.setInvitedRegionId(-1);
	                	event.getPlayer().sendMessage("Denied.");
	                }
	                else if(event.getName().contains("Set admin duty status")) {
	                	if(user.getAdminUser() != null && user.getAdminUser().getAdminId() >= 2 && !user.getAdminUser().isOnDuty()) {
	                		user.getAdminUser().setOnDuty(true);
	    					player.sendMessage("You are currently on duty.");
	                	}
	                	else if(user.getAdminUser() != null && user.getAdminUser().getAdminId() >= 2 && user.getAdminUser().isOnDuty()) {
	                		user.getAdminUser().setOnDuty(false);
	    					player.sendMessage("You are currently off duty.");
	                	}
	                }
	                event.setWillDestroy(true);
	            }
	        }, plugin)
	        .setOption(0, new ItemStack(Material.PAPER), "§r§l§fGeneral Options", "General options are listed on this row.")     
	        .setOption(8, new ItemStack(Material.PAPER), "§rHelp/Tutorial", "A simple yet informative guide on the server.")
	        
	        .setOption(9, new ItemStack(Material.PAPER), "§r§l§eConstruction Options", "Construction options are listed on this row.")
	        .setOption(10, new ItemStack(Material.PAPER), "§rSet state of buildcode", "§7Current buildcode: Public", "§aClick to toggle")
	        
	        .setOption(18, new ItemStack(Material.PAPER), "§r§l§aRegion Options", "Region options are listed on this row.");	
			
			if(user.getInvitedRegionId() > 0) {
				menu.setOption(25, new ItemStack(Material.PAPER), "§rAccept the region invite!", "§rRegion ID: " + user.getInvitedRegionId(), "§aClick to accept");
				menu.setOption(26, new ItemStack(Material.PAPER), "§rDeny the region invite!", "§rRegion ID: " + user.getInvitedRegionId(), "§cClick to deny");
			}
			if(user.getAdminUser() != null) {
				menu.setOption(27, new ItemStack(Material.PAPER), "§r§l§cAdmin Options (level " + user.getAdminUser().getAdminId() + ")", "Admin options are listed on this row.");
				if(user.getAdminUser().isOnDuty())
					menu.setOption(28, new ItemStack(Material.PAPER), "§r§cSet admin duty status", "§7Toggles the admin status.", "§7Currently: §aOn duty", "§aClick to toggle");
				else
					menu.setOption(28, new ItemStack(Material.PAPER), "§r§cSet admin duty status", "§7Toggles the admin status.", "§7Currently: §cOff duty", "§aClick to toggle");
				menu.setOption(29, new ItemStack(Material.PAPER), "§r§cMute a player", "§7/admin mute [player]");
				menu.setOption(30, new ItemStack(Material.PAPER), "§r§cKick a player", "§7/admin kick [player]");
				menu.setOption(31, new ItemStack(Material.PAPER), "§r§cBan a player", "§7/admin ban [player]");
			}
			
			menu.open(player);
		}
		
		if(label.equalsIgnoreCase("unsell")) {
			ItemStack item = player.getItemInHand();
			ItemMeta itemMeta = item.getItemMeta();

			List<String> lores = itemMeta.getLore();
			List<String> newlores = null;
					
			if(lores != null) {			
				Iterator<String> iterator = lores.iterator();
				while(iterator.hasNext()) {
					String lore = iterator.next();
					if(lore.contains("§7") || lore.contains("§a") || lore.contains("§b'")) {
						iterator.remove();			
			        }
				}
				newlores = GameUtils.copyIterator(iterator);
				itemMeta.setLore(newlores);
				item.setItemMeta(itemMeta);
			}
			else {
				player.sendMessage("No valid lores found!");
			}			
		}
		
		if(label.equalsIgnoreCase("bug")) {
			if(args.length >= 1) {
				User user = User.getPlayer(player);
				int startArg = 0; // 1st arg
				StringBuilder sb = new StringBuilder();
				
				for(int i = startArg; i < args.length; i++) {
				    sb.append(args[i] + " ");
				}
				sb.deleteCharAt(sb.length() - 1); 
				String message = sb.toString();		
				
				new InsertLogTask(InsertLogTask.Log.BUG, user, message).runTaskAsynchronously(plugin);
			}
		}
		return false;
	}

}
