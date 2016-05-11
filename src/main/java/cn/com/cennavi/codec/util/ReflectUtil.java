package cn.com.cennavi.codec.util;

import java.lang.reflect.Field;

import cn.com.cennavi.codec.core.annotation.CoderItem;
public class ReflectUtil {

	public static Field getClassField(String fieldId, Class<?> clazz) {
		if (fieldId == null) {
			return null;
		}
		Class<?> thiz = clazz;
		for (; !thiz.equals(Object.class); thiz = thiz.getSuperclass()) {
			for (Field field : thiz.getDeclaredFields()) {
				if (field.isAnnotationPresent(CoderItem.class)) {
					CoderItem ei = field.getAnnotation(CoderItem.class);
					if (ei.id().equalsIgnoreCase(fieldId)) {
						return field;
					}
				}

			}
		}
		return null;
	}

	public static boolean isInstanceOf(Class<?> thiz, Class<?> clazz) {
		if (clazz == null || thiz == null) {
			return false;
		}
		String clazzName = clazz.getName();
		if (clazz.isInterface()) {
			for (; !thiz.equals(Object.class); thiz = thiz.getSuperclass()) {
				for (Class<?> inter : thiz.getInterfaces()) {
					if (inter.getName().equalsIgnoreCase(clazzName)) {
						return true;
					}
				}
			}
		} else {
			for (; !thiz.equals(Object.class); thiz = thiz.getSuperclass()) {
				if (thiz.getName().equalsIgnoreCase(clazzName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isInstanceOf(Field field, Class<?> clazz) {
		if (clazz == null || field == null) {
			return false;
		}
		Class<?> thiz = field.getType();
		return isInstanceOf(thiz, clazz);
	}

	public static Object createObject(String className) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
	}

	public static Object createObject(Class<?> clazz) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return Thread.currentThread().getContextClassLoader().loadClass(clazz.getName())
				.newInstance();
	}

}
