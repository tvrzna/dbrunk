package cz.tvrzna.dbrunk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cz.tvrzna.dbrunk.annotations.Entity;
import cz.tvrzna.dbrunk.annotations.Id;
import cz.tvrzna.dbrunk.exceptions.DbrunkDbException;
import cz.tvrzna.dbrunk.exceptions.DbrunkInitException;
import cz.tvrzna.dbrunk.helpers.UserEntity;
import cz.tvrzna.dbrunk.helpers.UserRepository;
import cz.tvrzna.dbrunk.repositories.AbstractEntity;
import cz.tvrzna.dbrunk.utils.DbrunkType;

public class DbrunkServiceTest
{
	private DbrunkService service;

	@BeforeEach
	public void setUp() throws DbrunkInitException
	{
		service = new DbrunkService();
		service.init(DbrunkType.MEMORY, null);

		if (service.isAutocommit())
		{
			service.setAutocommit(false);
		}
	}

	@AfterEach
	public void tearDown() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		if (service != null)
		{
			Field dbField = DbrunkService.class.getDeclaredField("db");
			dbField.setAccessible(true);
			Database db = (Database) dbField.get(service);
			db.close();
		}
	}

	@Test
	public void testInitMemoryDb() throws DbrunkInitException
	{
		service = new DbrunkService();
		service.init(DbrunkType.MEMORY, null);
	}

	@Test
	public void testInitMemoryGzip() throws DbrunkInitException
	{
		service = new DbrunkService();
		service.init(DbrunkType.GZIP, "test-db-service.db");

		File dbFile = new File("test-db-service.db");
		if (dbFile.exists())
		{
			dbFile.delete();
		}
	}

	@Test
	public void testInitMemoryFile() throws DbrunkInitException
	{
		service = new DbrunkService();
		service.init(DbrunkType.FILE, "test-db-service.db");

		File dbFile = new File("test-db-service.db");
		if (dbFile.exists())
		{
			dbFile.delete();
		}
	}

	@Test
	public void testSaveCreateUser()
	{
		UserEntity user = new UserEntity();
		user.setUserName("username");
		user.setPassword("password");

		assertNotNull(service.save(UserEntity.class, user));
	}

	@Test
	public void testSaveUpdateUser()
	{
		UserRepository repo = new UserRepository(service);

		UserEntity user = new UserEntity();
		user.setId(150l);
		user.setUserName("username");
		user.setPassword("password");

		assertEquals(user, repo.save(user));
	}

	@Test
	public void testFindUser()
	{
		UserRepository repo = new UserRepository(service);

		UserEntity user = new UserEntity();
		user.setId(150l);
		user.setUserName("username");
		user.setPassword("password");
		service.save(UserEntity.class, user);
		service.commit();

		assertEquals(user, repo.find(150l));
		assertNotEquals(0, repo.findAll().size());
	}

	@Test
	public void testFindAll()
	{
		UserRepository repo = new UserRepository(service);

		assertEquals(0, repo.findAll().size());
	}

	@Test
	public void testRemoveById()
	{
		UserRepository repo = new UserRepository(service);

		UserEntity user = new UserEntity();
		user.setId(150l);
		user.setUserName("username");
		user.setPassword("password");
		repo.save(user);

		repo.remove(150l);

		assertEquals(0, repo.findAll().size());
	}

	@Test
	public void testRemoveByEntity()
	{
		UserRepository repo = new UserRepository(service);

		UserEntity user = new UserEntity();
		user.setId(150l);
		user.setUserName("username");
		user.setPassword("password");
		repo.save(user);

		repo.remove(user);

		assertEquals(0, repo.findAll().size());
	}

	@Test
	public void testSaveToNotEntity1()
	{
		assertThrows(DbrunkDbException.class, () -> service.save(DummyEntity1.class, new DummyEntity1()));
	}

	@Test
	public void testSaveToNotEntity2()
	{
		assertThrows(DbrunkDbException.class, () -> service.save(DummyEntity2.class, new DummyEntity2()));
	}

	@Test
	public void testSaveToNotEntity3()
	{
		assertThrows(DbrunkDbException.class, () -> service.save(DummyEntity3.class, new DummyEntity3()));
	}

	@Test
	public void testRotateIdGenerate()
	{
		for (int i = 0; i < 10050; i++)
		{
			UserEntity entity = new UserEntity();
			entity.setUserName("user-".concat(Integer.toString(i)));
			service.save(UserEntity.class, entity);
		}
		service.removeAll(UserEntity.class);
	}

	@Entity("dummy-entity-1")
	private class DummyEntity1 extends AbstractEntity
	{
		private static final long serialVersionUID = 1L;
	}

	@Entity("dummy-entity-2")
	private class DummyEntity2 extends AbstractEntity
	{
		private static final long serialVersionUID = 1L;
		@Id
		private Long id1;
		@Id
		private Long id2;
	}

	private class DummyEntity3 extends AbstractEntity
	{
		private static final long serialVersionUID = 1L;
	}
}
