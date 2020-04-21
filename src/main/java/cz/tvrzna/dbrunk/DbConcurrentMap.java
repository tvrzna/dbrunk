package cz.tvrzna.dbrunk;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cz.tvrzna.jackie.Jackie;

/**
 * The Class DbConcurrentMap.
 *
 * @author michalt
 * @param <V>
 *          the value type
 * @since 0.1.0
 */
public class DbConcurrentMap<V> implements Serializable
{
	private static final long serialVersionUID = -1962417068327698201L;
	private static final Jackie JSON_PARSER = new Jackie();

	private ConcurrentMap<Long, String> map;
	private final Class<V> clazz;

	/**
	 * Instantiates a new db concurrent map.
	 *
	 * @param clazz
	 *          the clazz
	 */
	public DbConcurrentMap(Class<V> clazz)
	{
		this.map = new ConcurrentHashMap<>();
		this.clazz = clazz;
	}

	/**
	 * Put.
	 *
	 * @param key
	 *          the key
	 * @param value
	 *          the value
	 * @return the v
	 */
	public V put(Long key, V value)
	{
		return JSON_PARSER.fromJson(map.put(key, JSON_PARSER.toJson(value)), clazz);
	}

	/**
	 * Gets the.
	 *
	 * @param key
	 *          the key
	 * @return the v
	 */
	public V get(Long key)
	{
		return JSON_PARSER.fromJson(map.get(key), clazz);
	}

	/**
	 * Values.
	 *
	 * @return the collection
	 */
	public Collection<V> values()
	{
		Collection<V> result = new HashSet<>();
		for (String value : map.values())
		{
			result.add(JSON_PARSER.fromJson(value, clazz));
		}
		return result;
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Removes the.
	 *
	 * @param key
	 *          the key
	 */
	public void remove(Long key)
	{
		map.remove(key);
	}

}
