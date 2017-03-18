package com.panda.decay.helpers;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.panda.decay.Decay;
import com.panda.decay.DecayEntry;
import com.panda.decay.DecayRegistry;
import com.panda.decay.config.DecayProperties;
import com.panda.decay.ref.DecayHandler;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class EventsHandler
{
	
	
	@SubscribeEvent
	public void onEntityJoinWorld(PlayerContainerEvent.Open event){
		TextFormatting blue = TextFormatting.BLUE;
		
	}

	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{

			if(event.getEntity() instanceof EntityItem )
			{
				EntityItem item = (EntityItem)event.getEntity();
				ItemStack rotStack = DecayHandler.doRot(event.getWorld(), item.getEntityItem());
				System.out.println("entity joined world");
				if(item.getEntityItem() != rotStack)
				{
					item.setEntityItemStack(rotStack);
				}
			} else if(event.getEntity() instanceof EntityPlayer)
			{
				IInventory invo = ((EntityPlayer)event.getEntity()).inventory;
				DecayHandler.rotInvo(event.getWorld(), invo);
			} else if(event.getEntity() instanceof IInventory)
			{
				IInventory invo = (IInventory)event.getEntity();
				DecayHandler.rotInvo(event.getWorld(), invo);
			}
		
	}
	
	
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		ItemStack item = event.getEntityPlayer().getHeldItem(event.getHand());
		

			TileEntity tile = event.getEntityPlayer().worldObj.getTileEntity(event.getPos());
			
		if(tile != null & tile instanceof IInventory)
		{
			DecayHandler.rotInvo(event.getEntityPlayer().worldObj, (IInventory)tile);
		}

	}
	
	@SubscribeEvent
	public void onEntityInteract(EntityInteract event)
	{
		
		if(event.getTarget() != null && event.getTarget() instanceof IInventory )
		{
			IInventory chest = (IInventory)event.getTarget();
			
			DecayHandler.rotInvo(event.getEntityPlayer().worldObj, chest);
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{

		if(event.getEntityLiving() instanceof EntityPlayer && (event.getEntityLiving().worldObj.getTotalWorldTime()%20 == 0))
		{
			InventoryPlayer invo = (InventoryPlayer)((EntityPlayer)event.getEntityLiving()).inventory;
				DecayHandler.rotInvo(event.getEntityLiving().worldObj, invo);
		}
	}
	
	

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if(event.getItemStack() != null && event.getItemStack().hasTagCompound())
		{

			
			if(event.getItemStack().getTagCompound().getLong("DECAY_START") > 0 )
			{
				
				double rotDate = event.getItemStack().getTagCompound().getLong("DECAY_START");
				double rotTime = event.getItemStack().getTagCompound().getLong("DECAY_PERIOD");
				double curTime = event.getEntity().worldObj.getTotalWorldTime();
				
				//System.out.println("is "+((rotDate+rotTime) >= curTime));
				if((Double)(rotDate+rotTime) >= curTime)
				{
					//System.out.println(rotDate+rotTime);
					//System.out.println(curTime);
					event.getToolTip().add(((rotDate+rotTime)-curTime)+" Ticks left");
//					/new ChatComponentTranslation("misc.enviromine.tooltip.rot", "0%" , MathHelper.floor_double((curTime - rotDate)/24000L) , MathHelper.floor_double(rotTime/24000L)).getUnformattedText()
					//event.toolTip.add("Rotten: 0% (Day " + MathHelper.floor_double((curTime - rotDate)/24000L) + "/" + MathHelper.floor_double(rotTime/24000L) + ")");
					//event.toolTip.add("Use-By: Day " + MathHelper.floor_double((rotDate + rotTime)/24000L));
				} //else
				//{
				//	event.getToolTip().add(new ChatComponentTranslation("misc.enviromine.tooltip.rot", MathHelper.floor_double((curTime - rotDate)/rotTime * 100D) + "%", MathHelper.floor_double((curTime - rotDate)/24000L), MathHelper.floor_double(rotTime/24000L)).getUnformattedText());
					//event.toolTip.add("Use-By: Day " + MathHelper.floor_double((rotDate + rotTime)/24000L));
				//}
			}
		}
	}
	
	
	@SubscribeEvent
	public void onSmelted(ItemSmeltedEvent event) // Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
	{
		//event.crafting.setTagCompound(new NBTTagCompound());
		//event.crafting.getTagCompound().setLong("DECAY_START", event.player.worldObj.getTotalWorldTime());
		
		
		
		if(event.player.worldObj.isRemote || event.smelting == null || event.smelting.getItem() == null)
		{
			
			return;
		}
		
		DecayEntry rotProps = null;
		long rotTime = 0;

		if(DecayRegistry.Contains(event.smelting)) 
		{
			
			rotProps = DecayRegistry.getProperties(event.smelting);
			System.out.println(rotProps);
			rotTime = rotProps.ticks;
			
		}else{
			return;
		}

		long lowestDate = event.player.worldObj.getTotalWorldTime();
		
		if(lowestDate >= 0)
		{
			
			if(event.smelting.getTagCompound() == null)
			{
				event.smelting.setTagCompound(new NBTTagCompound());
			}
			
			event.smelting.getTagCompound().setLong("DECAY_START", lowestDate);
			event.smelting.getTagCompound().setLong("DECAY_PERIOD", rotTime);
		}
		
	}
	
	
	@SubscribeEvent
	public void onCrafted(ItemCraftedEvent event) // Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
	{
		//event.crafting.setTagCompound(new NBTTagCompound());
		//event.crafting.getTagCompound().setLong("DECAY_START", event.player.worldObj.getTotalWorldTime());
		
		
		
		if(event.player.worldObj.isRemote || event.crafting == null || event.crafting.getItem() == null)
		{
			
			return;
		}
		
		DecayEntry rotProps = null;
		long rotTime = 0;
		//System.out.println(event.crafting);
		//System.out.println(DecayRegistry.Contains(event.crafting));
		
		if(DecayRegistry.Contains(event.crafting)) 
		{
			
			rotProps = DecayRegistry.getProperties(event.crafting);
			System.out.println(rotProps);
			rotTime = rotProps.ticks;
			
		}else{
			return;
		}
			

		//	rotProps = EM_Settings.rotProperties.get("" + Item.REGISTRY.getNameForObject(event.crafting.getItem()));
		//	rotTime = (long)(rotProps.days * 24000L);
		//} else if(EM_Settings.rotProperties.containsKey("" + Item.REGISTRY.getNameForObject(event.crafting.getItem()) + "," + event.crafting.getItemDamage()))
		//{
		//	rotProps = EM_Settings.rotProperties.get("" + Item.REGISTRY.getNameForObject(event.crafting.getItem()) + "," + event.crafting.getItemDamage());
		//	rotTime = (long)(rotProps.days * 24000L);
		//}

		long lowestDate = event.player.worldObj.getTotalWorldTime();
		
		for(int i = 0; i < event.craftMatrix.getSizeInventory(); i++)
		{
			ItemStack stack = event.craftMatrix.getStackInSlot(i);
			
			if(stack == null || stack.getItem() == null || stack.getTagCompound() == null)
			{

				continue;
			}
			
			if(stack.getTagCompound().hasKey("DECAY_START") && (lowestDate < 0 || stack.getTagCompound().getLong("DECAY_START") < lowestDate))
			{
				lowestDate = stack.getTagCompound().getLong("DECAY_START");
			}
		}
		
		if(lowestDate >= 0)
		{
			
			if(event.crafting.getTagCompound() == null)
			{
				System.out.println("doot");
				event.crafting.setTagCompound(new NBTTagCompound());
			}
			
			event.crafting.getTagCompound().setLong("DECAY_START", lowestDate);
			event.crafting.getTagCompound().setLong("DECAY_PERIOD", rotTime);
		}
		
	}
}