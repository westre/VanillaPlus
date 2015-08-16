package com.westre.vanillaplus.protection;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.westre.vanillaplus.main.Main;
import com.westre.vanillaplus.entity.BlockManager;
import com.westre.vanillaplus.entity.RegisteredBlock;
import com.westre.vanillaplus.entity.User;
import com.westre.vanillaplus.region.Region;
import com.westre.vanillaplus.region.RegionManager;
import com.westre.vanillaplus.task.DelBlockQueueTask;
import com.westre.vanillaplus.task.UpdateInventoryStateTask;

public class BlockListener implements Listener {
	
	private Main plugin;
	
	private Material[] excludedBlocks = { 
		Material.TNT,
		Material.PISTON_BASE,
		Material.SAND,
		Material.GRAVEL,
		Material.ANVIL,
		Material.DRAGON_EGG,
		Material.FLINT_AND_STEEL,
		Material.LAVA,
		Material.LAVA_BUCKET,
		Material.WATER,
		Material.WATER_BUCKET
	};
	
	private Material[] inventoryBlocks = { 
		Material.CHEST,
		Material.ENDER_CHEST,
		Material.FURNACE,
		Material.ANVIL
	};
	
	public BlockListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		User user = User.getPlayer(player);
		
		boolean found = false;
		
		for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
		    String key = entry.getKey();
		    String[] coordinate = key.split(",");
		    
		    if(Region.isEntityInside(e.getBlockPlaced().getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
		    	player.sendMessage("This is a region block, will not be saved to database.");
		    	
		    	if(e.getItemInHand().getType() == Material.SPONGE) {
					ItemStack torch = e.getItemInHand();
					ItemMeta torchMeta = torch.getItemMeta();
					
					if(torchMeta.getDisplayName().contains("Information Block")) {
						Region region = entry.getValue();
						
						if(region.getAdministratorRoster().contains(player.getName())) {
							player.sendMessage("Admin action successful");
						}
						else {
							e.setCancelled(true);
						}
					}
					player.sendMessage(torchMeta.getDisplayName());
				}
		    	
		    	found = true;
		    	break;
		    }
		}
		
		boolean isProtected = true;
		boolean isInventory = false;
		
