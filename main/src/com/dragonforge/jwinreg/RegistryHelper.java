package com.dragonforge.jwinreg;

public class RegistryHelper
{
	public static final String SYSTEM_ENVIRONMENT = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment";
	public static final String AUTORUN = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
	
	public static String SOFTWARE(String app)
	{
		return "HKEY_CURRENT_USER\\Software\\" + app;
	}
	
	
}