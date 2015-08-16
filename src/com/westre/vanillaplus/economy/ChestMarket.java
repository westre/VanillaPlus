package com.westre.vanillaplus.economy;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.westre.vanillaplus.entity.BlockManager;
import com.westre.vanillaplus.entity.CustomItemStack;
import com.westre.vanillaplus.entity.RegisteredBlock;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.task.EditMarketTask;

public class ChestMarket implements Listener {
	
	private Main plugin;
	
	public ChestMarket(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) event.getClickedBlock().getState();
			User user = User.getPlayer(event.getPlayer());
			
			BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
			for(BlockFace blockFace : blockFaces) {
				if(event.getClickedBlock().getRelative(blockFace).getLocation().getBlock().getType() == Material.WALL_SIGN) {	
					Sign sign = (Sign) event.getClickedBlock().getRelative(blockFace).getLocation().getBlock().getState();
					
					String scope = sign.getLine(0);
					String chestState = sign.getLine(1);
					
					if(scope.equals("[MARKET]") && (chestState.equals("[IMPORT]") || chestState.equals("[EXPORT]"))) {
						chest.getInventory().clear();
						refreshItemData(chest);
						user.setInCurrentChestShop(chest);
					}
				}
			}
		}
	}
	
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
						String scopetype = sign.getLine(1);
						
						if(scope.equals("[MARKET]") && scopetype.equals("[IMPORT]")) {
							int[] location = { chest.getX(), chest.getY(), chest.getZ() };
							block = BlockManager.getBlock(player, location);
							
							if(event.isShiftClick()) {
								player.sendMessage("Shift click is currently disabled for shops.");
								event.setCancelled(true);
					        }
							
							if(block != null) {
								// chest inv
								if(event.getRawSlot() <= event.getInventory().getSize()) {
									ItemStack itemStack = event.getCurrentItem();
									for(Material allowedItems : ImportExportItems.getItems()) {
										if(itemStack != null && itemStack.getData().getItemType().equals(allowedItems)) {
											ItemMeta itemMeta = itemStack.getItemMeta();
											for(String lore : itemMeta.getLore()) {
												if(lore.contains("CUR PRICE")) {
													String subString = lore.substring(17);
													if(user.getMoney() >= Integer.parseInt(subString)) {
														user.takeMoney(Integer.parseInt(subString));
														player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType()));
														new EditMarketTask(itemStack.getData().getItemType().toString(), EditMarketTask.Action.DEMAND, 1).runTaskAsynchronously(plugin);
														event.setCancelled(true);
														break;
													}
													else {
														player.sendMessage("Not enough money!");
														event.setCancelled(true);
														break;
													}
												}
											} 
										}
									}	
								}
								else {
									event.setCancelled(true);
								}
							}
						}
						else if(scope.equals("[MARKET]") && scopetype.equals("[EXPORT]")) {
							int[] location = { chest.getX(), chest.getY(), chest.getZ() };
							block = BlockManager.getBlock(player, location);
							
							if(event.isShiftClick()) {
								player.sendMessage("Shift click is currently disabled for shops.");
								event.setCancelled(true);
					        }
							
							ItemStack itemStack = event.getCursor();
						
							if(event.getCursor().getType() != Material.AIR && event.getRawSlot() <= event.getInventory().getSize() && block != null) {
								for(Material allowedItems : ImportExportItems.getItems()) {
									if(itemStack.getData().getItemType().equals(allowedItems)) {
										// item is correct
										Material materialData = itemStack.getData().getItemType();
										for(ItemStack containerItem : event.getInventory().getContents()) {
											if(containerItem.getData().getItemType() == materialData) {
												ItemMeta itemMeta = containerItem.getItemMeta();
												for(String lore : itemMeta.getLore()) {
													if(lore.contains("CUR PRICE")) {
														String subString = lore.substring(17);
														int price = Integer.parseInt(subString);
														int amount = price * itemStack.getAmount();
														player.sendMessage("You have sold " + itemStack.getAmount() + " " + materialData.toString() + " for: " + amount + "TPN (" + price + "/unit)");													
														new EditMarketTask(itemStack.getData().getItemType().toString(), EditMarketTask.Action.SUPPLY, itemStack.getAmount()).runTaskAsynchronously(plugin);
														user.giveMoney(amount);
														player.closeInventory();
														break;
													}
												}
												break;
											}
										}
										break;
									}
								}
							}
							
							// shitty workaround?
							if(event.getRawSlot() <= event.getInventory().getSize()) {
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().getState() instanceof Chest) {
			Chest chest = (Chest) event.getBlock().getState();
			BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
			for(BlockFace blockFace : blockFaces) {
				if(event.getBlock().getRelative(blockFace).getLocation().getBlock().getType() == Material.WALL_SIGN) {	
					Sign sign = (Sign) event.getBlock().getRelative(blockFace).getLocation().getBlock().getState();
					
					String chestState = sign.getLine(1);
					if(chestState.equals("[IMPORT]") || chestState.equals("[EXPORT]"))
						chest.getInventory().clear();
					break;
				}
			}
		}
	}
	
	private void refreshItemData(Chest chest) {
		for(ImportExportMaterial ieMaterial : ImportExportItems.getImportExportItems()) {
			String netChange = "undefined";
			String netChangePercentage = "undefined";
	
			if(ieMaterial.getNetChange() > 0) {
				netChange = "§c+" + ieMaterial.getNetChange();
				netChangePercentage = "§c+" + ieMaterial.getNetChangeInPercentage();
			}				
			else if(ieMaterial.getNetChange() < 0) 
			{
				netChange = "§a" + ieMaterial.getNetChange();
				netChangePercentage = "§a" + (-ieMaterial.getNetChangeInPercentage());
			}
			else {
				netChange = "§60";
				netChangePercentage = "§60.0";
			}
				
			chest.getInventory().addItem(new CustomItemStack(ieMaterial.getMaterial(), "§b" + ieMaterial.getMaterial().toString(), "§bCUR PRICE: " + ieMaterial.getCurrentPrice(), "§7PREV PRICE: " + ieMaterial.getOldPrice(), "NET CHANGE: " + netChange, "% CHANGE: " + netChangePercentage).getItemStack());
		}
	}
}
