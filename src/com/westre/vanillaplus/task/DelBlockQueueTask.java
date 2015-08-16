package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.misc.MySQL;

public class DelBlockQueueTask extends BukkitRunnable {
	
	private static ArrayList<int[]> queue = new ArrayList<int[]>();
	private static ArrayList<int[]> secondaryQueue = new ArrayList<int[]>();
	
	public static boolean processingMainQueue = false;
	
	public static void queue(int[] block) {
		queue.add(block);
	}
	
	public static void secondaryQueue(int[] block) {
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
		
		for(Iterator<int[]> it = queue.iterator(); it.hasNext(); ) {
			int[] block = it.next();
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM block WHERE x = " + block[0] + " AND y = " + block[1] + " AND z = " + block[2]);
				ps.execute();		
				it.remove();
			}
			catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}	
		}
		
		processingMainQueue = false;
		
		for(Iterator<int[]> it = secondaryQueue.iterator(); it.hasNext(); ) {
			int[] block = it.next();
			try {
				PreparedStatement ps = mysql.getConnection().prepareStatement("DELETE FROM block WHERE x = " + block[0] + " AND y = " + block[1] + " AND z = " + block[2]);
				ps.execute();		
				it.remove();
			}
			catch (SQLException ex) {
				System.out.println(ex.getMessage());
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
