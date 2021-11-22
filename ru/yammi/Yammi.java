package ru.yammi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.Timer;
import net.xtrafrancyz.covered.ObfValue;
import org.lwjgl.Sys;
import ru.yammi.config.Config;
import ru.yammi.event.EventBus;
import ru.yammi.event.events.PostLoadEvent;
import ru.yammi.gui.YammiScreen;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.hook.MapHook;
import ru.yammi.hook.ProfilerHook;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.misc.ClickGUI;
import ru.yammi.module.movement.Blink;
import ru.yammi.module.render.PlayerESP;
import ru.yammi.utils.*;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;

public class Yammi {

    private static Yammi instance;
    private int version = 16;

    private Minecraft mc = Minecraft.getMinecraft();
    private Timer timer;
    private ProfilerHook profilerHook;

    private List<Module> modules;

    private FontGL11 tabFont;
    private FontGL11 moduleFont;
    private FontGL11 optionsFont;
    private FontGL11 notificationsFont;
    private FontGL11 copyrightFont;

    private List<String> friendList = new ArrayList<>();

    public Yammi(){}

    public void start(List<Object> allObjects){
        this.modules = ModuleUtils.loadModules();
        this.initDepends((Field)allObjects.get(0), (Field)allObjects.get(1), (Field)allObjects.get(2));

        EventBus.call(new PostLoadEvent());
    }

    private void initDepends(Field profilerField, Field timerField, Field defaultResourcePackField) {
        try {
            if(Modifier.isFinal(profilerField.getModifiers())){
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(profilerField, profilerField.getModifiers() & ~Modifier.FINAL);
            }

            profilerHook = new ProfilerHook();
            profilerField.set(this.mc, profilerHook);
            timerField.setAccessible(true);
            timer = (Timer)timerField.get(this.mc);

            /*MapHook mapHook = new MapHook();
            Field damagedBlocksField = Reflection.getField(RenderGlobal.class, "damagedBlocks", "x");
            damagedBlocksField.setAccessible(true);
            if(Modifier.isFinal(damagedBlocksField.getModifiers())){
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(damagedBlocksField, damagedBlocksField.getModifiers() & ~Modifier.FINAL);
            }
            damagedBlocksField.set(this.mc.renderGlobal, mapHook);*/

            File resourcesDir = Resources.unpack();
            Field mapAssetsField = Reflection.getField(DefaultResourcePack.class, "mapAssets", "b");
            mapAssetsField.setAccessible(true);
            Map<String, File> mapAssets = (Map<String, File>)mapAssetsField.get(defaultResourcePackField.get(mc));

            String shadersPath = resourcesDir.getAbsolutePath() + File.separator + "shaders";
            mapAssets.put("minecraft:blurShader", new File(shadersPath + "\\blurInit.json"));
            mapAssets.put("minecraft:shaders/program/blurPost.json", new File(shadersPath + "\\blurPost.json"));
            mapAssets.put("minecraft:shaders/program/blurPost.fsh", new File(shadersPath + "\\blurPost.fsh"));

            String imagesPath = resourcesDir.getAbsolutePath() + File.separator + "images";
            for(Category category : Category.values()) {
                String name = category.name().toLowerCase(Locale.ROOT);
                mapAssets.put("minecraft:" + name + "Image", new File(imagesPath + "\\" + name +".png"));
            }

            mapAssets.put("minecraft:cancelImage", new File(imagesPath + "\\cancel.png"));
            mapAssets.put("minecraft:okImage", new File(imagesPath + "\\ok.png"));
            mapAssets.put("minecraft:keyBack", new File(imagesPath + "\\keyboard\\back.png"));
            mapAssets.put("minecraft:keyEnter", new File(imagesPath + "\\keyboard\\enter.png"));

            Reflection.getField(OpenGlHelper.class, "framebufferSupported", "l").setBoolean(null, true);

            this.getModule(Blink.class).setState(false);
            EventBus.register(new EventUtils());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Timer getTimer(){
        return timer;
    }

    public YammiScreen getScreen(){
        ClickGUI clickGUI = getModule(ClickGUI.class);
        return clickGUI.getClickGUIScreen(false);
    }

    public FontGL11 getNotificationsFont() {
        if(notificationsFont == null) {
            notificationsFont = new FontGL11(new Font("Linux Biolinum", Font.BOLD, 20), true, true);
        }
        return notificationsFont;
    }

    public FontGL11 getOptionsFont() {
        if(optionsFont == null) {
            optionsFont = new FontGL11(new Font("Linux Biolinum", Font.PLAIN, 17), true, true);
        }
        return optionsFont;
    }

    public FontGL11 getModuleFont() {
        if(moduleFont == null) {
            moduleFont = new FontGL11(new Font("Linux Biolinum", Font.PLAIN, 20), true, true);
        }
        return moduleFont;
    }

    public FontGL11 getCopyrightFont() {
        if(copyrightFont == null) {
            copyrightFont = new FontGL11(new Font("Linux Biolinum", Font.BOLD, 30), true, true);
        }
        return copyrightFont;
    }

    public FontGL11 getTabFont() {
        if(tabFont == null) {
            tabFont = new FontGL11(new Font("Linux Biolinum", Font.BOLD, 24), true, true);
        }
        return tabFont;
    }

    public Module getModule(String name) {
        for(Module module : modules) {
            if(module.getName().equals(name))
                return module;
        }
        return null;
    }

    public <T> T getModule(Class<T> moduleClass) {
        for(Module module : modules) {
            if(module.getClass() == moduleClass)
                return (T)module;
        }
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }

    public static Yammi getInstance() {
        return instance;
    }

    public static void main(List<Object> args){
        instance = new Yammi();
        instance.start(args);
    }

    public int getVersion() {
        return version;
    }

    public float getPartialTicks(){
        try {
            Timer timer = this.timer;
            Field renderPartialTicksField = Reflection.getField(timer.getClass(), "renderPartialTicks", "c");
            Class<?> rtype = renderPartialTicksField.getType();
            if(rtype == Float.TYPE) {
                return renderPartialTicksField.getFloat(timer);
            } else {
                if(Modifier.isFinal(renderPartialTicksField.getModifiers())){
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(renderPartialTicksField, renderPartialTicksField.getModifiers() & ~Modifier.FINAL);
                }
                Object c = renderPartialTicksField.get(timer);
                ObfValue.WalkingFloat walkingFloat = (ObfValue.WalkingFloat)c;
                /*Method cGet = ReflectUtils.getMethod(c.getClass(), "get");
                cGet.setAccessible(true);
                return (float)cGet.invoke(c, new Object[0]);*/
                return walkingFloat.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0F;
    }

    public boolean isModuleEnabled(Class<? extends Module> module) {
        Module m = getModule(module);
        return m == null ? false : m.getState();
    }

    public boolean isModuleEnabled(String module) {
        Module m = getModule(module);
        return m == null ? false : m.getState();
    }

    public void disableModule(Class<? extends Module> clazz) {
        Module m = getModule(clazz);
        if(m != null)
            m.setState(true);
        Config.store();
    }

    public static boolean checkRenderOutline(){
        if(instance.getModule(PlayerESP.class).getCurrentMode() == PlayerESP.RenderMode.SHADER) {
            if(instance.mc.thePlayer != null) {
                return true;
            }
        }
        return false;
    }

    public List<String> getFriendList() {
        return friendList;
    }
}
