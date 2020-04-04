package cz.tvrzna.dbrunk.helpers;

import cz.tvrzna.dbrunk.DbrunkService;
import cz.tvrzna.dbrunk.repositories.AbstractRepository;

public class UserRepository extends AbstractRepository<UserEntity>
{
	private static final long serialVersionUID = 8498953619322356145L;

	public UserRepository(DbrunkService databaseService)
	{
		super(UserEntity.class, databaseService);
	}

}
