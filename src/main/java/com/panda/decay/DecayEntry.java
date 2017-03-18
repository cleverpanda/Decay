package com.panda.decay;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DecayEntry {
	public ItemStack itemIn;
	
	public ItemStack itemOut;
	
	public String type;
	public int ticks;
	
	
	public DecayEntry(ItemStack in, ItemStack out, String type,Boolean tickup,int ticks)
	{
		this.itemIn = in;
		
		this.itemOut = out;

		
		this.type = type;
		this.ticks = ticks;

	}

	public boolean checkMetaIn( DecayEntry ent){
		return ent.itemIn.getMetadata()==-1? false:true;
	}
	public boolean checkMetaOut( DecayEntry ent){
		return ent.itemOut.getMetadata()==-1? false:true;
	}
	
	public int TimetoTicks(int length, String unit) {
		switch(unit.toLowerCase()){
		case "ticks":
			return length;
		case "seconds":
			return length*20;
		case "minutes":
			return length*1200;
		case "hours":
			return length*72000;
		case "days":
			return length*24000;
		case "years":
			return length*8640000;
		}
		Decay.log.error("Unit "+unit+" is not supported. Using " + length +"as ticks");
		return length;
	}
	
	public boolean tickDirection(String dir){
		switch(dir.toLowerCase()){
		case "up":
			return true;
		case "down":
			return false;
		}
		Decay.log.error("Ticking direction "+dir+" is not supported. Defaulting to UP");
		return true;
	}
}