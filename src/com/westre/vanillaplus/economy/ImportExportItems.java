package com.westre.vanillaplus.economy;

import org.bukkit.Material;


public class ImportExportItems {
	
	// only valid for global public import/export chests

	private static ImportExportMaterial blockOfDiamond = new ImportExportMaterial(Material.DIAMOND_BLOCK, 10000);
	private static ImportExportMaterial blockOfEmerald = new ImportExportMaterial(Material.EMERALD_BLOCK, 9000);
	private static ImportExportMaterial blockOfGold = new ImportExportMaterial(Material.GOLD_BLOCK, 5000);
	private static ImportExportMaterial blockOfIron = new ImportExportMaterial(Material.IRON_BLOCK, 6500);
	private static ImportExportMaterial blockOfLapis = new ImportExportMaterial(Material.LAPIS_BLOCK, 250);
	private static ImportExportMaterial blockOfQuartz = new ImportExportMaterial(Material.QUARTZ_BLOCK, 3000);
	private static ImportExportMaterial blockOfRedstone = new ImportExportMaterial(Material.REDSTONE_BLOCK, 200);
	
	public static ImportExportMaterial[] importExportItems = { blockOfDiamond, blockOfEmerald, blockOfGold, blockOfIron, blockOfLapis, blockOfQuartz, blockOfRedstone };
		
	public enum ChestType {
		LOCAL_COMMERCIAL_IMPORT_EXPORT,
		GLOBAL_PUBLIC_IMPORT,
		GLOBAL_PUBLIC_EXPORT,
	}
	
	public static Material[] getItems() {
		Material[] importExportMaterialItems = new Material[importExportItems.length];
		for(int index = 0; index < importExportItems.length; index++) {
			importExportMaterialItems[index] = importExportItems[index].getMaterial();
		}
		return importExportMaterialItems;
	}
	
	public static ImportExportMaterial[] getImportExportItems() {
		return importExportItems;
	}
	
	public static void refreshData() {

		/*// TODO other thread
		Bukkit.broadcastMessage("Refreshing data");
		MySQL mysql = new MySQL();
		
		ImportExportMaterial referenceMaterial = null;
		
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT materialname, materialsupply, materialdemand FROM importexport");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				if(rs.getString("materialname").equals(wood.getMaterial().toString()))
					referenceMaterial = wood;
				else if(rs.getString("materialname").equals(coal.getMaterial().toString()))
					referenceMaterial = coal;
				else if(rs.getString("materialname").equals(iron.getMaterial().toString()))
					referenceMaterial = iron;
				else if(rs.getString("materialname").equals(diamond.getMaterial().toString()))
					referenceMaterial = diamond;
				
				referenceMaterial.setDemand(rs.getInt("materialdemand"));
				referenceMaterial.setSupply(rs.getInt("materialsupply"));
				referenceMaterial.calculateCurrentPrice();
				//Bukkit.broadcastMessage(referenceMaterial.toString());
				//referenceMaterial.displayFormatted();
			}
			
			rs.close();
			ps.close();
		}
		catch (SQLException ex) {
			System.out.println("REFRESH DATA ERROR! " + ex.getMessage());
		}
		
		
		mysql.close();*/
	}
}
