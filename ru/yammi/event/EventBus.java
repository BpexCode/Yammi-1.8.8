package ru.yammi.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

    private static List<EventHandle> eventHandles = new CopyOnWriteArrayList<>();

    public static void clear(){
        eventHandles.clear();
    }

    public static void call(Event event) {
        for(EventHandle eventHandle : eventHandles){
            for(Method m : eventHandle.getMethods()){
                if(m.getParameterTypes()[0] == event.getClass()){
                    try {
                        m.invoke(eventHandle.getHandle(), event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void register(Object obj){
        try {
            List<Method> methods = new ArrayList<Method>();
            for(Method method : obj.getClass().getDeclaredMethods()){
                method.setAccessible(true);
                if (method.isAnnotationPresent(EventTarget.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1)
                    {
                        throw new IllegalArgumentException(
                                "Method " + method + " has @EventTarget annotation, but requires " + parameterTypes.length +
                                        " arguments.  Event handler methods must require a single argument."
                        );
                    }
                    Class<?> eventType = parameterTypes[0];

                    if (!Event.class.isAssignableFrom(eventType))
                    {
                        throw new IllegalArgumentException("Method " + method + " has @EventTarget annotation, but takes a argument that is not an Event " + eventType);
                    }
                    methods.add(method);
                }
            }
            if(methods.size() > 0){
                eventHandles.add(new EventHandle(obj, methods));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final class EventHandle {

        private Object handle;
        private List<Method> methods = new ArrayList<Method>();

        public EventHandle(Object handle, List<Method> methods) {
            this.handle = handle;
            this.methods = methods;
        }

        public Object getHandle() {
            return handle;
        }

        public void setHandle(Object handle) {
            this.handle = handle;
        }

        public List<Method> getMethods() {
            return methods;
        }

        public void setMethods(List<Method> methods) {
            this.methods = methods;
        }
    }
}
