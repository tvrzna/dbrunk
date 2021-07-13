# dbrunk
[![javadoc](https://javadoc.io/badge2/cz.tvrzna/dbrunk/0.1.1/javadoc.svg)](https://javadoc.io/doc/cz.tvrzna/dbrunk/0.1.1)

Minimal and simple file database.

## What is dbrunk good for?
Simple and minimalistic NoSQL database, that stores entities to file and provides basic CRUD operations.

## Installation
```xml
<dependency>
    <groupId>cz.tvrzna</groupId>
    <artifactId>dbrunk</artifactId>
    <version>0.1.1</version>
</dependency>
```

## Using Database interface

### Example

__Main.java__

```java
package test.project;

import java.io.IOException;
import java.util.Date;

import cz.tvrzna.dbrunk.Database;
import cz.tvrzna.dbrunk.DbConcurrentMap;
import cz.tvrzna.dbrunk.databases.FileDatabase;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		// init FileDatabase
		Database db = new FileDatabase("database.db");

		// save entity
		TestEntity entity = new TestEntity();
		entity.setId(1l);
		entity.setName("test");
		entity.setUpdatedDate(new Date(1584613354l));

		// Open table and save entity
		DbConcurrentMap<TestEntity> table = db.createOrOpen("testEntity", TestEntity.class);
		table.put(entity.getId(), entity);
		db.commit();

		// // print content of repo
		table.values().forEach(e -> System.out.format("id: %d\nname: %s\ndate: %s", e.getId(), e.getName(), e.getUpdatedDate()));

		db.close();
	}
}
```

__TestEntity.java__

```java
package test.project;

import java.io.Serializable;
import java.util.Date;


public class TestEntity implements Serializable
{
	private static final long serialVersionUID = -6952319164469394171L;

	private Long id;
	private String name;
	private Date updatedDate;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Date getUpdatedDate()
	{
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}
}
```

## Using DbrunkService

### static vs variable
In example below is used `getInstance()` variant, but there is also constructor for `AbstractRepository`, that allows to use own initialized instance of DbrunkService to avoid using Singleton around whole application.

### Example

__Main.java__

```java
package test.project;

import java.util.Date;

import cz.tvrzna.dbrunk.DbrunkService;
import cz.tvrzna.dbrunk.exceptions.DbrunkInitException;
import cz.tvrzna.dbrunk.utils.DbrunkType;

public class Main
{
	public static void main(String[] args) throws DbrunkInitException
	{
		// init DbrunkService
		DbrunkService service = DbrunkService.getInstance();
		service.init(DbrunkType.FILE, "database.db");

		// init repo
		TestRepository repo = new TestRepository();

		// save entity, let service to generate id
		TestEntity entity = new TestEntity();
		entity.setName("test");
		entity.setUpdatedDate(new Date(1584613354l));
		repo.save(entity);

		// print content of repo
		repo.findAll().forEach(e -> System.out.format("id: %d\nname: %s\ndate: %s", e.getId(), e.getName(), e.getUpdatedDate()));
	}
}
```

__TestEntity.java__

```java
package test.project;

import java.util.Date;

import cz.tvrzna.dbrunk.annotations.Entity;
import cz.tvrzna.dbrunk.annotations.Id;
import cz.tvrzna.dbrunk.repositories.AbstractEntity;

@Entity("testEntity")
public class TestEntity extends AbstractEntity
{
	private static final long serialVersionUID = -6952319164469394171L;

	@Id
	private Long id;
	private String name;
	private Date updatedDate;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Date getUpdatedDate()
	{
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}
}
```

__TestRepository.java__

```java
package test.project;

import cz.tvrzna.dbrunk.repositories.AbstractRepository;

public class TestRepository extends AbstractRepository<TestEntity>
{
	private static final long serialVersionUID = 6936693464016713616L;

	public TestRepository()
	{
		super(TestEntity.class, true);
	}
}
```

__Output result__

```
id: 202003191128250001
name: test
date: Mon Jan 19 09:10:13 CET 1970
```
