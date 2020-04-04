package cz.tvrzna.dbrunk.databases;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cz.tvrzna.dbrunk.Database;
import cz.tvrzna.dbrunk.DbConcurrentMap;

public class MemoryDatabase implements Database
{
	private ConcurrentMap<String, DbConcurrentMap<?>> database = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> DbConcurrentMap<T> createOrOpen(String tableName, Class<T> clazz)
	{
		return (DbConcurrentMap<T>) database.computeIfAbsent(tableName, (k) -> new DbConcurrentMap<>(clazz));
	}

	@Override
	public void drop(String tableName)
	{
		database.remove(tableName);
	}

	@Override
	public void commit() throws IOException
	{
		// nothing to do
	}

	@Override
	public void close() throws IOException
	{
		database = null;
	}

}
