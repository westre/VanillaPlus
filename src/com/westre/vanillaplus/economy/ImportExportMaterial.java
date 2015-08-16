package com.westre.vanillaplus.economy;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ImportExportMaterial {
	
	private Material material;
	private int supply, demand, basePrice;
	private int oldSupply, oldDemand;
	private int currentPrice;
	
	public ImportExportMaterial(Material material, int basePrice) {
		this.supply = 1;
		this.demand = 1;
		this.oldSupply = 1;
		this.oldDemand = 1;
		this.currentPrice = 1;
		this.basePrice = basePrice;
		this.material = material;
	}

	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.oldSupply = this.supply;
		
		this.supply = supply;
	}

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.oldDemand = this.demand;
		
		this.demand = demand;
	}

	public Material getMaterial() {
		return material;
	}

	public int getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(int basePrice) {
		this.basePrice = basePrice;
	}

	public int getOldSupply() {
		return oldSupply;
	}

	public void setOldSupply(int oldSupply) {
		this.oldSupply = oldSupply;
	}

	public int getOldDemand() {
		return oldDemand;
	}

	public void setOldDemand(int oldDemand) {
		this.oldDemand = oldDemand;
	}

	public int calculateCurrentPrice() {
		this.currentPrice = basePrice + ((demand - supply) / 10) * basePrice / 10;
		
		if(currentPrice < basePrice / 2) {
			this.currentPrice = basePrice / 2;	
		}
		
		return this.currentPrice;
	}
	
	public int getOldPrice() {
		int oldPrice = basePrice + ((oldDemand - oldSupply) / 10) * basePrice / 10;
		
		if(oldPrice < basePrice / 2) {
			oldPrice = basePrice / 2;	
		}
		
		return oldPrice;
	}
	
	public int getCurrentPrice() {
		return currentPrice;
	}
	
	public int getNetChange() {
		return getCurrentPrice() - getOldPrice();
	}
	
	public double getNetChangeInPercentage() {
		double rawAnswer = 0.0;
		if(getCurrentPrice() > getOldPrice()) {
			double a = (double)getCurrentPrice() - (double)getOldPrice();
			rawAnswer = (a / (double)getOldPrice()) * 100.0;
		}
		else {
			double a = (double)getOldPrice() - (double)getCurrentPrice();
			rawAnswer = (a / (double)getOldPrice()) * 100.0;
		}
		
		//double rawAnswer = ((double)getNetChange() / (double)getCurrentPrice()) * 100.0;
		DecimalFormat df = new DecimalFormat("0.00##");
		df.setRoundingMode(RoundingMode.DOWN);
		return Double.parseDouble(df.format(rawAnswer));
	}
	
	public void displayFormatted() {
		Bukkit.broadcastMessage("------------------" + ChatColor.GRAY + "[" + ChatColor.AQUA + material.toString() + ChatColor.GRAY + "]" + ChatColor.WHITE + "------------------");
		
		int difference = getCurrentPrice() - getOldPrice();
		
		double rawAnswer = ((double)difference / (double)getCurrentPrice()) * 100.0;
		DecimalFormat df = new DecimalFormat("0.00##");
		df.setRoundingMode(RoundingMode.DOWN);
		String answer = df.format(rawAnswer);
		
		if(difference > 0) {
			Bukkit.broadcastMessage(ChatColor.GRAY + "CUR PRICE: " + ChatColor.AQUA + getCurrentPrice() + ChatColor.GRAY + "               PREV PRICE: " + ChatColor.AQUA + getOldPrice());
			Bukkit.broadcastMessage(ChatColor.GRAY + "NET CHANGE: " + ChatColor.DARK_RED + "+" + difference + ChatColor.GRAY + "               % CHANGE: " + ChatColor.DARK_RED + "+" + answer);	
		}
		else if(difference < 0) {
			Bukkit.broadcastMessage(ChatColor.GRAY + "CUR PRICE: " + ChatColor.AQUA + getCurrentPrice() + ChatColor.GRAY + "               PREV PRICE: " + ChatColor.AQUA + getOldPrice());
			Bukkit.broadcastMessage(ChatColor.GRAY + "NET CHANGE: " + ChatColor.DARK_GREEN + difference + ChatColor.GRAY + "               % CHANGE: " + ChatColor.DARK_GREEN + answer);
		}
		else {
			Bukkit.broadcastMessage(ChatColor.GRAY + "CUR PRICE: " + ChatColor.AQUA + getCurrentPrice() + ChatColor.GRAY + "               PREV PRICE: " + ChatColor.AQUA + getOldPrice());
			Bukkit.broadcastMessage(ChatColor.GRAY + "NET CHANGE: " + ChatColor.YELLOW + difference + ChatColor.GRAY + "               % CHANGE: " + ChatColor.YELLOW + answer);
		}			
	}
}
