
package com.panda.decay.ref;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import com.panda.decay.Decay;
import com.panda.decay.DecayEntry;
import com.panda.decay.DecayRegistry;
import com.panda.decay.config.DecayConfig;
import com.panda.decay.config.DecayProperties;


public class DecayHandler
{
	public static ItemStack doRot(World world, ItemStack item)
	{//System.out.println("doing rot");
		if(DecayRegistry.Contains(item) ){
			if(item.hasTagCompound()){
				ItemStack out = DecayRegistry.getProperties(item).itemOut;
				
				double rotDate = item.getTagCompound().getLong("DECAY_START");
				double rotTime = item.getTagCompound().getLong("DECAY_PERIOD");
				System.out.println("Rotting: " + item.getDisplayName() +":"+rotDate+","+rotTime);
				System.out.println(world.getTotalWorldTime());
				if(rotDate+rotTime <= world.getTotalWorldTime()){
					System.out.println("Item Expired, rotting to " +out.getItem());
					out.stackSize = item.stackSize;
					return out;
				}
			}else{
				//make tag compound
				
				
				DecayEntry rotProps = DecayRegistry.getProperties(item);
				int rotTime = rotProps.ticks;
				
				
				long Date = world.getTotalWorldTime();
				
				if(Date >= 0)
				{

					item.setTagCompound(new NBTTagCompound());
	
					item.getTagCompound().setLong("DECAY_START", Date);
					item.getTagCompound().setLong("DECAY_PERIOD", rotTime);
				}
				
				
			}
			

			
		}
		return item;
		/*
		DecayProperties rotProps = null;
		long rotTime = (long)(DecayConfig.foodRotTime * 24000L);
		
		if(DecayConfig.decayProperties.containsKey("" + Item.REGISTRY.getNameForObject(item.getItem())))
		{
			rotProps = DecayConfig.decayProperties.get("" + Item.REGISTRY.getNameForObject(item.getItem()));
			rotTime = (long)(rotProps.days * 24000L);
		} else if(DecayConfig.decayProperties.containsKey("" + Item.REGISTRY.getNameForObject(item.getItem()) + "," + item.getItemDamage()))
		{
			rotProps = DecayConfig.decayProperties.get("" + Item.REGISTRY.getNameForObject(item.getItem()) + "," + item.getItemDamage());
			rotTime = (long)(rotProps.days * 24000L);
		}
		
		if( (rotProps == null && !(item.getItem() instanceof ItemFood)) || rotTime < 0)
		{
			if(item.getTagCompound() != null)
			{
				if(item.getTagCompound().hasKey("DECAY_START"))
				{
					item.getTagCompound().removeTag("DECAY_START");
				}
				if(item.getTagCompound().hasKey("DECAY_PERIOD"))
				{
					item.getTagCompound().removeTag("DECAY_PERIOD");
				}
			}
			return item;
		} else
		{
			if(item.getTagCompound() == null)
			{
				item.setTagCompound(new NBTTagCompound());
			}
			long UBD = item.getTagCompound().getLong("DECAY_PERIOD");
			
			if(UBD == 0)
			{
				UBD = (world.getTotalWorldTime()/24000L) * 24000L;
				UBD = UBD <= 0L? 1L : UBD;
				item.getTagCompound().setLong("DECAY_START", UBD);
				item.getTagCompound().setLong("DECAY_PERIOD", rotTime);
				return item;
			} else if(UBD + rotTime < world.getTotalWorldTime())
			{
				if(rotProps == null)
				{
					//return new ItemStack(ObjectHandler.rottenFood, item.stackSize);
				} else
				{
					//return Item.REGISTRY.getObject(rotProps.rotID) == null? null : new ItemStack((Item)Item.REGISTRY.getObject(rotProps.rotID), item.stackSize, rotProps.rotMeta < 0? item.getItemDamage() : rotProps.rotMeta);
				}
			} else
			{
				item.getTagCompound().setLong("DECAY_PERIOD", rotTime);
				return item;
			}
		}*/
	}
	
	public static void rotInvo(World world, IInventory inventory)
	{
		if(inventory == null || inventory.getSizeInventory() <= 0 || world.isRemote)
		{
			return;
		}
		
		boolean flag = false;
		
		try
		{
			for(int i = 0; i < inventory.getSizeInventory(); i++)
			{
				ItemStack slotItem = inventory.getStackInSlot(i);
				
				if(slotItem != null )
				{
					ItemStack rotItem = doRot(world, slotItem);
					
					if(rotItem == null || rotItem.getItem() != slotItem.getItem())
					{
						inventory.setInventorySlotContents(i, rotItem);
						flag = true;
					}
				}
			}
			
			if(flag && inventory instanceof TileEntity)
			{
				((TileEntity)inventory).markDirty();
			}
		} catch(Exception e)
		{
			Decay.log.log(Level.ERROR, "An error occured while attempting to decay inventory:", e);
			return;
		}
	}
	
}
