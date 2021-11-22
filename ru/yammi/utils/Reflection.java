package ru.yammi.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Reflection {

    private static List<HashedField> fields = new ArrayList<HashedField>();
    private static List<HashedMethod> methods = new ArrayList<HashedMethod>();

    public static Method getMethod(Class cl, Class[] params, String... names) {
        for(String s : names){
            HashedMethod hashedMethod = findMethod(cl,s + params.length);
            if(hashedMethod != null)
                return hashedMethod.getMethod();
            else {
                for(Method m : cl.getDeclaredMethods()) {
                    m.setAccessible(true);

                    if(m.getName().equals(s)){
                        for(int i = 0; i <  m.getParameterTypes().length; i++) {
                            Class clz = m.getParameterTypes()[0];
                            if(!clz.getName().equals(params[i].getName()))
                                return null;
                        }
                        methods.add(new HashedMethod(m, cl, m.getName() + params.length));
                        return m;
                    }
                }
            }
        }

        return null;
    }

    public static Method getMethod(Class cl, int ParameterCount, String... names) {
        for(String s : names){
            HashedMethod hashedMethod = findMethod(cl,s + ParameterCount);
            if(hashedMethod != null)
                return hashedMethod.getMethod();
            else {
                for(Method m : cl.getDeclaredMethods()) {
                    m.setAccessible(true);

                    if(m.getName().equals(s) && m.getParameterCount() == ParameterCount){
                        methods.add(new HashedMethod(m, cl, m.getName() + ParameterCount));
                        return m;
                    }
                }
            }
        }

        return null;
    }

    public static Method getMethod(Class<?> target, String... names){
        for(String s : names){
            HashedMethod hashedMethod = findMethod(target, s);
            if(hashedMethod != null)
                return hashedMethod.getMethod();
            else {
                for(Method method : target.getDeclaredMethods()){
                    method.setAccessible(true);
                    if(method.getName().equals(s)){
                        methods.add(new HashedMethod(method, target, s));
                        return method;
                    }
                }
            }
        }
        return null;
    }

    public static Field getField(Class<?> target, String...names) {
        for(String s : names){
            HashedField hashedField = findField(target, s);
            if(hashedField != null)
                return hashedField.getField();
            else {
                for(Field f : target.getDeclaredFields()){
                    f.setAccessible(true);
                    if(f.getName().equals(s)) {
                        fields.add(new HashedField(target, f, s));
                        return f;
                    }
                }
            }
        }
        return null;
    }

    private static HashedMethod findMethod(Class<?> declared, String name){
        for(HashedMethod hashedMethod : methods) {
            int hash = 0;
            for(char c : name.toCharArray()){
                hash += c ^ 16;
            }
            for(char c : declared.getName().toCharArray()){
                hash += c ^ 16;
            }
            if(hashedMethod.getHash() == hash)
                return hashedMethod;
        }
        return null;
    }

    private static HashedField findField(Class<?> declared, String name){
        for(HashedField hashedField : fields) {
            if(hashedField != null) {
                int hash = 0;
                for (char c : name.toCharArray()) {
                    hash += c ^ 16;
                }
                for (char c : declared.getName().toCharArray()) {
                    hash += c ^ 16;
                }
                if (hashedField.getHash() == hash)
                    return hashedField;
            }
        }
        return null;
    }

    public static class HashedMethod {
        private int hash;
        private Method method;

        public HashedMethod(Method method, Class<?> declared, String name) {
            for(char c : name.toCharArray()){
                hash += c ^ 16;
            }
            for(char c : declared.getName().toCharArray()) {
                hash += c ^ 16;
            }
            this.method = method;
        }

        public int getHash() {
            return hash;
        }

        public Method getMethod() {
            return method;
        }
    }

    public static class HashedField {

        private int hash;
        private Field field;

        public HashedField(Class<?> declared, Field field, String name) {
            for(char c : name.toCharArray()){
                hash += c ^ 16;
            }
            for(char c : declared.getName().toCharArray()){
                hash += c ^ 16;
            }
            this.field = field;
        }

        public int getHash() {
            return hash;
        }

        public Field getField() {
            return field;
        }
    }

}