		for(Material inventoryBlock : inventoryBlocks) {
			if(inventoryBlock == e.getBlock().getType()) {
				player.sendMessage("This block is an inventory.");
				isInventory = true;
				
				Block block = e.getBlock();
				boolean isOwner = false;
				
				long timestamp = (long)(System.currentTimeMillis() / 1000L);
				String type = e.getBlock().getType().toString();
				int[] location = { e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ() };
				World world = player.getWorld();
				Block[] possibleBlocks = {
					block.getRelative(BlockFace.NORTH),
					block.getRelative(BlockFace.EAST),
					block.getRelative(BlockFace.SOUTH),
					block.getRelative(BlockFace.WEST)
				};				
				
				for(Block checkBlock : possibleBlocks) {
					if(checkBlock.getTypeId() == Material.CHEST.getId()) {
						isOwner = checkInventoryOwner(player, checkBlock.getX(), checkBlock.getY(), checkBlock.getZ());
						if(isOwner) {
							new RegisteredBlock(RegisteredBlock.BlockProperty.LIST_TO_DATABASE, user.getUserId(), player.getName(), timestamp, type, location, world.getEnvironment().toString(), user.getBuildStatus(), 0);
						}
						else {
							player.sendMessage("You are not the owner.");
							e.setCancelled(true);
						}	
					}
				}
					
				if(!isOwner) new RegisteredBlock(RegisteredBlock.BlockProperty.LIST_TO_DATABASE, user.getUserId(), player.getName(), timestamp, type, location, world.getEnvironment().toString(), user.getBuildStatus(), 0);
				break;
			}
			
			if(!found) {		
				for(Material excludedBlock : excludedBlocks) {
					if(excludedBlock == e.getBlock().getType()) {
						player.sendMessage("This block will not be protected due not being in a region.");
						isProtected = false;
						break;
					}
				}
				
				if(isProtected && !isInventory) {
					long timestamp = (long)(System.currentTimeMillis() / 1000L);
					String type = e.getBlock().getType().toString();
					int[] location = { e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ() };
					World world = player.getWorld();

					new RegisteredBlock(RegisteredBlock.BlockProperty.LIST_TO_DATABASE, user.getUserId(), player.getName(), timestamp, type, location, world.getEnvironment().toString(), user.getBuildStatus(), -1);			
					player.sendMessage("This is a non-region block and will be saved to the database.");
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent e) {
		Player player = e.getPlayer();
		User user = User.getPlayer(player);
		
		boolean found = false;
		
		for(Map.Entry<String, Region> entry : RegionManager.getRegions().entrySet()) {
		    String key = entry.getKey();
		    String[] coordinate = key.split(",");
		    
		    if(Region.isEntityInside(e.getBlock().getLocation(), Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), Integer.parseInt(coordinate[2]), Integer.parseInt(coordinate[3]), Integer.parseInt(coordinate[4]), Integer.parseInt(coordinate[5]))) {
		    	found = true;
		    	break;
		    }
		}
		
		if(!found) {
			// delete market chests
			if(Material.WALL_SIGN == e.getBlock().getType()) {
				Block block = e.getBlock();
				
				Block[] possibleBlocks = {
					block.getRelative(BlockFace.NORTH),
					block.getRelative(BlockFace.EAST),
					block.getRelative(BlockFace.SOUTH),
					block.getRelative(BlockFace.WEST)
				};				
				
				for(Block checkBlock : possibleBlocks) {
					if(checkBlock.getType() == Material.CHEST) {
						Sign sign = (Sign) e.getBlock().getState();
						
						if(sign.getLine(0).equals("[MARKET]") && sign.getLine(1).equals("[IMPORT]") || sign.getLine(1).equals("[EXPORT]")) {
							player.sendMessage("Please delete the chest first.");
							e.setCancelled(true);
						}
					}
				}			
			}
			
			int[] blockLocation = { e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ() };
			BlockManager.AttemptedRemoval executed = BlockManager.removeBlock(player, blockLocation);
			
			if(executed == BlockManager.AttemptedRemoval.SUCCEEDED) {
				if(!DelBlockQueueTask.processingMainQueue)
					DelBlockQueueTask.queue(blockLocation);
				else
					DelBlockQueueTask.secondaryQueue(blockLocation);
			}
			else if(executed == BlockManager.AttemptedRemoval.NO_PERMISSION && user.getAdminUser().getAdminId() >= 3) { // admin
				player.sendMessage("Admin overriding this block.");
			}
			else if(executed == BlockManager.AttemptedRemoval.NO_PERMISSION) {
				e.setCancelled(true);
			}
		}	
	}
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent e) {
		Iterator<Block> blockList = e.blockList().iterator();
		
		while(blockList.hasNext()) {
			Block block = blockList.next();	
			World world = block.getWorld();
			
			int[] location = { block.getX(), block.getY(), block.getZ() };
			
			RegisteredBlock blockExists = BlockManager.getBlock(world, location);

			if(blockExists != null) {
				blockList.remove();
			}
		}
		//if(e.getEntity() instanceof TNTPrimed == false)
		//e.setCancelled(true); // for now
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		World world = event.getBlock().getWorld();
		int[] location = { event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ() };
		
		RegisteredBlock blockExists = BlockManager.getBlock(world, location);

		if(blockExists != null) {
			event.setCancelled(true);
		}
    }
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event){
		World world = event.getBlock().getWorld();
		int[] location = { event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ() };
		
		RegisteredBlock blockExists = BlockManager.getBlock(world, location);

		if(blockExists != null) {
			event.setCancelled(true);
		}
    }
	
	@EventHandler
    public void onPlayerClickEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Action action = e.getAction();
		User user = User.getPlayer(player);
		
		if(e.getClickedBlock() != null) {
			int[] location = { e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ() };
			RegisteredBlock block = BlockManager.getBlock(player, location);
			
			if(player.getItemInHand().getType().name().toLowerCase().equals("stick")) {		
				if(block != null) {
					if(action == Action.RIGHT_CLICK_BLOCK) {
						player.sendMessage(ChatColor.RED + "---------------------------------------");
						player.sendMessage(ChatColor.GRAY + "Owner: " + ChatColor.WHITE + block.getUsername());

						if(!block.isPrivate()) {
							player.sendMessage(ChatColor.GRAY + "Build Code: " + ChatColor.GREEN + "Public");
						}
						else {
							player.sendMessage(ChatColor.GRAY + "Build Code: " + ChatColor.RED + "Private");
						}
						
						if(block.getState() == 1) {
							player.sendMessage(ChatColor.GRAY + "Inv Code: " + ChatColor.GREEN + "Public");
						}
						else if(block.getState() == 0) {
							player.sendMessage(ChatColor.GRAY + "Inv Code: " + ChatColor.RED + "Private");
						}
						else if(block.getState() == -1) {
							player.sendMessage(ChatColor.GRAY + "Inv Code: " + ChatColor.GRAY + "Not an inventory");
						}
						
						player.sendMessage(ChatColor.RED + "---------------------------------------");
					}
					else if(action == Action.LEFT_CLICK_BLOCK && block.getState() >= 0) {
						if(block.getState() == 0 && block.getUserid() == user.getUserId()) {
							block.setState(1);
							new UpdateInventoryStateTask(1, block.getLocation()).runTaskAsynchronously(plugin);
							player.sendMessage("This inventory is now public.");
						}
						else if(block.getState() == 1 && block.getUserid() == user.getUserId()) {
							block.setState(0);
							new UpdateInventoryStateTask(0, block.getLocation()).runTaskAsynchronously(plugin);
							player.sendMessage("This inventory is now private.");
						}
					}
				}
			}
			else {
				if(block != null && block.getState() >= 0) {
					if(action == Action.RIGHT_CLICK_BLOCK) {
						// IS FULL OWNER
						if(block.getUserid() == user.getUserId()) {
							//player.sendMessage("Welcome.");
						}
						// PRIVATE INVENTORY, IS ADMIN
						else if(block.getState() == 0 && (player.isOp() || user.getAdminUser().getAdminId() >= 3)) {
							player.sendMessage("(ADMIN) This inventory is owned by: " + ChatColor.DARK_GREEN + block.getUsername() + ChatColor.YELLOW + ".");
						}
						// PUBLIC INVENTORY
						else if(block.getState() == 1) {
							player.sendMessage("This inventory is owned by: " + ChatColor.DARK_GREEN + block.getUsername() + ChatColor.YELLOW + " and it is " + ChatColor.GREEN + "unlocked.");
						}
						// PRIVATE INVENTORY
						else if(block.getState() == 0) {
							player.sendMessage("This inventory is owned by: " + ChatColor.DARK_GREEN + block.getUsername() + ChatColor.YELLOW + " and it is " + ChatColor.RED + "locked.");
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	private boolean checkInventoryOwner(Player player, int x, int y, int z) {
		int[] location = {x, y, z};
		RegisteredBlock block = BlockManager.getBlock(player, location);
		User user = User.getPlayer(player);
		World world = player.getWorld();
		
		// double chest shop protection, sry for invading ):
		BlockFace[] blockFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
		for(BlockFace blockFace : blockFaces) {
			if(world.getBlockAt(x, y, z).getRelative(blockFace).getLocation().getBlock().getType() == Material.WALL_SIGN) {	
				Sign sign = (Sign) world.getBlockAt(x, y, z).getRelative(blockFace).getLocation().getBlock().getState();
				String line1 = sign.getLine(0);
				String line2 = sign.getLine(1);
				
				if(line1.equals("[BANK]") || (line1.equals("[MARKET]") && (line2.equals("[IMPORT]") || line2.equals("[EXPORT]")) || line1.equals("[SHOP]"))) {
					player.sendMessage("I'm afraid I can't let you do that.");
					return false;
				}			
				break;
			}
		}
		
		if(block != null && block.getState() >= 0 && block.getUserid() == user.getUserId()) {
			return true;
		}
		return false;
	}
}
