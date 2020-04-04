package cz.tvrzna.dbrunk;

import java.io.IOException;

/**
 * This interface defines default operation of database.
 *
 * @since 0.1.0
 * @author michalt
 */
public interface Database
{

	/**
	 * Creates the or open table of defined <code>tableName</code>.
	 *
	 * @param <T>
	 *          the generic type
	 * @param tableName
	 *          the table name
	 * @param clazz
	 *          the clazz
	 * @return the db concurrent map
	 */
	public <T> DbConcurrentMap<T> createOrOpen(String tableName, Class<T> clazz);

	/**
	 * Drops table with defined <code>tableName</code>.
	 *
	 * @param tableName
	 *          the table name
	 */
	public void drop(String tableName);

	/**
	 * Commit.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void commit() throws IOException;

	/**
	 * Close.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException;
}
