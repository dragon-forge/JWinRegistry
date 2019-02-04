package com.dragonforge.jwinreg;

import java.io.File;

public class RegistryHelper
{
	public static final String SYSTEM_ENVIRONMENT = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment";
	public static final String AUTORUN = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
	
	public static String SOFTWARE(String app)
	{
		return "HKEY_CURRENT_USER\\Software\\" + app;
	}
	
	public static void associateFileExtention(String extention, String identifier, File executable, String description)
	{
		if(!extention.startsWith("."))
			throw new IllegalArgumentException(extention + " must start with a dot!");
		String handler = "AppJWinReg{" + identifier + "}";
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + extention, RegistryType.REG_SZ, handler), true);
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + handler, RegistryType.REG_SZ, description), true);
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + handler + "\\DefaultIcon", RegistryType.REG_SZ, "\"" + executable.getAbsolutePath() + "\",0"), true);
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + handler + "\\shell\\Open\\Command", RegistryType.REG_SZ, "\"" + executable.getAbsolutePath() + "\" \"%1\""), true);
	}
	
	public static void delFileExtention(String extention)
	{
		if(!extention.startsWith("."))
			throw new IllegalArgumentException(extention + " must start with a dot!");
		@SuppressWarnings("unchecked")
		RegistryEntry<String> identifier = (RegistryEntry<String>) RegistryManager.readDefault("HKEY_CURRENT_USER\\Software\\Classes\\" + extention);
		RegistryManager.deletePath("HKEY_CURRENT_USER\\Software\\Classes\\" + extention);
		if(identifier != null)
			RegistryManager.deletePath("HKEY_CURRENT_USER\\Software\\Classes\\" + identifier.value);
	}
	
	public static void associateURLProtocol(String protocol, File executable, String description)
	{
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + protocol, RegistryType.REG_SZ, "URL:" + description), true);
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + protocol, "URL Protocol", RegistryType.REG_SZ, "URL:" + description), true);
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + protocol + "\\DefaultIcon", RegistryType.REG_SZ, "\"" + executable.getAbsolutePath() + ",0\""), true);
		RegistryManager.write(new RegistryEntry<>("HKEY_CURRENT_USER\\Software\\Classes\\" + protocol + "\\shell\\Open\\Command", RegistryType.REG_SZ, "\"" + executable.getAbsolutePath() + "\" \"%1\""), true);
	}
	
	public static void delURLProtocol(String protocol)
	{
		RegistryManager.deletePath("HKEY_CURRENT_USER\\Software\\Classes\\" + protocol);
	}
}