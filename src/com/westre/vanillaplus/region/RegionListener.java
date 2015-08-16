package com.westre.vanillaplus.region;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.menu.IconMenu;

public class RegionListener implements Listener {
	
	private Main main;
	
	public RegionListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		User user = User.getPlayer(player);
		
		if(e.getItemInHand().getType() == Material.TORCH) {
			ItemStack torch = e.getItemInHand();
			ItemMeta torchMeta = torch.getItemMeta();
			
			if(torchMeta.getDisplayName() == null) return;
			
			if(torchMeta.getDisplayName().contains("Landmarks")) {
				boolean found = false;
				
				for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
				    String key = entry.getKey();
				    String[] coordinate = key.split(",");
				    
				    if(Region.isEntityInside(e.getBlockPlaced().getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {	
				    	player.sendMessage("No.");
				    	found = true;
				    	e.setCancelled(true);
				    	break;
				    }
				}
				
				if(!found) {
					if(user.getRegionUser() == null) user.registerAsRegionUser();
					
					user.getRegionUser().addZone(e.getBlockPlaced().getLocation());
				}
				
			}
		}
		
		for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
		    String key = entry.getKey();
		    String[] coordinate = key.split(",");
		    
		    if(Region.isEntityInside(e.getBlockPlaced().getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {	
		    	if(user.getRegionUser() != null) {
		    		if(user.getRegionUser().getLevel() == 0) {
		    			player.sendMessage("Not enough permissions. Level too low.");
				    	e.setCancelled(true);
		    		}
		    	}
		    	else {
		    		player.sendMessage("Not enough permissions. No GetRegionUser?");
			    	e.setCancelled(true);
		    	}  	
		    	break;
		    }
		}
	}
	
	@EventHandler
	public void ReedPistonExtend(BlockPistonExtendEvent event) {
		if(event.isCancelled()) return;
		for(Block block : event.getBlocks()) {
			if(block.getType() == Material.SPONGE) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {	
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SPONGE) {
			for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
			    String key = entry.getKey();
			    String[] coordinate = key.split(",");
			    
			    if(Region.isEntityInside(event.getClickedBlock().getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
			    	Region region = entry.getValue();
			    	
			    	IconMenu menu = new IconMenu(region.getName(), 54, new IconMenu.OptionClickEventHandler() {
			            @Override
			            public void onOptionClick(IconMenu.OptionClickEvent event) {
			                           
			                event.setWillDestroy(true);
			            }
			        }, main)
			        .setOption(0, new ItemStack(Material.PAPER), "§r§l§fInformation", "§7ID: " + region.getId(), "§7Name: " + region.getName(), "§7Desc: " + region.getDescription(), "§7Blocks: " + region.calculateBlocks())     
			        .setOption(1, new ItemStack(Material.PAPER), "§r§eRegion Command", "§r§7Regional chat", "§r§a/r [message]")
			        .setOption(9, new ItemStack(Material.PAPER), "§r§cStaff", "§7Administrators/mods are the admins of this region.")
			        .setOption(18, new ItemStack(Material.PAPER), "§r§eResidents", "§7Builders of this region.")
			        .setOption(36, new ItemStack(Material.PAPER), "§r§eActive Residents", "§7Residents currently in this region.")
			    	.setOption(45, new ItemStack(Material.PAPER), "§r§8Active Guests", "§7Don't mind me, I'm just a guest.");
			    	
			    	for(int index = 27; index < 36; index++)
			    		menu.setOption(index, new ItemStack(Material.IRON_FENCE), "§r§8---");
			    	
			    	int adminColumnIndex = 10;
			    	for(String name : region.getAdministratorRoster()) {
			    		menu.setOption(adminColumnIndex, new ItemStack(Material.SKULL), "§r§c" + name, "Administrator");
			    		adminColumnIndex++;
			    	}
			    	
			    	for(String name : region.getModeratorRoster()) {
			    		menu.setOption(adminColumnIndex, new ItemStack(Material.SKULL), "§r§a" + name, "Moderator");
			    		adminColumnIndex++;
			    	}
			    	
			    	int residentColumnIndex = 19;
			    	for(String name : region.getResidentRoster()) {		    		
			    		menu.setOption(residentColumnIndex, new ItemStack(Material.SKULL), "§r§e" + name);
			    		residentColumnIndex++;
			    	}
			    	
			    	int residentOnlineColumnIndex = 37;
			    	for(String name : region.getOnlineResidents()) {		    		
			    		menu.setOption(residentOnlineColumnIndex, new ItemStack(Material.SKULL), "§r§e" + name);
			    		residentOnlineColumnIndex++;
			    	}
			    	
			    	int guestOnlineColumnIndex = 46;
			    	for(String name : region.getOnlineGuests()) {		    		
			    		menu.setOption(guestOnlineColumnIndex, new ItemStack(Material.SKULL), "§r§8" + name);
			    		guestOnlineColumnIndex++;
			    	}
			    	
			    	if(region.getAdministratorRoster().contains(event.getPlayer().getName()) || region.getModeratorRoster().contains(event.getPlayer().getName())) {
			    		menu.setOption(2, new ItemStack(Material.PAPER), "§r§cAdmin Command", "§r§7Sets the region name", "§r§a/region name [name]");
			    		menu.setOption(3, new ItemStack(Material.PAPER), "§r§cAdmin Command", "§r§7Sets the region description", "§r§a/region desc [name]");
			    		menu.setOption(4, new ItemStack(Material.PAPER), "§r§aAdmin/Mod Command", "§r§7Invite a player", "§r§a/region invite [name]");
			    		menu.setOption(5, new ItemStack(Material.PAPER), "§r§aAdmin/Mod Command", "§r§7Remove a player", "§r§a/region remove [name]");
			    		menu.setOption(6, new ItemStack(Material.PAPER), "§r§cAdmin Command", "§r§7Promote a player", "§r§a/region promote [name]");
			    		menu.setOption(7, new ItemStack(Material.PAPER), "§r§cAdmin Command", "§r§7Demote a player", "§r§a/region demote [name]");
			    	}
			    	
			    	menu.open(event.getPlayer());
			    	break;
			    }
			}
		}
	}
	
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		
		for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
		    String key = entry.getKey();
		    String[] coordinate = key.split(",");
		    
		    if(Region.isEntityInside(player.getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
		    	Region region = entry.getValue();
		    	User user = User.getPlayer(player);
		    	
		    	if(!region.isPlayerInArrayList(event.getPlayer().getName())) {
		    		if(region.getAdministratorRoster().contains(event.getPlayer().getName())) {
		    			region.addOnlineResident(event.getPlayer().getName());
		    			
		    			Bukkit.broadcastMessage(event.getPlayer().getName() + " welcome back admin");
			    		player.sendMessage("Welcome back, Administrator.");
		    			if(user.getRegionUser() == null) {
		    				user.registerAsRegionUser();
		    				user.getRegionUser().setLevel(3);
		    				user.getRegionUser().setRegionId(region.getId());
		    				Bukkit.broadcastMessage(event.getPlayer().getName() + " getregionuser null, instance regionuser created");
		    			}
		    			
		    		}
		    		else if(region.getModeratorRoster().contains(event.getPlayer().getName())) {
		    			region.addOnlineResident(event.getPlayer().getName());
		    			
		    			Bukkit.broadcastMessage(event.getPlayer().getName() + " welcome back mod");
			    		player.sendMessage("Welcome back, Moderator.");
		    			if(user.getRegionUser() == null) {
		    				user.registerAsRegionUser();
		    				user.getRegionUser().setLevel(2);
		    				user.getRegionUser().setRegionId(region.getId());
		    				Bukkit.broadcastMessage(event.getPlayer().getName() + " getregionuser null, instance regionuser created");
		    			}
		    			
		    		}
		    		else if(region.getResidentRoster().contains(event.getPlayer().getName())) {
		    			region.addOnlineResident(event.getPlayer().getName());
		    			
		    			Bukkit.broadcastMessage(event.getPlayer().getName() + " welcome back res");
		    			player.sendMessage("Welcome back, Resident.");
		    			if(user.getRegionUser() == null) {
		    				user.registerAsRegionUser();
		    				user.getRegionUser().setLevel(1);
		    				user.getRegionUser().setRegionId(region.getId());
		    				Bukkit.broadcastMessage(event.getPlayer().getName() + " getregionuser null, instance regionuser created");
		    			}	
		    		}
		    		else {
		    			Bukkit.broadcastMessage(event.getPlayer().getName() + " welcome back guest");
		    			
		    			region.addOnlineGuest(event.getPlayer().getName());
			    		player.sendMessage("Welcome Guest!");
			    		if(user.getRegionUser() == null) {
		    				user.registerAsRegionUser();
		    				user.getRegionUser().setLevel(0);
		    				user.getRegionUser().setRegionId(region.getId());
		    				Bukkit.broadcastMessage(event.getPlayer().getName() + " getregionuser null, instance regionuser created");
		    			}	
		    		}
		    	}
		    }
		    else {
		    	Region region = entry.getValue();
		    	User user = User.getPlayer(player);
		    	
		    	if(region.isPlayerInArrayList(event.getPlayer().getName())) {
		    		if(region.getAdministratorRoster().contains(event.getPlayer().getName()) || region.getModeratorRoster().contains(event.getPlayer().getName()) || region.getResidentRoster().contains(event.getPlayer().getName())) {
		    			region.removeOnlineResident(event.getPlayer().getName());
		    			
		    			if(user.getRegionUser() != null) {
		    				Bukkit.broadcastMessage(event.getPlayer().getName() + " getregionuser instance delted");
		    				user.unRegisterAsRegionUser();
		    				player.sendMessage("Bye. Resident");
		    			}
		    			
			    		
		    		}
		    		else {
		    			region.removeOnlineGuest(event.getPlayer().getName());
		    			
		    			if(user.getRegionUser() != null) {
		    				Bukkit.broadcastMessage(event.getPlayer().getName() + " getregionuser instance delted");
		    				user.unRegisterAsRegionUser();
		    				player.sendMessage("Bye. Guest");
		    			}
		    		}
		    	}
		    }
		}
	}
}
