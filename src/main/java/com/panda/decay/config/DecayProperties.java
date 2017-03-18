package com.panda.decay.config;

import java.io.File;
import java.util.Iterator;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import com.panda.decay.Decay;
import com.panda.decay.helpers.PropertyBase;
import com.panda.decay.helpers.SerialisableProperty;


public class DecayProperties implements SerialisableProperty, PropertyBase
{
	public static final DecayProperties base = new DecayProperties();
	static String[] RPName;
	
	public String name;
	public int meta;
	public String rotID;
	public int rotMeta;
	public int days;
	public String loadedFrom;
	
	public DecayProperties(NBTTagCompound tags)
	{
		this.ReadFromNBT(tags);
	}
	
	public DecayProperties()
	{
		// THIS CONSTRUCTOR IS FOR STATIC PURPOSES ONLY!
		
		if(base != null && base != this)
		{
			throw new IllegalStateException();
		}
	}
	
	public DecayProperties(String name, int meta, String rotID, int rotMeta, int days, String fileName)
	{
		this.name = name;
		this.meta = meta;
		this.rotID = rotID;
		this.rotMeta = rotMeta;
		this.days = days;
		this.loadedFrom = fileName;
	}

	@Override
	public NBTTagCompound WriteToNBT()
	{
		NBTTagCompound tags = new NBTTagCompound();
		tags.setString("name", this.name);
		tags.setInteger("meta", this.meta);
		tags.setString("rotID", this.rotID);
		tags.setInteger("rotMeta", this.rotMeta);
		tags.setInteger("days", this.days);
		return tags;
	}

	@Override
	public void ReadFromNBT(NBTTagCompound tags)
	{
		this.name = tags.getString("name");
		this.meta = tags.getInteger("meta");
		this.rotID = tags.getString("rotID");
		this.rotMeta = tags.getInteger("rotMeta");
		this.days = tags.getInteger("days");
	}

	@Override
	public String categoryName()
	{
		return "decay";
	}

	@Override
	public String categoryDescription()
	{
		return "Set the properties of decay Items";
	}

	@Override
	public void LoadProperty(Configuration config, String category)
	{
		config.addCustomCategoryComment(this.categoryName(), this.categoryDescription());
		String name = config.get(category, RPName[0], "").getString();
		int meta = config.get(category, RPName[1], -1).getInt(-1);
		String rotID = config.get(category, RPName[2], "", "Set blank to rot into nothing").getString();
		int rotMeta = config.get(category, RPName[3], 0).getInt(0);
		int DTR = config.get(category, RPName[4], 0, "Set this to -1 to disable rotting on this item").getInt(0);
		String filename = config.getConfigFile().getName();
		
		DecayProperties entry = new DecayProperties(name, meta, rotID, rotMeta, DTR, filename);
		
		if(meta < 0)
		{
			// If item already exist and current file hasn't completely been loaded do this
			if(DecayConfig.decayProperties.containsKey("" + name) && !EM_ConfigHandler.loadedConfigs.contains(filename)) Decay.log.log(Level.ERROR, "CONFIG DUPLICATE: Spoiling/Rot -"+ name.toUpperCase() +" was already added from "+ DecayConfig.decayProperties.get(name).loadedFrom.toUpperCase() +" and will be overriden by "+ filename.toUpperCase());
			
			DecayConfig.decayProperties.put("" + name, entry);
		} else
		{
			// If item already exist and current file hasn't completely been loaded do this
			if(DecayConfig.decayProperties.containsKey("" + name + "," + meta) && !EM_ConfigHandler.loadedConfigs.contains(filename)) Decay.log.log(Level.ERROR, "CONFIG DUPLICATE: Spoiling/Rot -"+ name.toUpperCase() +" - Meta:"+ meta +" was already added from "+ DecayConfig.decayProperties.get(name).loadedFrom.toUpperCase() +" and will be overriden by "+ filename.toUpperCase());
			
			DecayConfig.decayProperties.put("" + name + "," + meta, entry);
		}
	}

	@Override
	public void SaveProperty(Configuration config, String category)
	{
		config.get(category, RPName[0], name).getString();
		config.get(category, RPName[1], meta).getInt(-1);
		config.get(category, RPName[2], rotID, "Set blank to rot into nothing").getString();
		config.get(category, RPName[3], rotMeta).getInt(0);
		config.get(category, RPName[4], days, "Set this to -1 to disable rotting on this item").getInt(7);
	}

