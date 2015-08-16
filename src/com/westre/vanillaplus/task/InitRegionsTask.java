package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;
import com.westre.vanillaplus.region.Region;
import com.westre.vanillaplus.region.RegionManager;

public class InitRegionsTask extends BukkitRunnable {

	public void run() {
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT * FROM region");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Region region = new Region();
				
				Location location = new Location(Bukkit.getWorlds().get(0), rs.getInt("minx"), rs.getInt("miny"), rs.getInt("minz"));			
				region.setPointA(location);
				
				location = new Location(Bukkit.getWorlds().get(0), rs.getInt("maxx"), rs.getInt("maxy"), rs.getInt("maxz"));	
				region.setPointB(location);
				
				region.setName(rs.getString("regionname"));
				region.setId(rs.getInt("regionid"));
				region.setDescription(rs.getString("regiondescription"));
				
				region.createZone();
				
				PreparedStatement ps2 = mysql.getConnection().prepareStatement("SELECT * FROM regionresidents WHERE regionid = " + rs.getInt("regionid"));
				ResultSet rs2 = ps2.executeQuery();
				
				ArrayList<String> adminRoster = region.getAdministratorRoster();
				ArrayList<String> modRoster = region.getModeratorRoster();
				ArrayList<String> residentRoster = region.getResidentRoster();
            	
				while(rs2.next()) {
					if(rs2.getString("rank").contains("Administrator")) {
						adminRoster.add(rs2.getString("name"));
					}
					else if(rs2.getString("rank").contains("Moderator")) {
						modRoster.add(rs2.getString("name"));
					}
					else {
						residentRoster.add(rs2.getString("name"));
					}				
				}
				
				region.setAdministratorRoster(adminRoster);
				region.setModeratorRoster(modRoster);
				region.setResidentRoster(residentRoster);
				
				rs2.close();
				ps2.close();
				
				int[] loc = region.getLocation();
            	RegionManager.addRegion(loc[0] + "," + loc[1] + "," + loc[2] + "," + loc[3] + "," + loc[4] + "," + loc[5], region);
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mysql.close();
	}
}
