package com.westre.vanillaplus.economy;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.westre.vanillaplus.entity.BlockManager;
import com.westre.vanillaplus.entity.CustomItemStack;
import com.westre.vanillaplus.entity.RegisteredBlock;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.task.OfflinePlayerTask;


public class ChestShop implements Listener {
	
	private Main plugin;
	
	public ChestShop(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		User user = User.getPlayer(player);
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) event.getClickedBlock().getState();
			
			BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
			for(BlockFace blockFace : blockFaces) {
				if(event.getClickedBlock().getRelative(blockFace).getLocation().getBlock().getType() == Material.WALL_SIGN) {	
					Sign sign = (Sign) event.getClickedBlock().getRelative(blockFace).getLocation().getBlock().getState();
					
					String scope = sign.getLine(0);

					if(scope.equals("[SHOP]")) {
						int[] location = { chest.getX(), chest.getY(), chest.getZ() };
						RegisteredBlock block = BlockManager.getBlock(player, location);
						
						if(block != null && block.getUserid() != user.getUserId()) {
							player.sendMessage("Welcome to " + block.getUsername() + "'s shop!");
							user.setInCurrentChestShop(chest);
						}
						else if(block != null && block.getUserid() == user.getUserId()) {
							player.sendMessage("You are the owner of this shop.");
							user.setInCurrentChestShop(chest);
							user.setOwnerOfShop(true);
							Bukkit.broadcastMessage(user.getInCurrentChestShop().toString());
						}
					}
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player && event.getInventory().getType() == InventoryType.CHEST) {
			Player player = (Player) event.getWhoClicked();
			User user = User.getPlayer(player);
			RegisteredBlock block = null;
			
			InventoryHolder ih = event.getInventory().getHolder();
			if(ih instanceof Chest) {
				Chest chest = (Chest) ih;
				
				BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
				for(BlockFace blockFace : blockFaces) {
					if(chest.getLocation().getBlock().getRelative(blockFace).getType() == Material.WALL_SIGN) {	
						Sign sign = (Sign) chest.getLocation().getBlock().getRelative(blockFace).getLocation().getBlock().getState();
						
						String scope = sign.getLine(0);

						if(scope.equals("[SHOP]")) {
							int[] location = { chest.getX(), chest.getY(), chest.getZ() };
							block = BlockManager.getBlock(player, location);
							
							if(event.isShiftClick()) {
								player.sendMessage("Shift click is currently disabled for shops.");
								event.setCancelled(true);
					        }
							
							if(block != null) {
								if(event.getSlotType().equals(SlotType.QUICKBAR) && event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
									ItemStack currentItem = event.getCursor();
									ItemMeta itemMeta = currentItem.getItemMeta();
									
									int price = -1;
									int amount = -1;
									
									if(itemMeta.getLore() != null) { // added recently
										for(String lore : itemMeta.getLore()) {
											if(lore.contains("Price")) {
												lore = lore.trim();
												price = Integer.parseInt(lore.substring(9).trim());
												amount = currentItem.getAmount();
											}
										}
										
										if(user.getMoney() >= price * amount) {
											user.takeMoney(price * amount);
											CustomItemStack.removePriceLores(currentItem);
											player.sendMessage("You have bought a " + currentItem.getType().toString() + "!");
											player.sendMessage("Price Per Unit: " + price + ", Amount: " + amount);	
											
											try {
												User seller = User.getUserFromID(block.getUserid());
												seller.setTempBankAccountValue(price * amount);
											}
											catch(NullPointerException ex) {
												new OfflinePlayerTask(OfflinePlayerTask.Action.TEMP_BANK_ACCOUNT, block.getUserid(), price * amount).runTaskAsynchronously(plugin);
											}
											
										}
										else {
											//chest.getInventory().addItem(currentItem);
											
											player.sendMessage("You don't have enough money to make this purchase! " + currentItem.getType().toString());		
											//player.getItemOnCursor().setAmount(50);
											player.updateInventory();
											//player.getItemOnCursor().setType(Material.AIR);
											event.setCancelled(true);
											//player.getInventory().removeItem(currentItem);
											
											//player.updateInventory();
										}
									}			
								}
								else if(event.getRawSlot() >= event.getInventory().getSize()) { // clicked in player inventory
									if(event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
										ItemStack currentItem = event.getCursor();
										ItemMeta itemMeta = currentItem.getItemMeta();
										
										int price = -1;
										int amount = -1;
										
										if(itemMeta.getLore() != null) { // added recently
											for(String lore : itemMeta.getLore()) {
												if(lore.contains("Price")) {
													lore = lore.trim();
													price = Integer.parseInt(lore.substring(9).trim());
													amount = currentItem.getAmount();
												}
											}
												
											if(user.getMoney() >= price * amount && user.getUserId() != block.getUserid()) {
												user.takeMoney(price * amount);
												CustomItemStack.removePriceLores(currentItem);
												player.sendMessage("You have bought a " + currentItem.getType().toString() + "!");
												player.sendMessage("Price Per Unit: " + price + ", Amount: " + amount);
												
												try {
													User seller = User.getUserFromID(block.getUserid());
													seller.setTempBankAccountValue(price * amount);
												}
												catch(NullPointerException ex) {
													new OfflinePlayerTask(OfflinePlayerTask.Action.TEMP_BANK_ACCOUNT, block.getUserid(), price * amount).runTaskAsynchronously(plugin);;
												}
											}
											else if(user.getMoney() <= price * amount && user.getUserId() != block.getUserid()) {
												player.sendMessage("You don't have enough money to make this purchase!");	
												player.closeInventory();
												event.setCancelled(true);
											}
											else {
												CustomItemStack.removePriceLores(currentItem);
												player.sendMessage("Your shop, your command!");
											}
										}
									}
								}
							}
							if(event.getCurrentItem() != null) {
								ItemStack currentItem = event.getCurrentItem();
								ItemMeta itemMeta = currentItem.getItemMeta();
								
								if(currentItem.getType() != Material.AIR && (event.getRawSlot() >= event.getInventory().getSize() || event.getSlotType().equals(SlotType.QUICKBAR))) {
									if(currentItem.getItemMeta() != null) /*recently addeed*/ {
										itemMeta = currentItem.getItemMeta();
										
										if(block.getUserid() != user.getUserId()) {
											player.sendMessage("This is not your shop!");
											event.setCancelled(true);
										}
										
										if(itemMeta.getLore() == null) {
											event.setCancelled(true);
										}
										else {
											boolean isValid = false;
											
											for(String lore : itemMeta.getLore()) {
												if(lore.contains("Price")) {
													isValid = true;
													break;
												}
											}
											
											if(!isValid) {
												//player.sendMessage("NO");
												event.setCancelled(true);
											}
										}
									}	
								}
							}
						}
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if(event.getInventory().getType() == InventoryType.CHEST) {
			HumanEntity entity = event.getPlayer();
			final Player player = (Player) entity;
			final User user = User.getPlayer(player);
			
			user.setOwnerOfShop(false);
			user.setInCurrentChestShop(null);
		}
	}
}
