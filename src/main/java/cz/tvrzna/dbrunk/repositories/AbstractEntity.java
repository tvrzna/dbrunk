package cz.tvrzna.dbrunk.repositories;

import java.io.Serializable;
import java.lang.reflect.Field;

import cz.tvrzna.dbrunk.annotations.Id;
import cz.tvrzna.dbrunk.utils.Reflections;

/**
 * The Class AbstractEntity.
 *
 * @author michalt
 */
public abstract class AbstractEntity implements Serializable
{
	private static final long serialVersionUID = 3506228068534759272L;

	/**
	 * Gets the id field.
	 *
	 * @return the id field
	 */
	private Field getIdField()
	{
		Field[] arrFields = Reflections.findAnnotatedFields(this, Id.class);
		if (arrFields.length > 0)
		{
			return arrFields[0];
		}
		return null;
	}

	/**
	 * Gets the id field name.
	 *
	 * @return the id field name
	 */
	private String getIdFieldName()
	{
		Field field = getIdField();
		return field != null ? field.getName() : null;
	}

	/**
	 * Gets the id value.
	 *
	 * @return the id value
	 */
	private Long getIdValue()
	{
		Field field = getIdField();
		if (field != null)
		{
			field.setAccessible(true);
			try
			{
				return (Long) field.get(this);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode()
	{
		int hash = 0;
		hash += (getIdValue() != null ? getIdValue().hashCode() : 0);
		return hash;
	}

	/**
	 * Equals.
	 *
	 * @param object
	 *          the object
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object object)
	{
		if (null == object || !getClass().equals(object.getClass()))
		{
			return false;
		}
		AbstractEntity other = (AbstractEntity) object;
		if ((this.getIdValue() == null && other.getIdValue() != null) || (this.getIdValue() != null && !this.getIdValue().equals(other.getIdValue())))
		{
			return false;
		}
		return true;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString()
	{
		return getClass().getCanonicalName() + "[" + getIdFieldName() + "=" + getIdValue() + "]";
	}
}
