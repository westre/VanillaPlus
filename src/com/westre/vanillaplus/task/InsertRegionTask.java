package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;
import com.westre.vanillaplus.region.Region;

public class InsertRegionTask extends BukkitRunnable {
	
	private String name, description, rank, playername;
	private int minX, minY, minZ, maxX, maxY, maxZ, userId;	
	private Region region;
	
	public InsertRegionTask(String name, String description, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int userId, String playername, Region region) {
		this.name = name;
		this.description = description;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.playername = playername;
		this.userId = userId;
		this.rank = "Administrator"; // 1st member always admin
		this.region = region;
	}
	
	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO region (regionname, regiondescription, minx, miny, minz, maxx, maxy, maxz) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, this.name);
			ps.setString(2, this.description);
			ps.setInt(3, this.minX);
			ps.setInt(4, this.minY);
			ps.setInt(5, this.minZ);
			ps.setInt(6, this.maxX);
			ps.setInt(7, this.maxY);
			ps.setInt(8, this.maxZ);
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()) {
			    int regionId = rs.getInt(1);
			    region.setId(regionId);
			    
			    PreparedStatement ps2 = mysql.getConnection().prepareStatement("INSERT INTO regionresidents (regionid, userid, name, rank) VALUES (?, ?, ?, ?)");
				
				ps2.setInt(1, regionId);
				ps2.setInt(2, this.userId);
				ps2.setString(3, this.playername);
				ps2.setString(4, this.rank);

				ps2.executeUpdate();
				ps2.close();			    
			}
			
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
	}
}
