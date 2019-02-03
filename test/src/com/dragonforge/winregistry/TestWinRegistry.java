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
		RegistryManager.write(new RegistryEntry<>(new RegistryPath(RegistryRoot.HKEY_CURRENT_USER).append("Software").append("JWinRegistry"), "TestMain", RegistryType.REG_SZ, "This really works!"), true);
	}
}