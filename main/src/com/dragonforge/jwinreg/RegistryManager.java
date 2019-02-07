package com.dragonforge.jwinreg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
			List<String> args = new ArrayList<>();
			args.add("reg");
			args.add("add");
			args.add("\"" + entry.path + "\"");
			if(entry.defaultName)
				args.add("/ve");
			else
			{
				args.add("/v");
				args.add("\"" + entry.key + "\"");
			}
			if(entry.type != null)
			{
				args.add("/t");
				args.add(entry.type.getId());
			}
			if(overwrite)
				args.add("/f");
			args.add("/d");
			args.add("" + entry.stringValue() + "");
			Process process = new ProcessBuilder(args).start();
			
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static RegistryEntry<?> read(String path, String key, boolean defaultValue)
	{
		try
		{
			// Run reg query, then read output with StreamReader (internal
			// class)
			Process process = Runtime.getRuntime().exec("reg query " + '"' + path + "\" /v" + (defaultValue ? "e" : " " + key));
			
			process.waitFor();
			
			String[] output = readProcessOutput(process).replaceAll("\r", "").replaceAll("\n", "").split("    ");
			
			RegistryType<?> type = RegistryType.valueOf(output[2]);
			
			return new RegistryEntry(output[0], output[1], type, type != null ? type.convert(output[3]) : output[3]);
		} catch(Exception e)
		{
		}
		
		return null;
	}
	
	private static String readProcessOutput(Process process)
	{
		InputStream is = process.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try
		{
			int c;
			while((c = is.read()) != -1)
				baos.write(c);
		} catch(IOException e)
		{
		}
		
		return baos.toString();
	}
}