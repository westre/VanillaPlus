package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.RegisteredBankAccounts;
import com.westre.vanillaplus.entity.RegisteredBlock;
import com.westre.vanillaplus.misc.MySQL;

public class InitDataTask extends BukkitRunnable {

	public void run() {
		MySQL mysql = new MySQL();
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM block");
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				int[] location = { rs.getInt("x"), rs.getInt("y"), rs.getInt("z") };
				new RegisteredBlock(RegisteredBlock.BlockProperty.DATABASE_TO_LIST, rs.getInt("userid"), rs.getString("username"), rs.getLong("date"), rs.getString("type"), location, rs.getString("world"), rs.getInt("private"), rs.getInt("state"));
				
				if(rs.getInt("blockid") % 10 == 0) {
					System.out.println("Block id: " + rs.getInt("blockid"));
				}
			}
			
			rs.close();
			ps.close();
			
			ps = mysql.getConnection().prepareStatement("SELECT * FROM bank");
			rs = ps.executeQuery();
			while(rs.next()) {
				RegisteredBankAccounts.addBankAccount(rs.getInt("bankid"), rs.getInt("money"));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mysql.close();
		// cancel the thread after it is done
		cancel();
	}

}
