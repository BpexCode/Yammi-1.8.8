package ru.yammi.gui.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.module.Module;
//import ru.yammi.module.misc.KeybindManager;
import ru.yammi.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Key {

    private static Key lastKey = null;

    private Minecraft mc = Minecraft.getMinecraft();

    private String text;
    private int keyID;
    private ResourceLocation imageLocation;

    private int width = 0;

    private MouseUtils mouseUtils = new MouseUtils(250L);
    private TimerUtils timerUtils = new TimerUtils(50L);

    private int yOffset = 0;
    private boolean opened = false;

    private KeybindPanel keybindPanel;

    private List<Module> bindedModules = new CopyOnWriteArrayList<>();

    public Key(String text, int keyID) {
        this.text = text;
        this.keyID = keyID;

        this.width = 30;
        this.keybindPanel = new KeybindPanel(this);
        for(Module module : Yammi.getInstance().getModules()) {
            if(module.getKeybind() == keyID) {
                this.bindedModules.add(module);
            }
        }
    }

    public Key(ResourceLocation imageLocation, int keyID) {
        this.imageLocation = imageLocation;
        this.keyID = keyID;
        this.keybindPanel = new KeybindPanel(this);

        for(Module module : Yammi.getInstance().getModules()) {
            if(module.getKeybind() == keyID) {
                this.bindedModules.add(module);
            }
        }
    }

    public void draw(int x, int yPos) {
        FontGL11 font = Yammi.getInstance().getModuleFont();

        boolean hovered = isKeyHovered(x, yPos, this.getWidth());
        //hovered = Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().isLockControls() ? false : hovered;

        if(!KeybindPanel.removeButtonHovered) {
            if (hovered) {
                if(KeybindPanel.lockControls) {
                    if(KeybindPanel.lockTimer.isTimeReached()) {
                        KeybindPanel.lockControls = false;
                    }
                }
                if(!KeybindPanel.lockControls) {
                    if (mouseUtils.isMouseButtonDown(0)) {
                        if (lastKey != null) {
                            if (lastKey != this)
                                lastKey.close();
                        }
                        lastKey = this;
                        opened = !opened;

                        if (opened) {
                            keybindPanel.onOpen();
                        } else {
                            keybindPanel.onClose();
                        }

                        this.runAnimation();
                    }
                }
            }
        }

        if(timerUtils.isTimeReached())
            yOffset = 0;

        R2DUtils.drawRect(x + 4, yPos + 28 + yOffset, x + this.getWidth() - 4, yPos + 32+ yOffset, ColorUtils.TAB_SEPARATOR_COLOR);

        R2DUtils.drawFullCircle(x + 4, yPos + 28+ yOffset, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_SEPARATOR_COLOR, true);
        R2DUtils.drawFullCircle(x + this.getWidth() - 4, yPos + 28+ yOffset, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_SEPARATOR_COLOR, true);

        R2DUtils.drawRect(x + 4, yPos+ yOffset, x + this.getWidth() - 4, yPos + 8+ yOffset, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR);
        R2DUtils.drawRect(x, yPos + 4+ yOffset, x + this.getWidth(), yPos + 26+ yOffset, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR);
        R2DUtils.drawRect(x + 4, yPos + 22+ yOffset, x + this.getWidth() - 4, yPos + 30+ yOffset, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR);

        R2DUtils.drawFullCircle(x + 4, yPos + 4+ yOffset, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + this.getWidth() - 4, yPos + 4+ yOffset, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + 4, yPos + 26+ yOffset, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + this.getWidth() - 4, yPos + 26+ yOffset, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_KEY_BACKGROUND_COLOR, true);

        if(this.getText() != null) {
            int strX = (x + this.getWidth() / 2) - font.getStringWidth(this.getText()) / 2;
            font.drawStringWithShadow(this.getText(), strX, yPos + 12 + yOffset, -1);
        } else {
            drawImage(this.imageLocation, x + this.getWidth() / 2 - 6, yPos + 9 + yOffset);
        }

        if(opened) {
            keybindPanel.draw(x + this.getWidth() / 2, yPos + 35);
        }
    }

    public void syncKey() {
        if(lastKey != this) {
            if(lastKey != null)
                lastKey.close();
        }
        lastKey = this;
        opened = !opened;

        if(opened) {
            keybindPanel.onOpen();
        } else {
            keybindPanel.onClose();
        }
    }

    public void runAnimation(){
        yOffset = 1;
        timerUtils.setPause();
    }

    private void drawImage(ResourceLocation resourceLocation, int x, int y){
        GL11.glPushMatrix();

        this.mc.getTextureManager().bindTexture(resourceLocation);
        this.mc.getTextureManager().getTexture(resourceLocation).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f ,15, 15, 15, 15);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GL11.glPopMatrix();
    }

    private boolean isKeyHovered(int x, int y, int width) {
        int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
        int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
        int startSettingsBoxPosX = x;
        int startSettingsBoxPosY = y;
        int endSettingsBoxPosX = x + width;
        int endSettingsBoxPosY = y + 30;
        boolean hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
        return hovered;
    }

    public String getText() {
        return text;
    }

    public Key withWidth(int width){
        this.width = width;
        return this;
    }

    public void open() {
        this.opened = true;
    }

    public void close(){
        this.opened = false;
        keybindPanel.onClose();
    }

    public int getWidth() {
        return width;
    }

    public List<Module> getBindedModules() {
        return bindedModules;
    }

    public int getKeyID() {
        return keyID;
    }
}
