package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.MySQL;
import com.westre.vanillaplus.region.Region;

public class UpdateRegionTask extends BukkitRunnable {
	
	public static enum RegionUpdate {
		ADD_MEMBER,
		DEL_MEMBER,
		CHANGE_MEMBER_RANK,
		NAME,
		DESCRIPTION
	}
	
	private RegionUpdate regionUpdate;
	private User user;
	private String message;
	private Region region;
	
	public UpdateRegionTask(RegionUpdate regionUpdate, Region region, User user) {
		this.regionUpdate = regionUpdate;
		this.user = user;
		this.region = region;
	}
	
	public UpdateRegionTask(RegionUpdate regionUpdate, Region region, String message) {
		this.regionUpdate = regionUpdate;
		this.message = message;
		this.region = region;
	}
	
	public UpdateRegionTask(RegionUpdate regionUpdate, Region region, User user, String message) {
		this.regionUpdate = regionUpdate;
		this.user = user;
		this.region = region;
		this.message = message;
	}
	
	public void run() {
		if(regionUpdate == RegionUpdate.NAME) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE region SET regionname = ? WHERE regionid = ?");
				ps.setString(1, message);
				ps.setInt(2, region.getId());
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
		else if(regionUpdate == RegionUpdate.DESCRIPTION) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE region SET regiondescription = ? WHERE regionid = ?");
				ps.setString(1, message);
				ps.setInt(2, region.getId());
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
		else if(regionUpdate == RegionUpdate.ADD_MEMBER) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO regionresidents (regionid, userid, name, rank) VALUES (?, ?, ?, ?)");
				ps.setInt(1, region.getId());
				ps.setInt(2, user.getUserId());
				ps.setString(3, user.getPlayer().getName());
				ps.setString(4, "Resident");
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
		else if(regionUpdate == RegionUpdate.DEL_MEMBER) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM regionresidents WHERE regionid = ? AND userid = ?");
				ps.setInt(1, region.getId());
				ps.setInt(2, user.getUserId());
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
		else if(regionUpdate == RegionUpdate.CHANGE_MEMBER_RANK) {
			MySQL mysql = new MySQL();
			
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE regionresidents SET rank = ? WHERE userid = ? AND regionid = ?");
				ps.setString(1, message);
				ps.setInt(2, user.getUserId());
				ps.setInt(3, region.getId());
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			mysql.close();
		}
	}
}
