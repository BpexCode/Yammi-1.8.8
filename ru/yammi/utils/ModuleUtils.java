package ru.yammi.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;
import ru.yammi.Yammi;
import ru.yammi.event.EventBus;
import ru.yammi.module.Module;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ModuleUtils {

    public static List<Module> loadModules(){
        try {
            if(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().contains("atpack")) {
                List<Module> rs = new ArrayList<>();
                File file = new File(Yammi.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                walkDir(rs, file);
                return rs;
            } else {
                Field classesField = ClassLoader.class.getDeclaredField("classes");
                classesField.setAccessible(true);
                Vector<Class> classes = (Vector<Class>) classesField.get(ModuleUtils.class.getClassLoader());
                Class[] copy = classes.toArray(new Class[0]);

                List<Module> moduleList = new ArrayList<Module>();
                for (Class c : copy) {
                    if (c != null && c.getSuperclass() == Module.class) {
                        Module module = (Module) c.newInstance();
                        EventBus.register(module);
                        moduleList.add(module);
                    }
                }
                return moduleList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static void walkDir(List<Module> rs, File file) {
        if(file.isDirectory()){
            File[] files = file.listFiles();
            Arrays.asList(files).stream().forEach(f -> walkDir(rs, f));
        } else {
            if(file.getName().endsWith(".class")) {
                String className = "boba";
                
                try {
                    if(className.startsWith(Module.class.getPackage().getName())) {
                        Class<?> cls = Yammi.class.getClassLoader().loadClass(className);
                        if (cls.getSuperclass() == Module.class) {
                            Module module = (Module) cls.newInstance();
                            EventBus.register(module);
                            rs.add(module);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
