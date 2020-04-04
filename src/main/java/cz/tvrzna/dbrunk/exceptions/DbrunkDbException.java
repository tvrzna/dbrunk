package cz.tvrzna.dbrunk.exceptions;

/**
 * The Class DbrunkDbException.
 *
 * @author michalt
 */
public class DbrunkDbException extends RuntimeException
{
	private static final long serialVersionUID = -2635744796645236216L;

	/**
	 * Instantiates a new dbrunk init exception.
	 *
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
	public DbrunkDbException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
