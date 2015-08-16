package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.MySQL;

public class UpdateTempBankAccountsTask extends BukkitRunnable {
	
	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("BEGIN");
			ps.executeQuery();
			ps.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}

		try {
			for(User value : User.getUsers().values()) {
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE user SET tempbankaccount = ? WHERE id = ?");
				ps.setInt(1, value.getTempBankAccountValue());
				ps.setInt(2, value.getUserId());
				ps.executeUpdate();
				ps.close();
			}	
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("COMMIT");
			ps.executeQuery();
			ps.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		mysql.close();
	}
}
