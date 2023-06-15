package cz.tvrzna.dbrunk.repositories;

import java.io.Serializable;
import java.util.List;

import cz.tvrzna.dbrunk.DbrunkService;

/**
 * The Class defines basic CRUD operation on entities stored in database.
 *
 * @author michalt
 * @param <T>
 *          the generic type
 * @since 0.1.0
 */
public abstract class AbstractRepository<T extends AbstractEntity> implements Serializable
{
	private static final long serialVersionUID = 1922380533400857928L;

	private final Class<T> clazz;
	private DbrunkService databaseService;

	/**
	 * Instantiates a new abstract repository.
	 *
	 * @param clazz
	 *          the clazz
	 * @param useDefault
	 *          the use default
	 */
	public AbstractRepository(Class<T> clazz, boolean useDefault)
	{
		if (useDefault)
		{
			this.databaseService = DbrunkService.getInstance();
		}
		this.clazz = clazz;
	}

	/**
	 * Instantiates a new abstract repository.
	 *
	 * @param clazz
	 *          the clazz
	 * @param databaseService
	 *          the database service
	 */
	public AbstractRepository(Class<T> clazz, DbrunkService databaseService)
	{
		this.databaseService = databaseService;
		this.clazz = clazz;
	}

	/**
	 * Sets the dbrunk service.
	 *
	 * @param databaseService
	 *          the new dbrunk service
	 */
	protected void setDbrunkService(DbrunkService databaseService)
	{
		this.databaseService = databaseService;
	}

	/**
	 * Gets the dbrunk service.
	 *
	 * @return the dbrunk service
	 */
	protected DbrunkService getDbrunkService()
	{
		return this.databaseService;
	}

	/**
	 * Creates or updates entity.
	 *
	 * @param entity
	 *          the entity
	 * @return the t
	 */
	public T save(T entity)
	{
		return getDbrunkService().save(clazz, entity);
	}

	/**
	 * Finds entity by <code>id</code>.
	 *
	 * @param id
	 *          the id
	 * @return the t
	 */
	public T find(Long id)
	{
		return getDbrunkService().find(clazz, id);
	}

	/**
	 * Finds all entities in table.
	 *
	 * @return the list
	 */
	public List<T> findAll()
	{
		return getDbrunkService().findAll(clazz);
	}

	/**
	 * Removes the entity by <code>id</code>.
	 *
	 * @param id
	 *          the id
	 */
	public void remove(Long id)
	{
		getDbrunkService().remove(clazz, id);
	}

	/**
	 * Removes the entity.
	 *
	 * @param entity
	 *          the entity
	 */
	public void remove(T entity)
	{
		getDbrunkService().remove(clazz, entity);
	}

	/**
	 * Removes the all.
	 */
	public void removeAll()
	{
		getDbrunkService().removeAll(clazz);
	}

}
