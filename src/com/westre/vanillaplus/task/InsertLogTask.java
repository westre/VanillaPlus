package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.MySQL;

public class InsertLogTask extends BukkitRunnable {
	
	public static enum Log {
		SECURITY,
		BUG
	}
	
	private Log log;
	private User user;
	private String message;
	
	public InsertLogTask(Log log, User user, String message) {
		this.log = log;
		this.user = user;
		this.message = message;
	}
	
	public void run() {
		if(log == Log.SECURITY) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO log (userid, date, message) VALUES (?, ?, ?)");
				
				ps.setInt(1, user.getUserId());
				ps.setLong(2, (long)(System.currentTimeMillis() / 1000L));
				ps.setString(3, message);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
		else if(log == Log.BUG) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO report (userid, date, message) VALUES (?, ?, ?)");
				
				ps.setInt(1, user.getUserId());
				ps.setLong(2, (long)(System.currentTimeMillis() / 1000L));
				ps.setString(3, message);
				ps.executeUpdate();
				ps.close();
				
				user.getPlayer().sendMessage("Your report has been submitted, thank you!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
	}
}
