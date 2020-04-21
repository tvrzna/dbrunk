package cz.tvrzna.dbrunk;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import cz.tvrzna.dbrunk.annotations.Entity;
import cz.tvrzna.dbrunk.annotations.Id;
import cz.tvrzna.dbrunk.databases.FileDatabase;
import cz.tvrzna.dbrunk.databases.GZipFileDatabase;
import cz.tvrzna.dbrunk.databases.MemoryDatabase;
import cz.tvrzna.dbrunk.exceptions.DbrunkDbException;
import cz.tvrzna.dbrunk.exceptions.DbrunkInitException;
import cz.tvrzna.dbrunk.repositories.AbstractEntity;
import cz.tvrzna.dbrunk.repositories.ResultWrapper;
import cz.tvrzna.dbrunk.utils.DbrunkType;
import cz.tvrzna.dbrunk.utils.Reflections;

/**
 * The Class DbrunkService.
 *
 * @author michalt
 */
public class DbrunkService
{
	private static final int MIN_COUNTER_VALUE = 0;
	private static final int MAX_COUNTER_VALUE = 9999;
	private static int rotateCounter = MIN_COUNTER_VALUE;

	private static DbrunkService instance;

	private Semaphore semaphore = new Semaphore(1, true);

	private Database db;
	private boolean autocommit = true;

	/**
	 * Gets the single instance of DbrunkService.
	 *
	 * @return single instance of DbrunkService
	 */
	public static DbrunkService getInstance()
	{
		if (instance == null)
		{
			instance = new DbrunkService();
		}
		return instance;
	}

	/**
	 * Inits the dbrunk service.
	 *
	 * @param dbType
	 *          the db type
	 * @param filePath
	 *          the file path
	 * @throws DbrunkInitException
	 *           the dbrunk init exception
	 */
	public void init(DbrunkType dbType, String filePath) throws DbrunkInitException
	{
		if (db == null)
		{
			try
			{
				switch (dbType)
				{
					case MEMORY:
						db = new MemoryDatabase();
						break;
					case GZIP:
						db = new GZipFileDatabase(filePath);
						break;
					case FILE:
					default:
						db = new FileDatabase(filePath);
				}
			}
			catch (IOException e)
			{
				throw new DbrunkInitException("Could not initialize database", e);
			}
		}
		else
		{
			throw new DbrunkInitException("Database is already initialized", null);
		}
	}

	/**
	 * Saves entity into Database.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param entity
	 *          the entity
	 * @return the t
	 */
	public <T extends AbstractEntity> T save(Class<T> clazz, T entity)
	{
		ResultWrapper<T> result = new ResultWrapper<>();
		dbOperation(clazz, map -> {
			T resultEntity = entity;
			if (getEntityId(resultEntity) == null)
			{
				setEntityId(resultEntity, generateId());
			}
			map.put(getEntityId(resultEntity), resultEntity);
			result.setValue(resultEntity);
		}, false);
		return result.getValue();
	}

	/**
	 * Finds entity by <code>id</code>.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param id
	 *          the id
	 * @return the t
	 */
	public <T extends AbstractEntity> T find(Class<T> clazz, Long id)
	{
		ResultWrapper<T> result = new ResultWrapper<>();
		dbOperation(clazz, map -> result.setValue(map.get(id)), true);
		return result.getValue();
	}

	/**
	 * Finds all entities by class.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @return the list
	 */
	public <T extends AbstractEntity> List<T> findAll(Class<T> clazz)
	{
		List<T> result = new ArrayList<>();
		dbOperation(clazz, map -> result.addAll(map.values()), true);
		return result;
	}

	/**
	 * Removes the entity by <code>id</code>.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param id
	 *          the id
	 */
	public <T extends AbstractEntity> void remove(Class<T> clazz, Long id)
	{
		dbOperation(clazz, map -> map.remove(id), false);
	}

