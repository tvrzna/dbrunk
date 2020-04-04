package cz.tvrzna.dbrunk.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class Reflections.
 *
 * @author michalt
 */
public class Reflections
{

	private Reflections()
	{
	}

	/**
	 * Find annotated fields.
	 *
	 * @param <T>
	 *          the generic type
	 * @param obj
	 *          the obj
	 * @param annoClazz
	 *          the anno clazz
	 * @return the field[]
	 */
	public static <T extends Annotation> Field[] findAnnotatedFields(Object obj, Class<T> annoClazz)
	{
		List<Field> fields = new ArrayList<>();
		Class<?> clazz = obj.getClass();
		while (clazz != null)
		{
			for (Field field : clazz.getDeclaredFields())
			{
				if (field.isAnnotationPresent(annoClazz))
				{
					fields.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}
}
