package edu.uclm.esi.games2020.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Instanciador {

	private static Object crear(Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object object = clazz.newInstance();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Autoejecutable.class)) {
				method.setAccessible(true);
				method.invoke(object);  // object.method();
			}
		}
		return object;
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		User user = (User) Instanciador.crear(User.class);
	}
}
