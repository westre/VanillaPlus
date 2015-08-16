package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;

public class OfflinePlayerTask extends BukkitRunnable {
	
	public static enum Action {
		TEMP_BANK_ACCOUNT;
	}
	
	private Action action;
	private int userId, money;
	
	public OfflinePlayerTask(Action action, int userId, int money) {
		this.action = action;
		this.userId = userId;
		this.money = money;
	}
	
	public void run() {
		System.out.println("Processing");	
		if(action == Action.TEMP_BANK_ACCOUNT) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM User WHERE id = ?");
				ps.setInt(1, userId);
				boolean exists = false;
				
				ResultSet rs = ps.executeQuery();		
				while(rs.next()) {
					PreparedStatement ps2 = mysql.getConnection().prepareStatement("UPDATE user SET tempbankaccount = tempbankaccount + ? WHERE id = ?");
					ps2.setInt(1, money);
					ps2.setInt(2, userId);
					ps2.executeUpdate();
					ps2.close();

					exists = true;
				} 
				
				if(exists) System.out.println("Offline Transaction taken place!");
				else System.out.println("ERROR! COULD NOT FIND OFFLINE PLAYER?");
				
				rs.close();
				ps.close();
				
			} catch (SQLException e) {
				
			}
			
			mysql.close();
		}
	}
}
