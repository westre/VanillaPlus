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
import com.westre.vanillaplus.entity.RegisteredBankAccounts;
import com.westre.vanillaplus.entity.RegisteredBlock;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.Freetopia;

public class ChestBank implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {	
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
			Chest chest = (Chest) event.getClickedBlock().getState();

			BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
			for(BlockFace blockFace : blockFaces) {
				if(event.getClickedBlock().getRelative(blockFace).getLocation().getBlock().getType() == Material.WALL_SIGN) {	
					Sign sign = (Sign) event.getClickedBlock().getRelative(blockFace).getLocation().getBlock().getState();
					User user = User.getPlayer(event.getPlayer());
					String scope = sign.getLine(0);

					if(scope.equals("[BANK]")) {
						user.setInCurrentChestShop(chest);
						if(Freetopia.bankClosed) {
							event.getPlayer().sendMessage("The bank is currently closed!");
							event.getPlayer().closeInventory();
							event.setCancelled(true);
							return;
						}
						
						if(event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() != Material.AIR) {
							ItemStack currentItem = event.getPlayer().getItemInHand();
							ItemMeta itemMeta = currentItem.getItemMeta();
							int pinCode = -1;
							
							if(itemMeta.getDisplayName() != null) {							
								if(itemMeta.getDisplayName().contains("Debit Card")) {
									if(itemMeta.getLore() != null) {
										for(String lore : itemMeta.getLore()) {
											if(lore.contains("Pin Code")) {
												try {
													pinCode = Integer.parseInt(lore.substring(14, lore.length()));
												}
												catch(Exception ex) {
													event.getPlayer().sendMessage("An error has occured retrieving your pin code (are you using an old version?)");
												}
												event.getPlayer().sendMessage("Pincode: " + pinCode);
												break;
											}
										}
									}
									else {
										event.getPlayer().sendMessage("Using an invalid debit card? You have been reported to the police! YOU FRAUD!");
									}
								}
								else {
									event.getPlayer().sendMessage("Please have your debit card in your hands!");
									event.getPlayer().closeInventory();
									event.setCancelled(true);
								}

								chest.getInventory().clear();
								refreshItemData(chest, User.getPlayer(event.getPlayer()), pinCode);
								event.getPlayer().sendMessage("Welcome to the official bank!");
							}
							else {
								event.getPlayer().sendMessage("Please have your debit card in your hands!");
								event.getPlayer().closeInventory();
								event.setCancelled(true);
							}
						}
						else {
							event.getPlayer().sendMessage("Please have your debit card in your hands!");
							event.getPlayer().closeInventory();
							event.setCancelled(true);
						}
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

						if(scope.equals("[BANK]")) {
							int[] location = { chest.getX(), chest.getY(), chest.getZ() };
							block = BlockManager.getBlock(player, location);
							
							if(event.isShiftClick()) {
								player.sendMessage("Shift click is currently disabled for shops.");
								event.setCancelled(true);
							}
							
							if(block != null) {
								// chest inv
								if(event.getRawSlot() <= event.getInventory().getSize()) {
									if(player.getItemInHand().getType() == Material.PAPER) {
										ItemMeta itemMeta = player.getItemInHand().getItemMeta();
										if(itemMeta.getLore() != null) {
											boolean isValid = false;
											for(String lore : itemMeta.getLore()) {
												if(lore.contains("Pin Code")) {
													int pinCode = Integer.parseInt(lore.substring(14).trim());
													isValid = true;
													if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BOOK && event.getCurrentItem().getType() != Material.AIR /* added recently*/) {
														ItemMeta itemMeta2 = event.getCurrentItem().getItemMeta();
														if(itemMeta2.getDisplayName().contains("Income")) {
															if(user.getTempBankAccountValue() >= 1) {
																player.sendMessage("Depositing " + user.getTempBankAccountValue() + " TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + user.getTempBankAccountValue());
																user.setTempBankAccountValue(0);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough income to do this, you poor soul.");
																event.setCancelled(true);
															}
														}
														event.setCancelled(true);
													}
													else if(event.getCurrentItem().getType() == Material.PAPER && event.getCurrentItem().getType() != Material.AIR) {
														ItemMeta itemMeta2 = event.getCurrentItem().getItemMeta();
														if(itemMeta2.getDisplayName().contains("Deposit 100")) {
															if(user.getMoney() >= 100) {
																player.sendMessage("Depositing 100 TPN");
																user.takeMoney(100);
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + 100);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Deposit 50")) {
															if(user.getMoney() >= 50) {
																player.sendMessage("Depositing 50 TPN");
																user.takeMoney(50);
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + 50);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Deposit 20")) {
															if(user.getMoney() >= 20) {
																player.sendMessage("Depositing 20 TPN");
																user.takeMoney(20);
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + 20);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Deposit 10")) {
															if(user.getMoney() >= 10) {
																player.sendMessage("Depositing 10 TPN");
																user.takeMoney(10);
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + 10);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Deposit 5")) {
															if(user.getMoney() >= 5) {
																player.sendMessage("Depositing 5 TPN");
																user.takeMoney(5);
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + 5);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Deposit 1")) {
															if(user.getMoney() >= 1) {
																player.sendMessage("Depositing 1 TPN");
																user.takeMoney(1);
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) + 1);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Withdraw 100")) {
															if(RegisteredBankAccounts.getMoney(pinCode) >= 100) {
																player.sendMessage("Withdrawing 100 TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) - 100);
																user.giveMoney(100);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Withdraw 50")) {
															if(RegisteredBankAccounts.getMoney(pinCode) >= 50) {
																player.sendMessage("Withdrawing 50 TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) - 50);
																user.giveMoney(50);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Withdraw 20")) {
															if(RegisteredBankAccounts.getMoney(pinCode) >= 20) {
																player.sendMessage("Withdrawing 20 TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) - 20);
																user.giveMoney(20);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Withdraw 10")) {
															if(RegisteredBankAccounts.getMoney(pinCode) >= 10) {
																player.sendMessage("Withdrawing 10 TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) - 10);
																user.giveMoney(10);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Withdraw 5")) {
															if(RegisteredBankAccounts.getMoney(pinCode) >= 5) {
																player.sendMessage("Withdrawing 5 TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) - 5);
																user.giveMoney(5);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
														else if(itemMeta2.getDisplayName().contains("Withdraw 1")) {
															if(RegisteredBankAccounts.getMoney(pinCode) >= 1) {
																player.sendMessage("Withdrawing 1 TPN");
																RegisteredBankAccounts.setMoney(pinCode, RegisteredBankAccounts.getMoney(pinCode) - 1);
																user.giveMoney(1);
																event.setCancelled(true);
															}
															else {
																player.sendMessage("You don't have enough money for this!");
																event.setCancelled(true);
															}
														}
													}					
													break;
												}
											}
											if(!isValid) {
												player.sendMessage("This debit card is not valid!");
												event.setCancelled(true);
											}
										}
										else {
											player.sendMessage("You need a real debit card for this to work!");
											event.setCancelled(true);
										}
									}

								}
								else {
									event.setCancelled(true);
								}
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

					String chestState = sign.getLine(0);
					if(chestState.equals("[BANK]"))
						chest.getInventory().clear();
					break;
				}
			}
		}
	}

	private void refreshItemData(Chest chest, User user, int pinCode) {
		chest.getInventory().addItem(new CustomItemStack(Material.BOOK, "Your Account", "Money: " + RegisteredBankAccounts.getMoney(pinCode) + " TPN").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.BOOK, "Deposit Your Income", "Income: " + user.getTempBankAccountValue() + " TPN", "Left click to deposit").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§aDeposit 100 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§aDeposit 50 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§aDeposit 20 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§aDeposit 10 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§aDeposit 5 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§aDeposit 1 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§cWithdraw 100 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§cWithdraw 50 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§cWithdraw 20 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§cWithdraw 10 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§cWithdraw 5 TPN", "Bank of Freetopia").getItemStack());
		chest.getInventory().addItem(new CustomItemStack(Material.PAPER, "§cWithdraw 1 TPN", "Bank of Freetopia").getItemStack());
	}
}
