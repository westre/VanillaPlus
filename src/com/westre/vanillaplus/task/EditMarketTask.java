package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;

public class EditMarketTask extends BukkitRunnable {

	public static enum Action {
		SUPPLY,
		DEMAND
	};
	
	private String item;
	private Action action;
	private int amount;
	
	public EditMarketTask(String item, Action action, int amount) {
		this.item = item;
		this.action = action;
		this.amount = amount;
	}
	
	@Override
	public void run() {
		if(action == Action.SUPPLY) {
			MySQL mysql = new MySQL();
			
			// very unoptimized, optimize this pls
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE importexport SET materialsupply = materialsupply + ? WHERE materialname = ?");
				ps.setInt(1, amount);
				ps.setString(2, item);
				ps.executeUpdate();
				ps.close();		
			} catch (SQLException se) {
				se.printStackTrace();
			}
			
			mysql.close();
		}
		else if(action == Action.DEMAND) {
			MySQL mysql = new MySQL();
			
			// very unoptimized, optimize this pls
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE importexport SET materialdemand = materialdemand + ? WHERE materialname = ?");
				ps.setInt(1, amount);
				ps.setString(2, item);
				ps.executeUpdate();
				ps.close();		
			} catch (SQLException se) {
				se.printStackTrace();
			}
			
			mysql.close();
		}
	}
}
