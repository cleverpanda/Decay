package com.panda.decay;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;


public class DecayRegistry {

	public static ArrayList<DecayEntry> decaylist = new ArrayList<DecayEntry>();

	public static void register(ItemStack in,ItemStack out, String type,Boolean tickup,int ticks)
	{
		DecayEntry entry = new DecayEntry(in,out, type, tickup, ticks);

		if(in != null && out != null && !Contains(in))
		{
			decaylist.add(entry);
		}else
		{
			Decay.log.error("An item was added to the Registry which was not an item");
			Decay.log.error(in.toString());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<DecayEntry> getRewards()
	{
		return decaylist;
	}
	
	public static DecayEntry getProperties(ItemStack item){

			Iterator<DecayEntry> it = decaylist.iterator();
			while(it.hasNext())
			{
				DecayEntry reward = it.next();

				if (reward.itemIn.getItem() == item.getItem())
				{
					return reward;
				}
			}
		
		return null;
	}



	public static boolean Contains(ItemStack stack)
	{
		Iterator<DecayEntry> it = decaylist.iterator();
		
		while(it.hasNext())
		{
			
			DecayEntry reward = it.next();
			//System.out.println(reward.itemIn+","+stack);
			if (reward.itemIn.getItem().equals(stack.getItem()))
			{
				System.out.println(true);
				return true;
			}
		}

		return false;
	}



	/*public static void registerConfigRarity(String configInput, Item item){
		String[] toStringArray = configInput.split(",");

		for(int i = 0; i < toStringArray.length; i++) 
		{
			try
			{
				int rarity = Integer.parseInt(toStringArray[i]); 
				//register(item, rarity, i, 0);
			}
			catch(NumberFormatException numberFormatException)  
			{
				numberFormatException.printStackTrace(); 
			}
		}	
	}

	public static void registerConfigRarity(String[] configInput, Item item, int meta){
		for(int i = 0; i < configInput.length; i++) 
		{
			try
			{
				int rarity = Integer.parseInt(configInput[i]); 
				//register(item, rarity, i, meta);
			}
			catch(NumberFormatException numberFormatException)  
			{
				numberFormatException.printStackTrace(); 
			}
		}
	}*/
}
