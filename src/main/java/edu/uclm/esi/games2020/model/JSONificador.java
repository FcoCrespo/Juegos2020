package edu.uclm.esi.games2020.model;

import java.util.List;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;



public class JSONificador {
	
	
	private JSONificador() {
		
	}
	private static final  Logger log = Logger.getLogger(JSONificador.class.getName());
	public static JSONObject toJSON(Object object) {
		JSONObject jso = new JSONObject();
		Class clazz = object.getClass();
		ArrayList<Field> fields = new ArrayList<>();
		loadCampos(clazz, fields);
		for (Field field : fields) {
			field.setAccessible(true);
			if (!esJSONificable(field))
				continue;
			try {
				Object valor = field.get(object);
				if (field.getType().isAssignableFrom(List.class)) {
					jso.put(field.getName(), toJSONArray(valor));
				} else if (esPrimitivo(field.getType())) {
					jso.put(field.getName(), valor);
				} else {
					jso.put(field.getName(), toJSON(valor));
				}
			} catch (Exception e) {
					log.info("Error al realizar el JSON");
			}
		}
		return jso;
	}

	private static boolean esPrimitivo(Class<?> type) {
		if (type.isPrimitive())
			return true;
		if (type==String.class || type==Integer.class || type==Float.class ||
				type==Double.class || type==Long.class || type==Boolean.class ||
				type==Byte.class || type==Short.class || type==Character.class)
			return true;
        return type.isEnum();
    }

	private static JSONArray toJSONArray(Object valor) {
		List list = (List) valor;
		JSONArray jsa = new JSONArray();
		for (Object elemento : list)
			jsa.put(toJSON(elemento));
		return jsa;
	}

	private static void loadCampos(Class clazz, ArrayList<Field> fields) {
		Field[] camposDeEstaClase = clazz.getDeclaredFields();
		for (Field campo : camposDeEstaClase)
			fields.add(campo);
		if (clazz.getSuperclass()!=Object.class)
			loadCampos(clazz.getSuperclass(), fields);
	}

	private static boolean esJSONificable(Field field) {
		return field.getAnnotation(NoJSON.class)==null;
	}

	
}
