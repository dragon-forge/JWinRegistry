package com.dragonforge.jwinreg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

class Joiner
{
	static final Joiner BACKSLASH = on("\\");
	
	private final String splitter;
	private boolean skipNull = false;
	
	public Joiner(String splitter)
	{
		this.splitter = splitter;
	}
	
	public static Joiner on(String between)
	{
		return new Joiner(between);
	}
	
	public static Joiner on(String between, boolean skipNull)
	{
		Joiner j = on(between);
		j.skipNull = skipNull;
		return j;
	}
	
	public String join(Object[] join)
	{
		return join(Arrays.stream(join));
	}
	
	public String join(Iterable<?> join)
	{
		return join(join.iterator());
	}
	
	public String join(Stream<?> join)
	{
		return join(join.iterator());
	}
	
	public String join(Iterator<?> join)
	{
		StringBuilder b = new StringBuilder();
		
		while(join.hasNext())
		{
			Object o = join.next();
			if(skipNull && o == null)
				continue;
			boolean more = join.hasNext();
			
			b.append(Objects.toString(o));
			
			if(more)
				b.append(splitter);
		}
		
		return b.toString();
	}
	
	public String[] split(String par)
	{
		return par.split(splitter);
	}
}