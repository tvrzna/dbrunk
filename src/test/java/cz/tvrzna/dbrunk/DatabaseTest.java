package cz.tvrzna.dbrunk;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.Test;

import cz.tvrzna.dbrunk.databases.FileDatabase;
import cz.tvrzna.dbrunk.databases.GZipFileDatabase;
import cz.tvrzna.dbrunk.databases.MemoryDatabase;

public class DatabaseTest
{

	@Test
	public void stressMemoryDatabaseTest() throws IOException
	{
		long start = System.currentTimeMillis();
		Database db = new MemoryDatabase();
		DbConcurrentMap<SampleObject> map = db.createOrOpen("sampleObject", SampleObject.class);
		DbConcurrentMap<SampleObject> map2 = db.createOrOpen("sampleObject2", SampleObject.class);

		for (long i = 0; i < 32767; i++)
		{
			map.put(i, new SampleObject(i, "value" + i, new Date()));
			map2.put(i, new SampleObject(i, "value" + i, new Date()));
		}

		db.commit();
		db.drop("sampleObject");
		db.drop("sampleObject2");
		db.commit();

		db.close();
		System.out.println("Performed memoryDB in " + (System.currentTimeMillis() - start) + "ms");
	}

	@Test
	public void stressFileDatabaseTest() throws IOException
	{
		long start = System.currentTimeMillis();
		Database db = new FileDatabase("test-file.db");
		DbConcurrentMap<SampleObject> map = db.createOrOpen("sampleObject", SampleObject.class);
		DbConcurrentMap<SampleObject> map2 = db.createOrOpen("sampleObject2", SampleObject.class);

		for (long i = 0; i < 32767; i++)
		{
			map.put(i, new SampleObject(i, "value" + i, new Date()));
			map2.put(i, new SampleObject(i, "value" + i, new Date()));
		}

		db.commit();
		db.drop("sampleObject");
		db.drop("sampleObject2");
		db.commit();

		db.close();
		File dbFile = new File("test-file.db");
		dbFile.delete();
		System.out.println("Performed fileDB in  " + (System.currentTimeMillis() - start) + "ms");
	}

	@Test
	public void stressGZipFileDatabaseTest() throws IOException
	{
		long start = System.currentTimeMillis();
		Database db = new GZipFileDatabase("test-gzip.db");
		DbConcurrentMap<SampleObject> map = db.createOrOpen("sampleObject", SampleObject.class);
		DbConcurrentMap<SampleObject> map2 = db.createOrOpen("sampleObject2", SampleObject.class);

		for (long i = 0; i < 32767; i++)
		{
			map.put(i, new SampleObject(i, "value" + i, new Date()));
			map2.put(i, new SampleObject(i, "value" + i, new Date()));
		}

		db.commit();
		db.drop("sampleObject");
		db.drop("sampleObject2");
		db.commit();

		db.close();
		File dbFile = new File("test-gzip.db");
		dbFile.delete();
		System.out.println("Performed gzipDB in " + (System.currentTimeMillis() - start) + "ms");
	}

	private static class SampleObject
	{
		private Long id;
		private String value;
		private Date createDate;

		public SampleObject(Long id, String value, Date createDate)
		{
			super();
			this.id = id;
			this.value = value;
			this.createDate = createDate;
		}
	}

}
