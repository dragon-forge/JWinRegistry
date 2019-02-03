package com.dragonforge.jwinreg;

import java.util.Objects;

/**
 * Simple registry entry. Can be either written or read to/from registry.
 */
public class RegistryEntry<T>
{
	public final String path, key;
	public final RegistryType<T> type;
	public final T value;
	
	/**
	 * Constructs the entry. Use this to write to registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do operations with.
	 * @param type
	 *            The registry entry type.
	 * @param value
	 *            The value to be read/written.
	 */
	public RegistryEntry(String path, String key, RegistryType<T> type, T value)
	{
		this.path = path;
		this.key = key;
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Constructs the entry. Use this to write to registry.
	 * 
	 * @param path
	 *            The path to the registry key.
	 * @param key
	 *            The key to do operations with.
	 * @param type
	 *            The registry entry type.
	 * @param value
	 *            The value to be read/written.
	 */
	public RegistryEntry(RegistryPath path, String key, RegistryType<T> type, T value)
	{
		this.path = path.toString();
		this.key = key;
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return "RegistryEntry{path=" + path + ",key=" + key + ",type=" + (type == null ? "REG_NONE" : type.getId()) + ",value=" + value + "}";
	}
	
	/**
	 * @return The encoded value ready to be written. Caution: internal use
	 *         only.
	 */
	public String stringValue()
	{
		return type != null ? type.backConvert(value) : Objects.toString(value);
	}
}