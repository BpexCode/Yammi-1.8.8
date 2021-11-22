package ru.yammi.gui.keybind;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.gui.animation.OpenScreenAnimation;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.module.Module;
import ru.yammi.utils.*;

import java.awt.*;

public class KeybindPanel {

    private Key key;
    private OpenScreenAnimation openScreenAnimation = new OpenScreenAnimation();

    private KeybindAddButton keybindAddButton;
    private Minecraft mc = Minecraft.getMinecraft();
    private MouseUtils mouseUtils = new MouseUtils(250L);

    public static TimerUtils lockTimer = new TimerUtils(300L);
    public static boolean lockControls = false;

    public static boolean removeButtonHovered = false;

    public KeybindPanel(Key key) {
        this.key = key;
        this.keybindAddButton = new KeybindAddButton(key);
    }

    public void draw(int x, int y) {
        openScreenAnimation.beginDraw();

        int color = ColorUtils.KEYBIND_PANEL_BACKGROUND_COLOR;
        drawLine(x - 10, y, 10, -10, color);
        drawLine(x + 10, y, -10, -10, color);

        for(int i = 0; i < 9; i++) {
            R2DUtils.drawRect(x - i - 1, y - (10 - i), x + i + 1, y, color);
        }

        R2DUtils.drawRect(x - 51, y + 35 * 4, x + 51, y + 35 * 4 + 2, ColorUtils.KEYBIND_PANEL_BACKGROUND_SHADOW_COLOR);

        R2DUtils.drawFullCircle(x - 51, y + 35 * 4 - 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_PANEL_BACKGROUND_SHADOW_COLOR, true);
        R2DUtils.drawFullCircle(x + 51, y + 35 * 4 - 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_PANEL_BACKGROUND_SHADOW_COLOR, true);

        R2DUtils.drawRect(x - 51, y, x + 51, y + 4, color);
        R2DUtils.drawRect(x - 55, y + 4, x + 55, y + 35 * 4 - 4, color);
        R2DUtils.drawRect(x - 51, y + 35 * 4 - 4, x + 51, y + 35 * 4, color);

        R2DUtils.drawFullCircle(x - 51, y + 4, PositionUtils.CIRCLE_RADIUS, color, true);
        R2DUtils.drawFullCircle(x + 51, y + 4, PositionUtils.CIRCLE_RADIUS, color, true);
        R2DUtils.drawFullCircle(x - 51, y + 35 * 4 - 4, PositionUtils.CIRCLE_RADIUS, color, true);
        R2DUtils.drawFullCircle(x + 51, y + 35 * 4 - 4, PositionUtils.CIRCLE_RADIUS, color, true);

        FontGL11 font = Yammi.getInstance().getNotificationsFont();
        font.drawString(Keyboard.getKeyName(key.getKeyID()) + " key", x - 45, y + 10, Color.WHITE.getRGB());

        R2DUtils.drawRect(x - 45, y + 30, x + 45, y + 31, ColorUtils.KEYBIND_PANEL_SEPARATOR_COLOR);

        int yPos = 0;

        boolean lock = false;
        for(Module module : this.key.getBindedModules()) {
            R2DUtils.drawRect(x + 39, y + 39 + yPos * 13, x + 43, y + 39 + yPos * 13 + 1, ColorUtils.TAB_SEPARATOR_COLOR);
            Yammi.getInstance().getOptionsFont().drawString(module.getName(), x - 45, y + 35 + yPos * 13, -1);

            if(isHovered(x + 39, y + 39 + yPos * 13, 3)) {
                removeButtonHovered = true;
                lock = true;
                if(mouseUtils.isMouseButtonDown(0)) {
                    lockControls = true;
                    lockTimer.setPause();
                    this.key.getBindedModules().remove(module);
                }
            }
            yPos++;
        }

        if(!lock)
            removeButtonHovered = false;

        keybindAddButton.draw(x, y);
        openScreenAnimation.endDraw();
    }

    private boolean isHovered(int x, int y, int width) {
        int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
        int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
        int startSettingsBoxPosX = x;
        int startSettingsBoxPosY = y;
        int endSettingsBoxPosX = x + width;
        int endSettingsBoxPosY = y + 1;
        boolean hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
        return hovered;
    }

    private void drawLine(final int x, final int y, final int xLen, final int yLen, int color) {
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(f, f1, f2, f3);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2i(x, y);
        GL11.glVertex2i(x + xLen, y + yLen);
        GL11.glEnd();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void onClose(){
        openScreenAnimation.onCloseScreenNoAlpha();
    }

    public void onOpen(){
        openScreenAnimation.onOpenScreen();
    }

    public Key getKey() {
        return key;
    }

}
