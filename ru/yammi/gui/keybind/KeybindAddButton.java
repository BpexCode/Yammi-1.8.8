package ru.yammi.gui.keybind;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import ru.yammi.Yammi;
import ru.yammi.gui.font.FontGL11;
//import ru.yammi.module.misc.KeybindManager;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.MouseUtils;
import ru.yammi.utils.R2DUtils;

public class KeybindAddButton {

    private Minecraft mc = Minecraft.getMinecraft();
    private Key key;

    private MouseUtils mouseUtils = new MouseUtils(250L);

    public KeybindAddButton(Key key) {
        this.key = key;
    }

    public void draw(int x, int y) {
        String text = "Add";

        FontGL11 font = Yammi.getInstance().getModuleFont();;
        x += 45 - font.getStringWidth(text);
        y += 35 * 4 - 16;
        font.drawString(text, x, y, ColorUtils.TAB_SEPARATOR_COLOR);

        boolean hovered = isButtonHovered(x, y, font.getStringWidth(text));
        //hovered = Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().isLockControls() ? false : hovered;
        if (hovered) {
            R2DUtils.drawRect(x, y + 12, x + font.getStringWidth(text), y + 13, ColorUtils.TAB_SEPARATOR_COLOR);

            if (mouseUtils.isMouseButtonDown(0)) {
               // Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().getKeybindSelectPanel().setKey(this.key);
            }
        }
    }

    private boolean isButtonHovered(int x, int y, int width) {
        int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
        int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
        int startSettingsBoxPosX = x;
        int startSettingsBoxPosY = y;
        int endSettingsBoxPosX = x + width;
        int endSettingsBoxPosY = y + 11;
        boolean hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
        return hovered;
    }

    public Key getKey() {
        return key;
    }
}
