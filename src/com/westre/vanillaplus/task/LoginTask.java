package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.MySQL;

public class LoginTask extends BukkitRunnable {
	
	private User player;
	
	public LoginTask(User player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		MySQL mysql = new MySQL();
		boolean exists = false;
		PreparedStatement ps;
		
		int playerId = -1;
		int adminId = -1;
		int buildStatus = 0;
		int tempBankAccountValue = 0;
		int minutesPlayed = 0;
		int levelId = 0;
		
		try {
			// this query gets the player data and the next level required data
			ps = mysql.getConnection().prepareStatement("SELECT * FROM user WHERE name = '" + player.getPlayer().getName() + "'");
			ResultSet rs = ps.executeQuery();

			// Login the player here
			while(rs.next()) {
				playerId = rs.getInt("id");
				buildStatus = rs.getInt("buildstatus");
				tempBankAccountValue = rs.getInt("tempbankaccount");
				adminId = rs.getInt("adminid");
				minutesPlayed = rs.getInt("minutesplayed");
				levelId = rs.getInt("levelid");
				exists = true;
			}
			
			rs.close();
			ps.close();
			
			player.setUserId(playerId);
			player.setBuildStatus(buildStatus);
			player.setTempBankAccountValue(tempBankAccountValue);
			player.setMinutesPlayed(minutesPlayed);
			player.setLevelId(levelId);
			
			if(adminId > 0) {
				player.registerAsAdmin();
				player.getAdminUser().setAdminId(adminId);
			}
		}
		catch (SQLException ex) {
			System.out.println("REFRESH DATA ERROR! " + ex.getMessage());
		}
		
		// Register the player here
		if(!exists) {
			try {
				ps = mysql.getConnection().prepareStatement("INSERT INTO User (name, joined, lastconnected, buildstatus, tempbankaccount, minutesplayed, levelid, adminid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				ps.setString(1, player.getPlayer().getName());
				ps.setLong(2, (long)(System.currentTimeMillis() / 1000L));
				ps.setLong(3, (long)(System.currentTimeMillis() / 1000L));
				ps.setInt(4, 0);
				ps.setInt(5, 0);
				ps.setInt(6, 0);
				ps.setInt(7, 1);
				ps.setInt(8, 0);
				ps.executeUpdate();
				
				ResultSet rskey = ps.getGeneratedKeys();	
				int lastInsertedId = -1;
				if (rskey != null && rskey.next()) {
					lastInsertedId = rskey.getInt(1);
				}
				
				player.setUserId(lastInsertedId);	
				player.setBuildStatus(0);
				player.setTempBankAccountValue(0);
				player.setMinutesPlayed(0);
				player.setLevelId(1);
				ps.close();
				
				player.getPlayer().sendMessage("You have been registered, welcome!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else {
			player.getPlayer().sendMessage("Welcome back!");
		}
		
		mysql.close();
	}
	
}
