package com.westre.vanillaplus.economy;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.CustomItemStack;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.task.NewBankAccountTask;

public class SignListener implements Listener {

	private Main plugin;
	
	public SignListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {		
		Player player = event.getPlayer();
	    Block b = event.getClickedBlock();
	    User user = User.getPlayer(player);
	    Action action = event.getAction();
	    
	    if(action == Action.RIGHT_CLICK_BLOCK && b != null && (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			
			if(sign.getLine(0).equals("[OPEN]") && sign.getLine(1).equals("[BANK]") && sign.getLine(2).equals("[ACCOUNT]")) {
				new NewBankAccountTask(plugin, user).runTaskAsynchronously(plugin);
			}	
			else if(sign.getLine(0).equals("[BUY]") && sign.getLine(1).equals("[LANDMARKS]")) {
				player.getInventory().addItem(new CustomItemStack(Material.TORCH, 2, "§9Landmarks", "Used to place 2 points to make a region.").getItemStack());
				player.sendMessage("You have received 2 landmarks. Go on, make a cuboid.");	
			}
		}
	}
	
	@EventHandler
    public void onSignChange(SignChangeEvent sign) {
		Player player = sign.getPlayer();
		User user = User.getPlayer(player);
		
        if(sign.getLine(0).equals("[BANK]")) {
        	if(user.getAdminUser().getAdminId() == 5 || user.getAdminUser().getAdminId() == 6)
        		player.sendMessage("You have created a new bank.");
        	else
            	sign.setLine(0, "Not for you");
        }

        if(sign.getLine(0).equals("[MARKET]") && sign.getLine(1).equals("[EXPORT]") || sign.getLine(1).equals("[IMPORT]")) {
        	if(user.getAdminUser().getAdminId() == 5 || user.getAdminUser().getAdminId() == 6)
        		player.sendMessage("You have created a new marketplace.");
        	else {
            	sign.setLine(0, "Not for you");
            	sign.setLine(1, "Not for you");
        	}
        }
        
        if(sign.getLine(0).equals("[OPEN]") && sign.getLine(1).equals("[BANK]") && sign.getLine(2).equals("[ACCOUNT]")) {
        	if(user.getAdminUser().getAdminId() == 5 || user.getAdminUser().getAdminId() == 6) {
        		player.sendMessage("You have created a new open up a new bank account.");
        	}
	        else {
	        	sign.setLine(0, "Not for you");
	        	sign.setLine(1, "Not for you");
	        	sign.setLine(2, "Not for you");
	        }
        }
        
        if(sign.getLine(0).equals("[BUY]") && sign.getLine(1).equals("[LANDMARKS]")) {
        	if(user.getAdminUser().getAdminId() == 5 || user.getAdminUser().getAdminId() == 6) {
        		player.sendMessage("You have created a new static shop for landmarks.");
        		sign.setLine(2, "Base Price: 100 TPN");
        	}
	        else {
	        	sign.setLine(0, "Not for you");
	        	sign.setLine(1, "Not for you");
	        	sign.setLine(2, "Not for you");
	        }
        }
	}
}
