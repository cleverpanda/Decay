package com.panda.decay;



import org.apache.logging.log4j.Logger;

import com.panda.decay.config.ReadUtils;
import com.panda.decay.helpers.EventsHandler;
import com.panda.decay.proxy.CommonProxy;
import com.panda.decay.proxy.ProxyClient;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Decay.MODID, version = Decay.VERSION, name = Decay.NAME)
public class Decay
{
    public static final String MODID = "decay";
    public static final String VERSION = "1.0";
    public static final String NAME = "Decay ";
    
    @SidedProxy(serverSide = "com.panda.decay.proxy.ProxyServer", clientSide = "com.panda.decay.proxy.ProxyClient")
	public static CommonProxy proxy;

	public static boolean haveWarnedVersionOutOfDate;

	//public static ProxyClient PROXY = null;
	
	public static Logger log;
    
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new EventsHandler());
		DecayRegistry.register(new ItemStack(Items.BAKED_POTATO,1, -1),new ItemStack(Items.ROTTEN_FLESH,1, -1),"rot",true,800);
		DecayRegistry.register(new ItemStack(Items.POTATO,1, -1),new ItemStack(Items.POISONOUS_POTATO,1, -1),"rot",true,800);
		DecayRegistry.register(new ItemStack(Items.BREAD,1, -1),new ItemStack(Items.ROTTEN_FLESH,1, -1), "rot",true,800);
		DecayRegistry.register(new ItemStack(Item.getItemFromBlock(Blocks.TORCH),1, -1),new ItemStack(Items.STICK,1, -1), "rot",true,800);
		proxy.preInit(event);

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{ 
		
		proxy.init(event);
	
		MinecraftForge.EVENT_BUS.register(this);
	}  
	
	
	
	@EventHandler
	public void PostInitialize(FMLPostInitializationEvent event)
	{
	 ReadUtils.ImportAllConfigs();   
	}
	
	/*
	public static final CreativeTabs TreeTab = new CreativeTabs(VarietyTrees.MODID) {
	    @Override public Item getTabIconItem() {
	        return panda.varietytrees.init.Items.oak_seed;
	    }
	};*/
	
	public static String replaceULN(String unlocalizedName)
	{
		unlocalizedName = unlocalizedName.replaceAll("[\\(\\)]", "");
		unlocalizedName = unlocalizedName.replaceAll("\\.+", "\\_");
		
		return unlocalizedName;
	}
	
	public static final String[] reservedNames = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    public static final char[] specialCharacters = new char[] {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

	public static String SafeFilename(String filename)
	{
		String safeName = filename;
		for(String reserved : reservedNames)
		{
			if(safeName.equalsIgnoreCase(reserved))
			{
				safeName = "_" + safeName + "_";
			}
		}
		
		for(char badChar : specialCharacters)
		{
			safeName = safeName.replace(badChar, '_');
		}
		
		return safeName;
	}
}
