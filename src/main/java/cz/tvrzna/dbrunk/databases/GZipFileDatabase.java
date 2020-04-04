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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cz.tvrzna.dbrunk.Database;
import cz.tvrzna.dbrunk.DbConcurrentMap;

public class GZipFileDatabase implements Database
{
	private RandomAccessFile raf;
	private ConcurrentMap<String, DbConcurrentMap<?>> database;

	@SuppressWarnings("unchecked")
	public GZipFileDatabase(String filePath) throws IOException
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
		GZIPInputStream gzip = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gzip);
		try
		{
			database = (ConcurrentMap<String, DbConcurrentMap<?>>) ois.readObject();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		ois.close();
		gzip.close();
		bais.close();
	}

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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gzip);
		oos.writeObject(database);
		oos.flush();
		gzip.finish();

		raf.seek(0);
		raf.write(baos.toByteArray());

		oos.close();
		gzip.close();
		baos.close();
	}

	@Override
	public void close() throws IOException
	{
		raf.close();
	}

}
