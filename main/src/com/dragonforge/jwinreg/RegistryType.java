package com.dragonforge.jwinreg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * The type of registry value. <br>
 * Values: {@link RegistryType#REG_SZ}, {@link RegistryType#REG_MULTI_SZ},
 * {@link RegistryType#REG_NONE}, {@link RegistryType#REG_DWORD},
 * {@link RegistryType#REG_QWORD}, {@link RegistryType#REG_BINARY}
 */
public class RegistryType<T>
{
	private static final Map<String, RegistryType<?>> TYPES = new HashMap<>();
	
	/**
	 * String type
	 */
	public static final RegistryType<String> REG_SZ = new RegistryType<>("REG_SZ", s -> s);
	
	/**
	 * String list type
	 */
	public static final RegistryType<String> REG_EXPAND_SZ = new RegistryType<>("REG_EXPAND_SZ", s -> s);
	
	/**
	 * String list type
	 */
	public static final RegistryType<List<String>> REG_MULTI_SZ = new RegistryType<>("REG_MULTI_SZ", s -> Arrays.asList(s.split("\\\\0")), l ->
	{
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < l.size(); ++i)
			b.append(l.get(i) + (i < l.size() - 1 ? "\\0" : ""));
		return b.toString();
	});
	
	/**
	 * Undefined type. Stored as string internally.
	 */
	public static final RegistryType<Object> REG_NONE = new RegistryType<>("REG_NONE", s -> s);
	
	/**
	 * A 32-bit number (integer) type
	 */
	public static final RegistryType<Integer> REG_DWORD = new RegistryType<>("REG_DWORD", s -> Integer.decode(s));
	
	/**
	 * A 64-bit number (long) type
	 */
	public static final RegistryType<Long> REG_QWORD = new RegistryType<>("REG_QWORD", s -> Long.decode(s));
	
	/**
	 * A binary type.
	 */
	public static final RegistryType<byte[]> REG_BINARY = new RegistryType<>("REG_BINARY", s ->
	{
		ByteBuffer buf = ByteBuffer.allocate(s.length() / 2);
		
		Character cache = null;
		char[] chars = s.toCharArray();
		for(int i = 0; i < chars.length; ++i)
		{
			if(i % 2 == 0)
				cache = chars[i];
			else
				buf.put(Byte.parseByte(cache.toString() + chars[i], 16));
		}
		
		return buf.array();
	}, ba ->
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < ba.length; ++i)
			sb.append(Integer.toString(ba[i], 16));
		return sb.toString();
	});
	
	private final String id;
	private final Function<String, T> converter;
	private final Function<T, String> backConverter;
	
	private RegistryType(String id, Function<String, T> converter)
	{
		this(id, converter, Objects::toString);
	}
	
	private RegistryType(String id, Function<String, T> converter, Function<T, String> backConverter)
	{
		TYPES.put(id, this);
		this.id = id;
		this.converter = converter;
		this.backConverter = backConverter;
	}
	
	T convert(String value)
	{
		return converter.apply(value);
	}
	
	String backConvert(T val)
	{
		return backConverter.apply(val);
	}
	
	/**
	 * @return the registry ID for this type.
	 */
	public String getId()
	{
		return id;
	}
	
	static RegistryType<?> valueOf(String id)
	{
		return TYPES.get(id);
	}
}