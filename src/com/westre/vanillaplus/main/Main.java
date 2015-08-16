package com.westre.vanillaplus.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.westre.vanillaplus.economy.ChestBank;
import com.westre.vanillaplus.economy.ChestMarket;
import com.westre.vanillaplus.economy.ChestShop;
import com.westre.vanillaplus.economy.ImportExportItems;
import com.westre.vanillaplus.economy.ImportExportMaterial;
import com.westre.vanillaplus.economy.SignListener;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.misc.Freetopia;
import com.westre.vanillaplus.misc.ItemDrop;
import com.westre.vanillaplus.misc.MySQL;
import com.westre.vanillaplus.protection.BlockListener;
import com.westre.vanillaplus.region.RegionListener;
import com.westre.vanillaplus.task.AddBlockQueueTask;
import com.westre.vanillaplus.task.DelBlockQueueTask;
import com.westre.vanillaplus.task.InitDataTask;
import com.westre.vanillaplus.task.InitRegionsTask;
import com.westre.vanillaplus.task.RefreshMarketTask;
import com.westre.vanillaplus.task.TimeTask;
import com.westre.vanillaplus.task.UpdateBankAccountsTask;
import com.westre.vanillaplus.task.UpdateTempBankAccountsTask;

public class Main extends JavaPlugin {
	
	public final BlockListener blockListener = new BlockListener(this);
	public final PlayerCommand commandListener = new PlayerCommand(this);
	public final AdminCommand adminListener = new AdminCommand(this);
	public final RegionCommand regionCmdListener = new RegionCommand(this);
	public final PlayerConnection connectionListener = new PlayerConnection(this);
	public final ChestShop chestShopListener = new ChestShop(this);
	public final ChestMarket chestMarketListener = new ChestMarket(this);
	public final ChestBank chestBankListener = new ChestBank();
	public final SignListener signPressListener = new SignListener(this);
	public final PlayerChat chatListener = new PlayerChat();
	public final ItemDrop itemDropListener = new ItemDrop();
	public final RegionListener regionListener = new RegionListener(this);
	
	private BukkitTask addBlockQueueTask, delBlockQueueTask, getBlocksTask, updateBankAccountsTask, updateTempBankAccountsTask, timeTask, refreshMarketTask, getRegionsTask;
	
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.connectionListener, this);
		pm.registerEvents(this.chestShopListener, this);
		pm.registerEvents(this.chestMarketListener, this);
		pm.registerEvents(this.signPressListener, this);
		pm.registerEvents(this.chestBankListener, this);
		pm.registerEvents(this.chatListener, this);
		pm.registerEvents(this.itemDropListener, this);
		pm.registerEvents(this.regionListener, this);
		
		getCommand("sell").setExecutor(commandListener);
		getCommand("unsell").setExecutor(commandListener);
		getCommand("money").setExecutor(commandListener);
		getCommand("bug").setExecutor(commandListener);
		getCommand("options").setExecutor(commandListener);
		
		getCommand("a").setExecutor(adminListener);
		getCommand("admin").setExecutor(adminListener);
		
		getCommand("r").setExecutor(regionCmdListener);
		getCommand("region").setExecutor(regionCmdListener);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinEvent(player, "Plugin reload"));
		}
	
		addBlockQueueTask = new AddBlockQueueTask().runTaskTimerAsynchronously(this, 50L, 50L); // 20L = 1sec
		delBlockQueueTask = new DelBlockQueueTask().runTaskTimerAsynchronously(this, 50L, 50L); // 20L = 1sec
		getBlocksTask = new InitDataTask().runTaskAsynchronously(this);
		updateBankAccountsTask = new UpdateBankAccountsTask().runTaskTimerAsynchronously(this, 50L, 50L);
		updateTempBankAccountsTask = new UpdateTempBankAccountsTask().runTaskTimerAsynchronously(this, 50L, 50L); 
		timeTask = new TimeTask().runTaskTimerAsynchronously(this, 0L, 20L);
		getRegionsTask = new InitRegionsTask().runTaskAsynchronously(this); 
		
		if(!Freetopia.marketClosed)
			refreshMarketTask = new RefreshMarketTask().runTaskTimerAsynchronously(this, 36000L, 36000L); // every half hour 36000L
		
		new RefreshMarketTask().runTaskAsynchronously(this); // will this give any errors?
		
		for(World world : Bukkit.getServer().getWorlds()){
            world.setTime(6000);
        }
		
		new BukkitRunnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					User user = User.getPlayer(player);
					user.setMinutesPlayed(user.getMinutesPlayed() + 1);
				}
			}			
		}.runTaskTimerAsynchronously(this, 1200L, 1200L);
		
		// init the data
		MySQL mysql = new MySQL();
		try {
			PreparedStatement ps = mysql.getConnection().prepareStatement("SELECT materialname, materialsupply, materialdemand FROM importexport");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				for(int index = 0; index < ImportExportItems.importExportItems.length; index++) {
					if(rs.getString("materialname").equals(ImportExportItems.importExportItems[index].getMaterial().toString())) {
						ImportExportMaterial referenceMaterial = ImportExportItems.importExportItems[index];
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
		
		
		//World world = Bukkit.getWorlds().get(0);
		//world.setSpawnLocation(-195, 66, 243);
	}
	
	@Override
	public void onDisable() {
		addBlockQueueTask.cancel();
		delBlockQueueTask.cancel();
		//Bukkit.getScheduler().cancelAllTasks();
		getBlocksTask.cancel();
		updateBankAccountsTask.cancel();
		updateTempBankAccountsTask.cancel();
		timeTask.cancel();
		refreshMarketTask.cancel();
		getRegionsTask.cancel();
	}
}
