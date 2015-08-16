package com.westre.vanillaplus.misc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.westre.vanillaplus.entity.User;

public class ItemDrop implements Listener {
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		User user = User.getPlayer(event.getPlayer());

		if(user.getInCurrentChestShop() == null) Bukkit.broadcastMessage("null"); else
		Bukkit.broadcastMessage(user.getInCurrentChestShop().toString());
		
		if(event.getPlayer().getOpenInventory().getType() == InventoryType.CHEST && user.getInCurrentChestShop() != null) {
			event.getPlayer().sendMessage("Dropping items from this chest is currently disabled. For now.");
			event.setCancelled(true);
			//user.getInCurrentChest().getInventory().addItem(event.getItemDrop().getItemStack());
			//event.getItemDrop().remove();
			
			//event.getPlayer().closeInventory();		
		}
	}
}
