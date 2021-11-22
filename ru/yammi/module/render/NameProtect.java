package ru.yammi.module.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.PostLoadEvent;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

public class NameProtect extends Module {

    private boolean hooked = false;

    public NameProtect(){
        super("NameProtect", Category.Render, "Zalupa");
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if(!hooked) {
            hooked = true;
            try {
                FontRendererHook fontRendererHook = new FontRendererHook(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), false);
                Field[] fields = FontRenderer.class.getDeclaredFields();
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                for(Field field : fields) {
                    if(Modifier.isFinal(field.getModifiers())){
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    }

                    field.setAccessible(true);
                    field.set(fontRendererHook, field.get(this.mc.fontRendererObj));
                }
                this.mc.fontRendererObj = fontRendererHook;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class FontRendererHook extends FontRenderer {

        private Minecraft mc = Minecraft.getMinecraft();
        private static char[] dictionary = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
        private static Random random = new Random();

        public FontRendererHook(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
            super(gameSettingsIn, location, textureManagerIn, unicode);
        }

        @Override
        public int drawStringWithShadow(String text, float x, float y, int color) {
            if(Yammi.getInstance().getModule(NameProtect.class).getState()) {
                if (text.contains(this.mc.getSession().getUsername())) {
                    text = text.replace(this.mc.getSession().getUsername(), getRandomName(12));
                }
            }
            return super.drawStringWithShadow(text, x, y, color);
        }

        private String getRandomName(int len){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < len; i++){
                stringBuilder.append(dictionary[random.nextInt(dictionary.length)]);
            }
            return stringBuilder.toString();
        }
    }

}
