package com.panda.decay.helpers;

import net.minecraft.nbt.NBTTagCompound;

public interface SerialisableProperty
{
	public abstract NBTTagCompound WriteToNBT();
	
	public abstract void ReadFromNBT(NBTTagCompound tags);
}