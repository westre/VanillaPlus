package com.westre.vanillaplus.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.westre.vanillaplus.economy.ImportExportItems;
import com.westre.vanillaplus.economy.ImportExportMaterial;
import com.westre.vanillaplus.misc.MySQL;

public class RefreshMarketTask extends BukkitRunnable {

	@Override
	public void run() {
		
		MySQL mysql = new MySQL();
		// Make the refresh data init only upon startup, then add arraylist to hold the data of supply and demand so not constantly query to database
		ImportExportMaterial referenceMaterial = null;
						
		try {		
			for(int index = 0; index < ImportExportItems.importExportItems.length; index++) {
				PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO data (materialname, materialdate, materialprice, materialsupply) VALUES (?, ?, ?, ?)");
				
				referenceMaterial = ImportExportItems.importExportItems[index];							
				int supplyDemand = referenceMaterial.getSupply() - referenceMaterial.getDemand();
				
				java.util.Date dt = new java.util.Date();
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				ps.setString(1, referenceMaterial.getMaterial().toString());
				ps.setString(2, sdf.format(dt));
				ps.setInt(3, referenceMaterial.getCurrentPrice());
				ps.setInt(4, supplyDemand);
				ps.executeUpdate();
				ps.close();
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT materialname, materialsupply, materialdemand FROM importexport");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				for(int index = 0; index < ImportExportItems.importExportItems.length; index++) {
					if(rs.getString("materialname").equals(ImportExportItems.importExportItems[index].getMaterial().toString())) {
						referenceMaterial = ImportExportItems.importExportItems[index];
						referenceMaterial.setDemand(rs.getInt("materialdemand"));
						referenceMaterial.setSupply(rs.getInt("materialsupply"));
						referenceMaterial.calculateCurrentPrice();
					}			
				}
			}
			
			rs.close();
			ps.close();
		}
		catch (SQLException ex) {
			System.out.println("REFRESH DATA ERROR! " + ex.getMessage());
		}
		
		
		mysql.close();
	}

}
