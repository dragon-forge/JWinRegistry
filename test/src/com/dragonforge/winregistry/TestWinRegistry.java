package com.dragonforge.winregistry;

import com.dragonforge.jwinreg.RegistryEntry;
import com.dragonforge.jwinreg.RegistryManager;
import com.dragonforge.jwinreg.RegistryPath;
import com.dragonforge.jwinreg.RegistryRoot;
import com.dragonforge.jwinreg.RegistryType;

public class TestWinRegistry
{
	public static void main(String[] args)
	{
		RegistryPath path = new RegistryPath(RegistryRoot.HKEY_CURRENT_USER).append("Software").append("JWinRegistry");
		
		RegistryManager.write(new RegistryEntry<>(path, "TestMain", RegistryType.REG_SZ, "This really works!"), true);
		
		System.out.println(RegistryManager.read(path.toString(), "TestMain"));
		
		RegistryManager.deletePath(path.toString());
	}
}