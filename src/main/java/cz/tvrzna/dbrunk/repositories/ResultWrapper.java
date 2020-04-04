package cz.tvrzna.dbrunk.repositories;

/**
 * The Class ResultWrapper.
 *
 * @author michalt
 * @param <T>
 *          the generic type
 */
public class ResultWrapper<T extends AbstractEntity>
{
	private T value;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public T getValue()
	{
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *          the new value
	 */
	public void setValue(T value)
	{
		this.value = value;
	}
}
