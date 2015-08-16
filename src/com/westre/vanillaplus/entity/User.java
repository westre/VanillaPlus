package com.westre.vanillaplus.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.task.InsertLogTask;

public class User {
	
	private static Map<Player, User> playerMap = new HashMap<Player, User>(); 
	
	private Player player;
	private int userId, buildStatus, tempBankAccount, minutesPlayed, levelId, invitedRegionId;
	private Chest inCurrentChest;
	private boolean isOwnerOfShop;
	public BukkitTask shopTimer;
	private Main plugin;
	private AdminUser adminUser;
	private RegionUser regionUser;
	
	public User(Player player, Main plugin) {
		this.player = player;
		this.buildStatus = 0;
		this.plugin = plugin;
		this.invitedRegionId = -1;
		
		playerMap.put(player, this);
	}

	public Player getPlayer() {
		return player;
	}
	
	public static User getUserFromID(int id) {
		for(User value : playerMap.values()) {
			if(value.getUserId() == id) return value;
        }
		return null;
	}
	
	public static void removePlayer(Player player) {
		playerMap.remove(player);
	}
	
	public static Map<Player, User> getUsers() {
		return playerMap;
	}
	
	public static User getPlayer(Player player) {
		return playerMap.get(player);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getBuildStatus() {
		return buildStatus;
	}

	public void setBuildStatus(int buildStatus) {
		this.buildStatus = buildStatus;
	}
	
	public void giveMoney(int value) {
		if(player.getInventory().firstEmpty() == -1) {
			player.sendMessage(ChatColor.RED + "Server has detected you have no inventory space remaining.");
            player.sendMessage(ChatColor.RED + "Due to this, you may collect " + value + " from the bank.");
            return;
		}
		
		new InsertLogTask(InsertLogTask.Log.SECURITY, this, "I have been given " + value + " TPN").runTaskAsynchronously(plugin);
		
		int index = 0;
		while(value > 0) {		
			int billType = Integer.parseInt(CustomItemStack.bills[index][1]);
			if(value > billType) {
				player.getInventory().addItem(new CustomItemStack(Material.PAPER, CustomItemStack.bills[index][0], CustomItemStack.bills[index][2], CustomItemStack.bills[index][3]).getItemStack());
				value -= billType;
				index = 0;	
			}
			else {
				if(index < 5) index++;
				else {
					player.getInventory().addItem(new CustomItemStack(Material.PAPER, CustomItemStack.bills[index][0], CustomItemStack.bills[index][2], CustomItemStack.bills[index][3]).getItemStack());
					break;
				}
			}
		}
	}
	
	public void takeMoney(int value) {
		int index = 0;
		int paidWith = 0;
		
		while(value > 0) {	
			if(player.getInventory().containsAtLeast(new CustomItemStack(Material.PAPER, CustomItemStack.bills[index][0], CustomItemStack.bills[index][2],  CustomItemStack.bills[index][3]).getItemStack(), 1)) {
				player.getInventory().removeItem(new CustomItemStack(Material.PAPER, CustomItemStack.bills[index][0], CustomItemStack.bills[index][2], CustomItemStack.bills[index][3]).getItemStack());
				value -= Integer.parseInt(CustomItemStack.bills[index][1]);
				paidWith += Integer.parseInt(CustomItemStack.bills[index][1]);
				index = 0;
			}
			else {
				if(index < 5) index++;
				else {
					player.getInventory().remove(new CustomItemStack(Material.PAPER, CustomItemStack.bills[index][0], CustomItemStack.bills[index][2], CustomItemStack.bills[index][3]).getItemStack());
					paidWith += Integer.parseInt(CustomItemStack.bills[index][1]);
					break;
				}
			}
		}
		
		if(value < 0) giveMoney(-value);
		player.sendMessage("Paid: " + paidWith);
		player.sendMessage("Change: " + -value);
	}
	
	public int getMoney() {
		ItemStack[] items = player.getInventory().getContents();
		ItemMeta itemMeta = null;
		int money = 0;
		
		for(ItemStack item : items) {
			if(item != null && item.getType() != Material.AIR) {
				itemMeta = item.getItemMeta();
				//player.sendMessage("Item: " + itemMeta.getDisplayName());
				
				List<String> lores = itemMeta.getLore();
				if(lores != null) {
					for(String lore : lores) {
						//player.sendMessage("Lore: " + lore);
						if(lore.contains("Federal Reserve")) {
							String strMoney = itemMeta.getDisplayName().substring(4, itemMeta.getDisplayName().length() - 3).trim();
							//player.sendMessage("Found: " + strMoney);
							money += Integer.parseInt(strMoney) * item.getAmount();
							continue;
						}
					}
				}
			}
		}		
		return money;
	}

	public Chest getInCurrentChestShop() {
		return inCurrentChest;
	}

	public void setInCurrentChestShop(Chest inCurrentChest) {
		this.inCurrentChest = inCurrentChest;
	}

	public boolean isOwnerOfShop() {
		return isOwnerOfShop;
	}

	public void setOwnerOfShop(boolean isOwnerOfShop) {
		this.isOwnerOfShop = isOwnerOfShop;
	}

	public int getTempBankAccountValue() {
		return tempBankAccount;
	}

	public void setTempBankAccountValue(int tempBankAccount) {
		this.tempBankAccount = tempBankAccount;
	}

	public int getMinutesPlayed() {
		return minutesPlayed;
	}

	public void setMinutesPlayed(int minutesPlayed) {
		this.minutesPlayed = minutesPlayed;
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}
	
	public void registerAsAdmin() {
		adminUser = new AdminUser();
	}
	
	public AdminUser getAdminUser() {
		if(adminUser != null) {
			return adminUser;
		}
		player.sendMessage("You are not an admin!");
		return null;
	}
	
	public void registerAsRegionUser() {
		regionUser = new RegionUser(this, plugin);
	}
	
	public void unRegisterAsRegionUser() {
		regionUser = null;
	}
	
	public RegionUser getRegionUser() {
		if(regionUser != null) {
			return regionUser;
		}
		return null;
	}

	public int getInvitedRegionId() {
		return invitedRegionId;
	}

	public void setInvitedRegionId(int invitedRegionId) {
		if(this.invitedRegionId == -1) {
			this.invitedRegionId = invitedRegionId;
			player.sendMessage("You have been invited to region id: " + invitedRegionId);
			player.sendMessage("Go to /options and accept it there.");
		}
		else {
			player.sendMessage("Someone else wants to invite you, but you have an invite pending.");
		}	
	}
	
}
