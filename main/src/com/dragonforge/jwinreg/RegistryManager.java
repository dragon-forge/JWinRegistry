package com.dragonforge.jwinreg;

import java.io.IOException;
import java.io.InputStream;

/**
 * A basic windows wrapper for registry management through Java.
 */
public class RegistryManager
{
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
		try
		{
			Process process = Runtime.getRuntime().exec("reg delete \"" + path + "\" /v \"" + key + "\"");
			
			process.getOutputStream().write("y\n".getBytes());
			process.getOutputStream().flush();
			
			process.waitFor();
		} catch(Exception e)
		{
		}
	}
	
	/**
	 * Delete the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 */
	public static void deletePath(String path)
	{
		try
		{
			Process process = Runtime.getRuntime().exec("reg delete \"" + path + "\"");
			
			process.getOutputStream().write("y\n".getBytes());
			process.getOutputStream().flush();
			
			process.waitFor();
		} catch(Exception e)
		{
		}
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
		try
		{
			Process process = Runtime.getRuntime().exec("reg add \"" + entry.path + "\" /v \"" + entry.key + (entry.type != null ? ("\" /t \"" + entry.type.getId()) : "") + "\" /d \"" + entry.stringValue() + "\"");
			
			process.getOutputStream().write((overwrite ? "y\n" : "n\n").getBytes());
			process.getOutputStream().flush();
			
			process.waitFor();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Read the parameter from registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do delete.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static RegistryEntry<?> read(String path, String key)
	{
		try
		{
			// Run reg query, then read output with StreamReader (internal
			// class)
			Process process = Runtime.getRuntime().exec("reg query " + '"' + path + "\" /v " + key);
			
			process.waitFor();
			
			InputStream is = process.getInputStream();
			StringBuilder sw = new StringBuilder();
			
			try
			{
				int c;
				while((c = is.read()) != -1)
					sw.append((char) c);
			} catch(IOException e)
			{
			}
			
			String[] output = sw.toString().replaceAll("\r", "").replaceAll("\n", "").split("    ");
			
			RegistryType<?> type = RegistryType.valueOf(output[2]);
			
			return new RegistryEntry(output[0], output[1], type, type != null ? type.convert(output[3]) : output[3]);
		} catch(Exception e)
		{
		}
		
		return null;
	}
}