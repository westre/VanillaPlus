package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.entity.RegisteredBankAccounts;
import com.westre.vanillaplus.misc.MySQL;

public class UpdateBankAccountsTask extends BukkitRunnable {
	
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
			Iterator<Entry<Integer, Integer>> entries = RegisteredBankAccounts.getAccounts().entrySet().iterator();
			while(entries.hasNext()) {
	            Map.Entry<Integer, Integer> entry = entries.next();
	            
	            int bankid = entry.getKey();
			    int money = entry.getValue();
			    
			    PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE bank SET money = ? WHERE bankid = ?");
				ps.setInt(1, money);
				ps.setInt(2, bankid);
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
