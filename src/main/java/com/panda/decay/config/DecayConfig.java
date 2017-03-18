package com.panda.decay.config;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DecayConfig
{

	
	public static File worldDir = null;
	
	
	@ShouldOverride({String.class, DecayProperties.class})
	public static HashMap<String,DecayProperties> decayProperties = new HashMap<String,DecayProperties>();
	

	public static int foodRotTime = 7;
	
	/** Whether or not this overridden with server settings */
	public static boolean isOverridden = false;
	
	
	
	/**
	 * Tells the server that this field should be sent to the client to overwrite<br>
	 * Usage:<br>
	 * <tt>@ShouldOverride</tt> - for ints/booleans/floats/Strings<br>
	 * <tt>@ShouldOverride(Class[] value)</tt> - for ArrayList or HashMap types
	 * */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ShouldOverride
	{
		Class<?>[] value() default {};
	}
}