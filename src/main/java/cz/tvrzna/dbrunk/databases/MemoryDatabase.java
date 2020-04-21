package cz.tvrzna.dbrunk.databases;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cz.tvrzna.dbrunk.Database;
import cz.tvrzna.dbrunk.DbConcurrentMap;

/**
 * Implementation of {@link Database}, that performs all operations just in
 * memory.
 *
 * @author michalt
 * @since 0.1.0
 */
public class MemoryDatabase implements Database
{
	private ConcurrentMap<String, DbConcurrentMap<?>> database = new ConcurrentHashMap<>();

	/*
	 * (non-Javadoc)
	 *
	 * @see cz.tvrzna.dbrunk.Database#createOrOpen(java.lang.String,
	 * java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> DbConcurrentMap<T> createOrOpen(String tableName, Class<T> clazz)
	{
		return (DbConcurrentMap<T>) database.computeIfAbsent(tableName, (k) -> new DbConcurrentMap<>(clazz));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see cz.tvrzna.dbrunk.Database#drop(java.lang.String)
	 */
	@Override
	public void drop(String tableName)
	{
		database.remove(tableName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see cz.tvrzna.dbrunk.Database#commit()
	 */
	@Override
	public void commit() throws IOException
	{
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see cz.tvrzna.dbrunk.Database#close()
	 */
	@Override
	public void close() throws IOException
	{
		database = null;
	}

}
