package com.westre.vanillaplus.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.westre.vanillaplus.misc.GameUtils;

public class CustomItemStack {
	
	private ItemStack itemStack;
	private ItemMeta itemMeta;
	
	public static String[][] bills = { 	
		{ "§a100 TPN", "100", "§7Federal Reserve", "§7§o'Life changing quote from a player'" },
		{ "§650 TPN", "50", "§7Federal Reserve", "§7§o'Life changing quote from a player'" },
		{ "§920 TPN", "20", "§7Federal Reserve", "§7§o'Life changing quote from a player'" },
		{ "§c10 TPN", "10", "§7Federal Reserve", "§7§o'Life changing quote from a player'" },
		{ "§d5 TPN", "5", "§7Federal Reserve", "§7§o'Life changing quote from a player'" },
		{ "§e1 TPN", "1", "§7Federal Reserve", "§7§o'Life changing quote from a player'" }
	};
	
	public CustomItemStack(Material material, String name) {
		this(material, 1, name, "");
	}
	
	public CustomItemStack(Material material, String name, String... lores) {
		this(material, 1, name, lores);
	}
	
	public CustomItemStack(Material material, int quantity, String name, String... lores) {
		// Get ItemStack
		itemStack = new ItemStack(material, quantity);
		
		// Get meta data from the ItemStack
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§r" + name);
		
		// Get data from vargs and put it in an ArrayList
		List<String> applyLores = new ArrayList<String>();
		for(String lore : lores) {
			applyLores.add("§r§7" + lore);
		}
		
		// Apply the ArrayList to the meta data
		itemMeta.setLore(applyLores);
		itemStack.setItemMeta(itemMeta);
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public ItemMeta getItemMeta() {
		return itemMeta;
	}
	
	public static ItemStack removePriceLores(ItemStack item) {
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
			return item;
		}
		return null;
	}
	
}
