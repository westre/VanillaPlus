package com.westre.vanillaplus.main;


import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.region.Region;
import com.westre.vanillaplus.region.RegionManager;
import com.westre.vanillaplus.task.UpdateRegionTask;
import com.westre.vanillaplus.task.UpdateRegionTask.RegionUpdate;

public class RegionCommand implements CommandExecutor {
	
	private Main plugin;
	
	public RegionCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		User user = User.getPlayer(player);
		
		if(label.equalsIgnoreCase("r")) {
			if(args.length >= 1) {
				User pLoop;		
				int startArg = 0; // 1st arg
				StringBuilder sb = new StringBuilder();
				
				for(int i = startArg; i < args.length; i++) {
				    sb.append(args[i] + " ");
				}
				sb.deleteCharAt(sb.length() - 1); 
				String message = sb.toString();		
				
				boolean found = false;
				
				for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
				    String key = entry.getKey();
				    String[] coordinate = key.split(",");
				    
				    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
				    	Region region = entry.getValue();
				    	found = true;
				    	
				    	for(Player p : Bukkit.getServer().getOnlinePlayers()) {
							pLoop = User.getPlayer(p);

							if(pLoop.getRegionUser() != null) {
								if(pLoop.getRegionUser().getRegionId() == region.getId()) {
									if(user.getRegionUser().getLevel() == 0)
										p.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "R-" + region.getId() + ChatColor.GOLD + "] " + ChatColor.GRAY + "Guest " + ChatColor.WHITE + player.getName() + ": " + message);
									else if(user.getRegionUser().getLevel() == 1)
										p.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "R-" + region.getId() + ChatColor.GOLD + "] " + ChatColor.GRAY + "Resident " + ChatColor.WHITE + player.getName() + ": " + message);
									else if(user.getRegionUser().getLevel() == 2)
										p.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "R-" + region.getId() + ChatColor.GOLD + "] " + ChatColor.GRAY + "Mod " + ChatColor.WHITE + player.getName() + ": " + message);
									else if(user.getRegionUser().getLevel() == 3)
										p.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "R-" + region.getId() + ChatColor.GOLD + "] " + ChatColor.GRAY + "Admin " + ChatColor.WHITE + player.getName() + ": " + message);
								}
							}	
						}
				    	break;
				    }
				}	
				
				if(!found) player.sendMessage("You are not in the region.");
			}
		}
		
		if(label.equalsIgnoreCase("region")) {
			if(args.length >= 1) {
				if(args[0].equals("name")) {
					boolean found = false;
					
					for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
					    String key = entry.getKey();
					    String[] coordinate = key.split(",");
					    
					    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
					    	Region region = entry.getValue();
					    	found = true;
					    	
					    	if(user.getRegionUser().getLevel() == 3) {
					    		StringBuilder sb = new StringBuilder();
								
								for(int i = 1; i < args.length; i++) {
								    sb.append(args[i] + " ");
								}
								sb.deleteCharAt(sb.length() - 1); 
								String message = sb.toString();		
								
					    		region.setName(message);
					    		player.sendMessage("You have set the name to: " + message);
					    		
					    		new UpdateRegionTask(RegionUpdate.NAME, region, message).runTaskAsynchronously(plugin); 
					    	}					    	
					    	break;
					    }
					}			
					if(!found) player.sendMessage("You are not in the region.");
				}
				else if(args[0].equals("desc")) {
					boolean found = false;
					
					for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
					    String key = entry.getKey();
					    String[] coordinate = key.split(",");
					    
					    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
					    	Region region = entry.getValue();
					    	found = true;
					    	
					    	if(user.getRegionUser().getLevel() == 3) {
					    		StringBuilder sb = new StringBuilder();
								
								for(int i = 1; i < args.length; i++) {
								    sb.append(args[i] + " ");
								}
								sb.deleteCharAt(sb.length() - 1); 
								String message = sb.toString();		
								
					    		region.setDescription(message);
					    		player.sendMessage("You have set the desc to: " + message);
					    		
					    		new UpdateRegionTask(RegionUpdate.DESCRIPTION, region, message).runTaskAsynchronously(plugin); 
					    	}					    	
					    	break;
					    }
					}	
					
					if(!found) player.sendMessage("You are not in the region.");
				}
				else if(args[0].equals("invite")) {
					boolean found = false;
					
					for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
					    String key = entry.getKey();
					    String[] coordinate = key.split(",");
					    
					    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
					    	Region region = entry.getValue();
					    	found = true;
					    	
					    	if(user.getRegionUser().getLevel() >= 2) {
					    		Player target = Bukkit.getServer().getPlayer(args[1]);
					    		if(target != null) {
					    			User targetUser = User.getPlayer(target);
					    			targetUser.setInvitedRegionId(region.getId());
					    		}
					    		player.sendMessage("Attempted to invite: " + target.getName());
					    	}					    	
					    	break;
					    }
					}	
					
					if(!found) player.sendMessage("You are not in the region.");
				}
				else if(args[0].equals("remove")) {
					boolean found = false;
					
					for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
					    String key = entry.getKey();
					    String[] coordinate = key.split(",");
					    
					    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
					    	Region region = entry.getValue();
					    	found = true;
					    	
					    	if(user.getRegionUser().getLevel() >= 2) {
					    		Player target = Bukkit.getServer().getPlayer(args[1]);
					    		if(target != null) {
					    			boolean foundResident = false;
						    		
						    		for(String name : region.getResidentRoster()) {
						    			if(name.equals(target)) {
						    				foundResident = true;
						    				User userResident = User.getPlayer(target);
						    				
						                	ArrayList<String> residents = region.getResidentRoster();
						                	residents.remove(target.getName());
						                	region.setResidentRoster(residents);
						                	
						    				new UpdateRegionTask(RegionUpdate.DEL_MEMBER, region, userResident).runTaskAsynchronously(plugin); 
						    				player.sendMessage("Player removed.");
						    				break;
						    			}
						    		}
						    		
						    		if(!foundResident) player.sendMessage("Player not found, did you demote him to the lowest rank?");
					    		}
					    		else {
					    			player.sendMessage("Player not online");
					    		}
					    	}					    	
					    	break;
					    }
					}	
					
					if(!found) player.sendMessage("You are not in the region.");
				}
				else if(args[0].equals("promote")) {
					boolean found = false;
					
					for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
					    String key = entry.getKey();
					    String[] coordinate = key.split(",");
					    
					    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
					    	Region region = entry.getValue();
					    	found = true;
					    	
					    	if(user.getRegionUser().getLevel() >= 3) {
					    		Player target = Bukkit.getServer().getPlayer(args[1]);
					    		if(target != null) {
					    			User targetUser = User.getPlayer(target);
					    			String newRank = "undefined";
					    			
					    			if(targetUser.getRegionUser() != null) {
					    				switch(targetUser.getRegionUser().getLevel()) {
						    			case 1:
						    				newRank = "Moderator";
						    				targetUser.getRegionUser().setLevel(2);
						    				
						    				ArrayList<String> residents = region.getResidentRoster();
						    				residents.remove(target.getName());
						                	region.setResidentRoster(residents);
						                	
						    				ArrayList<String> moderators = region.getModeratorRoster();
						                	moderators.add(target.getName());
						                	region.setModeratorRoster(moderators);
						                	
						    				break;
						    			case 2:
						    				newRank = "Administrator";
						    				targetUser.getRegionUser().setLevel(3);
						    				
						    				ArrayList<String> mods = region.getModeratorRoster();
						    				mods.remove(target.getName());
						                	region.setModeratorRoster(mods);
						                	
						    				ArrayList<String> admins = region.getAdministratorRoster();
						    				admins.add(target.getName());
						                	region.setAdministratorRoster(admins);
						                	
						    				break;
						    			case 3:
						    				player.sendMessage("Already highest rank!");
						    				break;
				    					default:
				    						player.sendMessage("He's not in the region.");
						    			}
						    			
						    			if(!newRank.equals("undefined")) {
						    				player.sendMessage("Promoted: " + target.getName() + " to: " + newRank);
						    				
							    			new UpdateRegionTask(RegionUpdate.CHANGE_MEMBER_RANK, region, targetUser, newRank).runTaskAsynchronously(plugin); 
						    			}
					    			}
					    			else {
					    				player.sendMessage("GetRegionUser is null");
					    			}					
					    		}			    		
					    	}					    	
					    	break;
					    }
					}	
					if(!found) player.sendMessage("You are not in the region.");
				}
				else if(args[0].equals("demote")) {
					boolean found = false;
					
					for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
					    String key = entry.getKey();
					    String[] coordinate = key.split(",");
					    
					    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
					    	Region region = entry.getValue();
					    	found = true;
					    	
					    	if(user.getRegionUser().getLevel() >= 3) {
					    		Player target = Bukkit.getServer().getPlayer(args[1]);
					    		if(target != null) {
					    			User targetUser = User.getPlayer(target);
					    			String newRank = "undefined";
					    			
					    			if(targetUser.getRegionUser() != null) {
					    				switch(targetUser.getRegionUser().getLevel()) {
						    			case 1:
						    				player.sendMessage("Already lowest rank!");
						    				break;
						    			case 2:
						    				newRank = "Resident";
						    				targetUser.getRegionUser().setLevel(1);
						    				
						    				ArrayList<String> residents = region.getResidentRoster();
						    				residents.add(target.getName());
						                	region.setResidentRoster(residents);
						                	
						    				ArrayList<String> moderators = region.getModeratorRoster();
						                	moderators.remove(target.getName());
						                	region.setModeratorRoster(moderators);
						                	
						    				break;
						    			case 3:
						    				newRank = "Moderator";
						    				targetUser.getRegionUser().setLevel(2);
						    				              	
						    				ArrayList<String> mods = region.getModeratorRoster();
						    				mods.add(target.getName());
						                	region.setModeratorRoster(mods);
						                	
						                	ArrayList<String> admin = region.getAdministratorRoster();
						                	admin.remove(target.getName());
						                	region.setAdministratorRoster(admin);
						                	
						    				break;
						    			}
						    			
						    			if(!newRank.equals("undefined")) {
						    				player.sendMessage("Demoted: " + target.getName() + " to: " + newRank);
						    				new UpdateRegionTask(RegionUpdate.CHANGE_MEMBER_RANK, region, targetUser, newRank).runTaskAsynchronously(plugin); 
						    			}
					    			}
					    			else {
					    				player.sendMessage("GetRegionUser is null");
					    			}
					    		}			    		
					    	}					    	
					    	break;
					    }
					}	
					if(!found) player.sendMessage("You are not in the region.");
				}
			}
		}
		return false;
	}
}
