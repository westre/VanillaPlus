package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.CustomItemStack;
import com.westre.vanillaplus.entity.RegisteredBankAccounts;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.MySQL;

public class NewBankAccountTask extends BukkitRunnable {
	
	private User user;
	private Main plugin;
	
	public NewBankAccountTask(Main plugin, User user) {
		this.plugin = plugin;
		this.user = user;
	}
	
	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO bank (userid, date, money) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, user.getUserId());
			ps.setLong(2, (long)(System.currentTimeMillis() / 1000L));
			ps.setInt(3, 0);
			ps.executeUpdate();	
			
			ResultSet rskey = ps.getGeneratedKeys();	
			if (rskey != null && rskey.next()) {
				final int lastInsertedId = rskey.getInt(1);
				
				// start sync thread in async thread!
				new BukkitRunnable() {
					public void run() {
						RegisteredBankAccounts.addBankAccount(lastInsertedId, 0);
						user.getPlayer().getInventory().addItem(new CustomItemStack(Material.PAPER, "§9Debit Card", "Bank of Freetopia 2013", "Name: " + user.getPlayer().getName(), "Pin Code: " + lastInsertedId).getItemStack());
					}				
				}.runTask(plugin);
			}
			
			user.getPlayer().sendMessage("You have opened up a new bank account.");
			
			ps.close();
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
		mysql.close();
	}
}
