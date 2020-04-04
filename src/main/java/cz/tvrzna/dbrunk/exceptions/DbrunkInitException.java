package cz.tvrzna.dbrunk.exceptions;

/**
 * The Class DbrunkInitException.
 *
 * @author michalt
 */
public class DbrunkInitException extends Exception
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
	public DbrunkInitException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
