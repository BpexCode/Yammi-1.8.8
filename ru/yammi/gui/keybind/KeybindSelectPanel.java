package ru.yammi.gui.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.gui.elements.utility.TextField;
import ru.yammi.gui.elements.utility.TypeList;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.Module;
//import ru.yammi.module.misc.KeybindManager;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.KeyboardUtils;
import ru.yammi.utils.PositionUtils;
import ru.yammi.utils.R2DUtils;

import java.awt.*;

public class KeybindSelectPanel {

    private Key key;
    private boolean opened = false;

    private Minecraft mc = Minecraft.getMinecraft();
    private KeyboardUtils keyboardUtils = new KeyboardUtils(250L);

    private TextField searchField = new TextField();
    private TypeList<Module> typeList;

    private String currentText = "";
    private long time = 0L;

    public KeybindSelectPanel() {
        this.typeList = new TypeList<>(Yammi.getInstance().getModules());
    }

    public void draw(){
        if(keyboardUtils.isKeyDown(Keyboard.KEY_ESCAPE))  {
            this.close();
        }
        FontGL11 font = Yammi.getInstance().getTabFont();

        ScaledResolution scaledResolution = new ScaledResolution(this.mc);

        int centerX = scaledResolution.getScaledWidth() / 2;
        int centerY = scaledResolution.getScaledHeight() / 2;

        int height = 5 * 30;
        int x = centerX - 110;
        int y = centerY - height;
        height *= 2;

        int color = ColorUtils.KEYBIND_PANEL_BACKGROUND_COLOR;

        R2DUtils.drawRect(0, 0, this.mc.currentScreen.width, this.mc.currentScreen.height, new Color(20, 20, 20, 200).getRGB());

        R2DUtils.drawRect(x + 4, y + height, x + 216, y + height + 2, ColorUtils.KEYBIND_PANEL_BACKGROUND_SHADOW_COLOR);
        R2DUtils.drawFullCircle(x + 4, y + height - 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_PANEL_BACKGROUND_SHADOW_COLOR, true);
        R2DUtils.drawFullCircle(x + 216, y + height - 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_PANEL_BACKGROUND_SHADOW_COLOR, true);

        R2DUtils.drawRect(x + 4, y, x + 216, y + 4, color);
        R2DUtils.drawRect(x, y + 4, x + 220, y + height - 4, color);
        R2DUtils.drawRect(x + 4, y + height - 4, x + 216, y + height, color);

        R2DUtils.drawFullCircle(x + 4, y + 4, PositionUtils.CIRCLE_RADIUS, color, true);
        R2DUtils.drawFullCircle(x + 216, y + 4, PositionUtils.CIRCLE_RADIUS, color, true);
        R2DUtils.drawFullCircle(x + 4, y + height - 4, PositionUtils.CIRCLE_RADIUS, color, true);
        R2DUtils.drawFullCircle(x + 216, y + height - 4, PositionUtils.CIRCLE_RADIUS, color, true);

        font.drawString("Select module to bind", x + 10, y + 20, -1);
        searchField.draw(x + 10, y + 45, 200);

        typeList.draw(x + 10, y + 50, 200, height, currentText);
    }

    public void sortModules(String text) {
        currentText = text;
    }

    public void mouseClicked(int xD, int yD) {

    }

    public void setKey(Key key) {
        this.time = System.currentTimeMillis() + 500L;
        this.key = key;
    }

    public void bindModule(Module module) {
        if(!this.key.getBindedModules().contains(module))
            this.key.getBindedModules().add(module);

        module.setKeybind(this.key.getKeyID());
        Config.store();

        //Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().initLockGui();
        this.close();
    }

    public long getTime() {
        return time;
    }

    public void open(){
        searchField.onInit();
        opened = true;
    }

    public void close(){
        this.key = null;
        opened = false;
    }

    public boolean isOpened() {
        return opened;
    }

    public Key getKey() {
        return key;
    }
}
