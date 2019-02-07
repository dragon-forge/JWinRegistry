package com.dragonforge.jwinreg;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

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
		RegistryHelper.associateURLProtocol("notepad", new File("C:\\Windows\\notepad.exe"), "\\\"%s\\\"", "Notepad Protocol");
		
		RegistryHelper.delFileExtention(".testtxt");
		RegistryHelper.delURLProtocol("notepad");
		
		System.out.println("dview://");
		RegistryHelper.associateURLProtocol("dview", new File(System.getenv("Appdata"), "Dragon Forge" + File.separator + "Dragon Viewer" + File.separator + "DragonViewer.exe"), "-url \\\"%s\\\"", "DragonViewer Protocol");
		
		try
		{
			Desktop.getDesktop().browse(URI.create("dview://https://dccg.herokuapp.com/zmc"));
		} catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RegistryHelper.delURLProtocol("dview");
	}
}