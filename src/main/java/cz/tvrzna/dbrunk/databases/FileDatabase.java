package cz.tvrzna.dbrunk.databases;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cz.tvrzna.dbrunk.Database;
import cz.tvrzna.dbrunk.DbConcurrentMap;

/**
 * Implementation of {@link Database}, that stores all data into specified file.
 *
 * @author michalt
 * @since 0.1.0
 */
public class FileDatabase implements Database
{
	private RandomAccessFile raf;
	private ConcurrentMap<String, DbConcurrentMap<?>> database;

	/**
	 * Instantiates a new file database.
	 *
	 * @param filePath
	 *          the file path
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public FileDatabase(String filePath) throws IOException
	{
		File file = new File(filePath);
		raf = new RandomAccessFile(file, "rw");
		if (!file.exists() || file.length() == 0l)
		{
			database = new ConcurrentHashMap<>();
			commit();
		}

		raf.seek(0);

		byte[] content = new byte[(int) file.length()];
		raf.read(content, 0, (int) file.length());

		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		ObjectInputStream ois = new ObjectInputStream(bais);
		try
		{
			database = (ConcurrentMap<String, DbConcurrentMap<?>>) ois.readObject();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		ois.close();
		bais.close();
	}

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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(database);

		raf.seek(0);
		raf.write(baos.toByteArray());

		oos.close();
		baos.close();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see cz.tvrzna.dbrunk.Database#close()
	 */
	@Override
	public void close() throws IOException
	{
		raf.close();
	}

}
