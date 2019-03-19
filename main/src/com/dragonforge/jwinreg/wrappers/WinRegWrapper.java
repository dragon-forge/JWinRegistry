package com.dragonforge.jwinreg.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.dragonforge.jwinreg.RegistryEntry;
import com.dragonforge.jwinreg.RegistryType;

public class WinRegWrapper implements IRegistryWrapper
{
	@Override
	public void deleteParam(String path, String key)
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

	@Override
	public void deletePath(String path)
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

	@Override
	public void write(RegistryEntry<?> entry, boolean overwrite)
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

	@Override
	public RegistryEntry<?> read(String path, String key, boolean defaultValue)
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