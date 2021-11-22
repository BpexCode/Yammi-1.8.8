package ru.yammi.gui.elements.utility;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import ru.yammi.Yammi;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.gui.keybind.KeybindScreen;
import ru.yammi.module.Module;
//import ru.yammi.module.misc.KeybindManager;
import ru.yammi.utils.MouseUtils;
import ru.yammi.utils.R2DUtils;
import ru.yammi.utils.TimerUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TypeList <T> {

    private static Minecraft mc = Minecraft.getMinecraft();
    public List<T> types;
    private MouseUtils mouseUtils = new MouseUtils(250L);

    private int scrollPos = 0;
    private int scrollMax = 0;

    private TimerUtils timerUtils = new TimerUtils(125L);

    private int scrollAnimatePos = 0;
    private int scrollSmoothPos = 0;
    private int animateTicks = 0;

    public TypeList(List<T> types) {
        this.types = types;
    }

    public void draw(int x, int y, int width, int height, String searchText) {
        y += 20;
        FontGL11 font =  Yammi.getInstance().getModuleFont();

        List<T> neededTypes = new ArrayList<T>();
        for(int i = 0; i < types.size(); i++) {
            T type = types.get(i);
            String text = type.toString();
            if (text.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))) {
                neededTypes.add(type);
            }
        }

        int yEnd = y + height - 80;
        scrollMax = neededTypes.size();

        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if(timerUtils.isTimeReached()) {
                if(scrollPos != (scrollMax - 1)) {
                    scrollPos++;
                    animateTicks = 40;
                }
            }
        } else if (dWheel > 0){
            if(timerUtils.isTimeReached()) {
                if (scrollPos != 0) {
                    scrollPos--;
                    animateTicks = -40;
                }
            }
        }

        List<T> renderTypes = new ArrayList<>();

        if(scrollMax == 0 && neededTypes.size() != 0)
            scrollMax = 1;
        if(scrollMax == 0)
            scrollMax = 1;
        int endTypes = (yEnd - y) / scrollMax;
        for(int i = scrollPos; i < scrollMax; i++) {
            renderTypes.add(neededTypes.get(i));
        }

        for(int i = 0; i < renderTypes.size(); i++) {
            T type = renderTypes.get(i);
            String text = type.toString();

            int yPos = y + i * 15;
            if(yPos < yEnd) {
                font.drawString(text, x, yPos, -1);
                //KeybindScreen keybindScreen = Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen();
                //if (Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().getKeybindSelectPanel().getTime() <= System.currentTimeMillis()) {
                    boolean hovered = isHovered(x, y + i * 15, width);
                    if (hovered) {
                        if (mouseUtils.isMouseButtonDown(0)) {
                            Module module = (Module)type;
                            //Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().getKeybindSelectPanel().bindModule((Module) type);
                        }
                    }
                //}
            }
        }

        R2DUtils.drawFullCircle(x + width - 1,  y + 2, 1, -1, true);
        R2DUtils.drawFullCircle(x + width - 1,  y + height - 80, 1, -1, true);
        R2DUtils.drawRect(x + width - 2,  y + 2, x + width, y + height - 80, -1);

        if(scrollMax == 0)
            scrollMax = 1;

        int delimiter = (height - 84) / scrollMax;
        int distance = scrollPos * delimiter;

        R2DUtils.drawRect(x + width - 2,  y + 2 + distance, x + width, y + 2 + distance + 10, Color.BLACK.getRGB());
    }

    private boolean isHovered(int x, int y, int width) {
        int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
        int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
        int startSettingsBoxPosX = x;
        int startSettingsBoxPosY = y;
        int endSettingsBoxPosX = x + width;
        int endSettingsBoxPosY = y + 9;
        boolean hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
        return hovered;
    }

}
