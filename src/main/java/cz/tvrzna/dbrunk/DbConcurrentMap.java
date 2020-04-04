package cz.tvrzna.dbrunk;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cz.tvrzna.jackie.Jackie;

public class DbConcurrentMap<V> implements Serializable
{
	private static final long serialVersionUID = -1962417068327698201L;
	private static final Jackie JSON_PARSER = new Jackie();

	private ConcurrentMap<Long, String> map;
	private final Class<V> clazz;

	public DbConcurrentMap(Class<V> clazz)
	{
		this.map = new ConcurrentHashMap<>();
		this.clazz = clazz;
	}

	public V put(Long key, V value)
	{
		return JSON_PARSER.fromJson(map.put(key, JSON_PARSER.toJson(value)), clazz);
	}

	public V get(Long key)
	{
		return JSON_PARSER.fromJson(map.get(key), clazz);
	}

	public Collection<V> values()
	{
		Collection<V> result = new HashSet<>();
		for (String value : map.values())
		{
			result.add(JSON_PARSER.fromJson(value, clazz));
		}
		return result;
	}

	public void clear()
	{
		map.clear();
	}

	public void remove(Long key)
	{
		map.remove(key);
	}

}
