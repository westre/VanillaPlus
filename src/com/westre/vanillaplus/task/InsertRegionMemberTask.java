package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;

public class InsertRegionMemberTask extends BukkitRunnable {
	
	private String rank, name;
	private int regionid, userid;	
	
	public InsertRegionMemberTask(int regionid, int userid, String name, String rank) {
		this.regionid = regionid;
		this.userid = userid;
		this.rank = rank;
		this.name = name;
	}
	
	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO regionresidents (regionid, userid, name, rank) VALUES (?, ?, ?, ?)");
			
			ps.setInt(1, this.regionid);
			ps.setInt(2, this.userid);
			ps.setString(3, this.name);
			ps.setString(4, this.rank);

			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
	}
}
