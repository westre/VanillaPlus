package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.RegisteredBlock;
import com.westre.vanillaplus.misc.MySQL;

public class AddBlockQueueTask extends BukkitRunnable {
	
	private static ArrayList<RegisteredBlock> queue = new ArrayList<RegisteredBlock>();
	private static ArrayList<RegisteredBlock> secondaryQueue = new ArrayList<RegisteredBlock>();
	
	public static boolean processingMainQueue = false;
	
	public static void queue(RegisteredBlock block) {
		queue.add(block);
	}
	
	public static void secondaryQueue(RegisteredBlock block) {
		secondaryQueue.add(block);
	}
	
	public void run() {	
		processingMainQueue = true;
		MySQL mysql = new MySQL();
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("BEGIN");
			ps.executeQuery();
			ps.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		for(Iterator<RegisteredBlock> it = queue.iterator(); it.hasNext(); ) {
			RegisteredBlock block = it.next();
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO block (userid, username, date, type, x, y, z, world, private, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				int[] location = block.getLocation();
				
				int buildStatus = -1;
				if(block.isPrivate()) buildStatus = 0;
				else buildStatus = 1;
				
				ps.setInt(1, block.getUserid());
				ps.setString(2, block.getUsername());
				ps.setLong(3, (long)(System.currentTimeMillis() / 1000L));
				ps.setString(4, block.getType());
				ps.setInt(5, location[0]);
				ps.setInt(6, location[1]);
				ps.setInt(7, location[2]);
				ps.setString(8, block.getWorld());
				ps.setInt(9, buildStatus);
				ps.setInt(10, block.getState());
				ps.executeUpdate();			
				ps.close();
				
				it.remove();
			}
			catch (SQLException ex) {
				
			}	
		}
		
		processingMainQueue = false;
		
		for(Iterator<RegisteredBlock> it = secondaryQueue.iterator(); it.hasNext(); ) {
			RegisteredBlock block = it.next();
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO block (userid, username, date, type, x, y, z, world, private, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				int[] location = block.getLocation();
				
				int buildStatus = -1;
				if(block.isPrivate()) buildStatus = 0;
				else buildStatus = 1;
				
				ps.setInt(1, block.getUserid());
				ps.setString(2, block.getUsername());
				ps.setLong(3, (long)(System.currentTimeMillis() / 1000L));
				ps.setString(4, block.getType());
				ps.setInt(5, location[0]);
				ps.setInt(6, location[1]);
				ps.setInt(7, location[2]);
				ps.setString(8, block.getWorld());
				ps.setInt(9, buildStatus);
				ps.setInt(10, block.getState());
				ps.executeUpdate();			
				ps.close();
				
				it.remove();
			}
			catch (SQLException ex) {
				
			}	
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
