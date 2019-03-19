package com.dragonforge.jwinreg;

public enum EnumOS
{
	WINDOWS, //
	OTHER;
	
	private static final EnumOS CURR;
	
	static
	{
		EnumOS d = OTHER;
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
			d = WINDOWS;
		CURR = d;
	}
	
	public static EnumOS getOS()
	{
		return CURR;
	}
}