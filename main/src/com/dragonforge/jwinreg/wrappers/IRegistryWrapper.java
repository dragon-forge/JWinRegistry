package com.dragonforge.jwinreg.wrappers;

import com.dragonforge.jwinreg.RegistryEntry;

public interface IRegistryWrapper
{
	void deleteParam(String path, String key);
	
	void deletePath(String path);
	
	void write(RegistryEntry<?> entry, boolean overwrite);
	
	RegistryEntry<?> read(String path, String key, boolean defaultValue);
	
	default void cleanup()
	{
	}
}