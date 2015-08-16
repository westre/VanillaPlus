package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.MySQL;

public class UpdatePlayerDataTask extends BukkitRunnable {
	
	private User user;
	
	public UpdatePlayerDataTask(User user) {
		this.user = user;
	}
	
	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE user SET lastconnected = ?, buildstatus = ?, tempbankaccount = ?, minutesplayed = ?, levelid = ?, adminid = ? WHERE id = ?");
			ps.setLong(1, (long)(System.currentTimeMillis() / 1000L));
			ps.setInt(2, user.getBuildStatus());
			ps.setInt(3, user.getTempBankAccountValue());
			ps.setInt(4, user.getMinutesPlayed());
			ps.setInt(5, user.getLevelId());
			if(user.getAdminUser() == null) {
				ps.setInt(6, 0);
			}
			else {
				ps.setInt(6, user.getAdminUser().getAdminId());
			}
			ps.setInt(7, user.getUserId());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		mysql.close();
	}
}
