package edu.uclm.esi.games2020.model;

import java.lang.annotation.Annotation;

public class Insertador {

    private static String insert(Object object) {
        Class clazz = object.getClass();
        if (!clazz.isAnnotationPresent(Tabla.class))
            return null;

        Tabla anotacion = (Tabla) clazz.getAnnotation(Tabla.class);
        String nombreTabla = anotacion.tabla();
        String sql = "insert into " + nombreTabla + " (";
        return sql;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setUserName("pepe");
        user.setEmail("pepe@pepe.com");

        String sql = Insertador.insert(user);
    }

}
