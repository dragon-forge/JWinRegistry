package com.dragonforge.jwinreg;

import java.io.File;

public class TestWinRegistry
{
	public static void main(String[] args)
	{
		RegistryPath path = new RegistryPath(RegistryRoot.HKEY_CURRENT_USER).append("Software").append("JWinRegistry");
		
		RegistryManager.write(new RegistryEntry<>(path, "TestMain", RegistryType.REG_SZ, "This really works!"), true);
		
		System.out.println(RegistryManager.read(path.toString(), "TestMain"));
		
		RegistryManager.deletePath(path.toString());
		
		System.out.println(".testtxt -> notepad.exe");
		RegistryHelper.associateFileExtention(".testtxt", "Notepad++", new File("C:\\Windows\\notepad.exe"), "Text file");
		
		System.out.println("notepad://");
		RegistryHelper.associateURLProtocol("notepad", new File("C:\\Windows\\notepad.exe"), "Notepad Protocol");
		
		RegistryHelper.delFileExtention(".testtxt");
		RegistryHelper.delURLProtocol("notepad");
	}
}