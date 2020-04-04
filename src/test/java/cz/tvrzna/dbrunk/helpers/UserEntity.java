package cz.tvrzna.dbrunk.helpers;

import cz.tvrzna.dbrunk.annotations.Entity;
import cz.tvrzna.dbrunk.annotations.Id;
import cz.tvrzna.dbrunk.repositories.AbstractEntity;

/**
 * The Class UserEntity.
 *
 * @author michalt
 */
@Entity("users")
public class UserEntity extends AbstractEntity
{
	private static final long serialVersionUID = 478239487824191315L;

	@Id
	private Long id;
	private String userName;
	private String password;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *          the new id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName
	 *          the new user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *          the new password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

}