	@Override
	public void GenDefaults()
	{
		@SuppressWarnings("unchecked")
		Iterator<Item> iterator = Item.REGISTRY.iterator();
		
		while(iterator.hasNext())
		{
			Item item = iterator.next();
			
			if(item == null)
			{
				continue;
			}
			
			String[] regName = Item.REGISTRY.getNameForObject(item). (":");
			
			if(regName.length <= 0)
			{
				Decay.log.log(Level.ERROR, "Failed to get correctly formatted object name for " + item.getUnlocalizedName() +"_"+ regName[1]);
				continue;
			}
			
			File itemFile = new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath + Decay.SafeFilename(regName[0]) + ".cfg");
			
			if(!itemFile.exists())
			{
				try
				{
					itemFile.createNewFile();
				} catch(Exception e)
				{
					Decay.log.log(Level.ERROR, "Failed to create file for " + item.getUnlocalizedName() +"_"+ regName[1], e);
					continue;
				}
			}
			
			Configuration config = new Configuration(itemFile, true);
			
			String category = this.categoryName() + "." + Decay.replaceULN(item.getUnlocalizedName() +"_"+ regName[1]);
			
			config.load();
			if(item == Items.ROTTEN_FLESH || item == ObjectHandler.rottenFood)
			{
				config.get(category, RPName[0], Item.REGISTRY.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], "", "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], -1, "Set this to -1 to disable rotting on this item").getInt(-1);
			} else if(item == Items.MILK_BUCKET)
			{
				config.get(category, RPName[0], Item.REGISTRY.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.REGISTRY.getNameForObject(ObjectHandler.spoiledMilk), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			} else if(item == Items.SPIDER_EYE)
			{
				config.get(category, RPName[0], Item.REGISTRY.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.REGISTRY.getNameForObject(Items.FERMENTED_SPIDER_EYE), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			} else if(item == Items.FERMENTED_SPIDER_EYE || item == Items.BEEF || item == Items.CHICKEN || item == Items.PORKCHOP || item == Items.FISH || item == Items.COOKED_BEEF || item == Items.COOKED_CHICKEN || item == Items.COOKED_PORKCHOP || item == Items.COOKED_FISH)
			{
				config.get(category, RPName[0], Item.REGISTRY.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.REGISTRY.getNameForObject(Items.ROTTEN_FLESH), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			} else if(item instanceof ItemFood && (regName[0].equals("minecraft") || DecayConfig.genConfigs))
			{
				config.get(category, RPName[0], Item.REGISTRY.getNameForObject(item)).getString();
				config.get(category, RPName[1], -1).getInt(-1);
				config.get(category, RPName[2], Item.REGISTRY.getNameForObject(ObjectHandler.rottenFood), "Set blank to rot into nothing").getString();
				config.get(category, RPName[3], 0).getInt(0);
				config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
			}
			
			config.save();
		}
	}

	@Override
	public File GetDefaultFile()
	{
		return new File(EM_ConfigHandler.loadedProfile + EM_ConfigHandler.customPath + "Spoiling.cfg");
	}

	@Override
	public void generateEmpty(Configuration config, Object obj)
	{
		if(obj == null || !(obj instanceof Item))
		{
			Decay.log.log(Level.ERROR, "Tried to register config with non item object!", new Exception());
			return;
		}
		
		Item item = (Item)obj;
		
		String regName = item.getUnlocalizedName();
		
		if(regName.length() <= 0)
		{
			Decay.log.log(Level.ERROR, "Failed to get correctly formatted object name for " + item.getUnlocalizedName());
			return;
		}
		
		String category = this.categoryName() + "." + Decay.replaceULN(item.getUnlocalizedName());
		
		config.get(category, RPName[0], Item.REGISTRY.getNameForObject(item)).getString();
		config.get(category, RPName[1], -1).getInt(-1);
		config.get(category, RPName[2], Item.REGISTRY.getNameForObject(ObjectHandler.rottenFood), "Set blank to rot into nothing").getString();
		config.get(category, RPName[3], 0).getInt(0);
		config.get(category, RPName[4], 7, "Set this to -1 to disable rotting on this item").getInt(7);
	}

	@Override
	public boolean useCustomConfigs()
	{
		return true;
	}

	@Override
	public void customLoad()
	{
	}
	
	static
	{
		RPName = new String[5];
		RPName[0] = "01.ID";
		RPName[1] = "02.Damage";
		RPName[2] = "03.Rotten ID";
		RPName[3] = "04.Rotten Damage";
		RPName[4] = "05.Days To Rot";
	}
}