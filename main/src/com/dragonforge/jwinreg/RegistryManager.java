package com.dragonforge.jwinreg;

import java.io.File;

import com.dragonforge.jwinreg.wrappers.FileRegWrapper;
import com.dragonforge.jwinreg.wrappers.IRegistryWrapper;
import com.dragonforge.jwinreg.wrappers.WinRegWrapper;

/**
 * A basic windows wrapper for registry management through Java.
 */
public class RegistryManager
{
	public static IRegistryWrapper registry;
	
	public static void init(IRegistryWrapper wrapper)
	{
		registry = wrapper;
	}
	
	public static void cleanup()
	{
		if(registry != null)
		{
			registry.cleanup();
			registry = null;
		}
	}
	
	static
	{
		if(EnumOS.getOS() == EnumOS.WINDOWS)
			registry = new WinRegWrapper();
		else
			registry = new FileRegWrapper(new File(System.getenv("APPDATA"), "jwinregistry.jwr"));
	}
	
	/**
	 * Delete the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 */
	public static void deleteParam(RegistryPath path, String key)
	{
		deleteParam(path.toString(), key);
	}
	
	/**
	 * Delete the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 */
	public static void deleteParam(String path, String key)
	{
		registry.deleteParam(path, key);
	}
	
	/**
	 * Delete the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 */
	public static void deletePath(RegistryPath path)
	{
		deletePath(path.toString());
	}
	
	/**
	 * Delete the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 */
	public static void deletePath(String path)
	{
		registry.deletePath(path);
	}
	
	/**
	 * Write the parameter to the registry.
	 * 
	 * @param entry
	 *            The entry to write to registry.
	 * @param overwrite
	 *            Whether the entry should overwrite existing one.
	 */
	public static void write(RegistryEntry<?> entry, boolean overwrite)
	{
		registry.write(entry, overwrite);
	}
	
	/**
	 * Read the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 * @return The queried entry, or null, if entry does not exist.
	 */
	public static RegistryEntry<?> read(RegistryPath path, String key)
	{
		return read(path.toString(), key);
	}
	
	/**
	 * Read the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @return The queried default entry, or null, if entry does not exist.
	 */
	public static RegistryEntry<?> readDefault(RegistryPath path)
	{
		return read(path.toString(), null, true);
	}
	
	/**
	 * Read the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 * @return The queried entry, or null, if entry does not exist.
	 */
	public static RegistryEntry<?> read(String path, String key)
	{
		return read(path, key, false);
	}
	
	/**
	 * Read the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @return The queried entry, or null, if entry does not exist.
	 */
	public static RegistryEntry<?> readDefault(String path)
	{
		return read(path, null, true);
	}
	
	/**
	 * Read the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 * @return The queried entry, or null, if entry does not exist.
	 */
	private static RegistryEntry<?> read(String path, String key, boolean defaultValue)
	{
		return registry.read(path, key, defaultValue);
	}
}