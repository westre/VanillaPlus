package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;

public class UpdateInventoryStateTask extends BukkitRunnable {
	
	private int state;
	private int[] location;
	
	public UpdateInventoryStateTask(int state, int[] location) {
		this.state = state;
		this.location = location;
	}
	
	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE block SET state = ? WHERE x = ? AND y = ? AND z = ?");
			ps.setInt(1, this.state);
			ps.setInt(2, location[0]);
			ps.setInt(3, location[1]);
			ps.setInt(4, location[2]);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		mysql.close();
	}

}
