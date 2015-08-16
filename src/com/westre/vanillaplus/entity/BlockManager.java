package com.westre.vanillaplus.entity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.westre.vanillaplus.task.AddBlockQueueTask;

public class BlockManager {
	
	private static HashMap<int[], RegisteredBlock> blockMap = new HashMap<int[], RegisteredBlock>();
	
	public static enum AttemptedRemoval {
		SUCCEEDED,
		NO_PERMISSION,
		NULL
	}
	
	public static void addBlock(RegisteredBlock.BlockProperty blockProperty, int[] location, RegisteredBlock pb) {
		blockMap.put(location, pb);
		
		if(blockProperty == RegisteredBlock.BlockProperty.LIST_TO_DATABASE) {
			if(!AddBlockQueueTask.processingMainQueue)
				AddBlockQueueTask.queue(pb);
			else
				AddBlockQueueTask.secondaryQueue(pb);
		}
	}

	public static RegisteredBlock getBlock(Player player, int[] location) {
		for(Map.Entry<int[], RegisteredBlock> entry : blockMap.entrySet()) {
			int[] blockLocation = entry.getKey();
		    
			if(location[0] == blockLocation[0] && location[1] == blockLocation[1] && location[2] == blockLocation[2]) {
				RegisteredBlock block = entry.getValue();
				World world = player.getWorld();
				
				if(world.getEnvironment().toString().equals(block.getWorld())) {
					return block;
				}
			}
		}			
		return null;
	}
	
	public static RegisteredBlock getBlock(World world, int[] location) {
		for(Map.Entry<int[], RegisteredBlock> entry : blockMap.entrySet()) {
			int[] blockLocation = entry.getKey();
		    
			if(location[0] == blockLocation[0] && location[1] == blockLocation[1] && location[2] == blockLocation[2]) {
				RegisteredBlock block = entry.getValue();
				
				if(world.getEnvironment().toString().equals(block.getWorld())) {
					return block;
				}
			}
		}			
		return null;
	}
	
	public static AttemptedRemoval removeBlock(Player player, int[] location) {
		for(Map.Entry<int[], RegisteredBlock> entry : blockMap.entrySet()) {
			int[] blockLocation = entry.getKey();
			RegisteredBlock block = entry.getValue();
			
			if(location[0] == blockLocation[0] && location[1] == blockLocation[1] && location[2] == blockLocation[2]) {
				World world = player.getWorld();
				block = entry.getValue();
				User user = User.getPlayer(player);
				if(world.getEnvironment().toString().equals(block.getWorld())) {
					if(!block.isPrivate()) {
						blockMap.remove(blockLocation);
						//player.sendMessage("Public block removed");
						return AttemptedRemoval.SUCCEEDED;
					}
					else if(block.isPrivate() && block.getUserid() == user.getUserId()) {
						blockMap.remove(blockLocation);
						//player.sendMessage("Private block removed");
						return AttemptedRemoval.SUCCEEDED;
					}
					else {
						return AttemptedRemoval.NO_PERMISSION;
					}
				}
			}
		}			
		return AttemptedRemoval.NULL;
	}
}
