package org.uutuc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

	public static List<Field> getFields(Object object) {
		Class<?> cls = object.getClass();
		return getFields(cls);
	}

	public static List<Field> getFields(Class<?> cls) {
		List<Field> fields = new ArrayList<Field>();
		while(!cls.equals(Object.class)) {
			Field[] flds = cls.getDeclaredFields();
			fields.addAll(Arrays.asList(flds));
			cls = cls.getSuperclass();
		}
		return fields;
	}

}
