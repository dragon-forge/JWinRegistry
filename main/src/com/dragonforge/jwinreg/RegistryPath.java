package com.dragonforge.jwinreg;

import java.util.ArrayList;
import java.util.List;

public class RegistryPath
{
	public final RegistryRoot root;
	private final List<String> path = new ArrayList<>();
	
	public RegistryPath(RegistryRoot root)
	{
		this.root = root;
	}
	
	public RegistryPath append(String sub)
	{
		path.add(sub);
		return this;
	}
	
	@Override
	public String toString()
	{
		return root.toString() + "\\" + Joiner.BACKSLASH.join(path);
	}
}