	/**
	 * Removes the entity by reference.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param entity
	 *          the entity
	 */
	public <T extends AbstractEntity> void remove(Class<T> clazz, T entity)
	{
		dbOperation(clazz, map -> map.remove(getEntityId(entity)), false);
	}

	/**
	 * Removes all entities defined by class.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 */
	public <T extends AbstractEntity> void removeAll(Class<T> clazz)
	{
		dbOperation(clazz, map -> map.clear(), false);
	}

	/**
	 * Performs commit operation on database.
	 */
	public void commit()
	{
		try
		{
			db.commit();
		}
		catch (Exception e)
		{
			throw new DbrunkDbException("Could not commit to database.", e);
		}
	}

	/**
	 * Checks, if is autocommit.
	 *
	 * @return true, if is autocommit
	 */
	public boolean isAutocommit()
	{
		return autocommit;
	}

	/**
	 * Sets the autocommit.
	 *
	 * @param autocommit
	 *          the new autocommit
	 */
	public void setAutocommit(boolean autocommit)
	{
		this.autocommit = autocommit;
	}

	/**
	 * Db operation.
	 *
	 * @param <T>
	 *          the generic type
	 * @param clazz
	 *          the clazz
	 * @param function
	 *          the function
	 * @param readOnly
	 *          the read only
	 * @throws DbrunkDbException
	 *           the dbrunk db exception
	 */
	private <T extends AbstractEntity> void dbOperation(Class<T> clazz, Consumer<DbConcurrentMap<T>> function, boolean readOnly) throws DbrunkDbException
	{
		try
		{
			if (semaphore.tryAcquire(1l, TimeUnit.SECONDS))
			{
				try
				{

					String tableName = clazz.getAnnotation(Entity.class).value();
					DbConcurrentMap<T> map = db.createOrOpen(tableName, clazz);
					function.accept(map);
					if (autocommit && !readOnly)
					{
						db.commit();
					}
				}
				catch (Exception e)
				{
					throw new DbrunkDbException("Could not perform DB operation", e);
				}
				finally
				{
					semaphore.release();
				}
			}
		}
		catch (Exception e)
		{
			throw new DbrunkDbException("Could not acquire database.", e);
		}
	}

	/**
	 * Gets the entity id.
	 *
	 * @param <T>
	 *          the generic type
	 * @param object
	 *          the object
	 * @return the entity id
	 */
	private <T extends AbstractEntity> Long getEntityId(T object)
	{
		Field[] fields = Reflections.findAnnotatedFields(object, Id.class);
		if (fields.length > 1)
		{
			throw new RuntimeException("Object contains more than one id!");
		}
		if (fields.length == 0)
		{
			return null;
		}
		fields[0].setAccessible(true);
		try
		{
			return (Long) fields[0].get(object);
		}
		catch (IllegalAccessException | IllegalArgumentException e)
		{
		}
		return null;
	}

	/**
	 * Sets the entity id.
	 *
	 * @param <T>
	 *          the generic type
	 * @param object
	 *          the object
	 * @param id
	 *          the id
	 */
	private <T extends AbstractEntity> void setEntityId(T object, Long id)
	{
		Field[] fields = Reflections.findAnnotatedFields(object, Id.class);
		if (fields.length > 1)
		{
			throw new RuntimeException("Object contains more than one id!");
		}
		if (fields.length == 0)
		{
			return;
		}
		fields[0].setAccessible(true);
		try
		{
			fields[0].set(object, id);
		}
		catch (IllegalAccessException | IllegalArgumentException e)
		{
		}
	}

	/**
	 * Generate id.
	 *
	 * @return the long
	 */
	private synchronized Long generateId()
	{
		if (rotateCounter >= MAX_COUNTER_VALUE)
		{
			rotateCounter = MIN_COUNTER_VALUE;
		}
		else
		{
			rotateCounter++;
		}

		Calendar cal = Calendar.getInstance();
		String template = String.format("%04d%02d%02d%02d%02d%02d%04d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), rotateCounter);
		return Long.parseLong(template);
	}
}
