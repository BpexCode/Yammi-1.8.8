package ru.yammi.gui.keybind;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ru.yammi.Yammi;
import ru.yammi.gui.animation.OpenScreenAnimation;
import ru.yammi.gui.elements.IElement;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.utils.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class KeybindScreen extends GuiScreen {

    private ShaderUtils shaderUtils = new ShaderUtils();
    private OpenScreenAnimation screenAnimation = new OpenScreenAnimation();
    private NotificationSystem notificationSystem = new NotificationSystem(4);

    private KeybindSelectPanel keybindSelectPanel = new KeybindSelectPanel();
    private List<Key> firstLineKeys = new ArrayList<>();
    private List<Key> secondLineKeys = new ArrayList<>();
    private List<Key> thirdLineKeys = new ArrayList<>();
    private List<Key> fourthLineKeys = new ArrayList<>();
    private List<Key> fifthLineKeys = new ArrayList<>();

    private TimerUtils initLockTimer = new TimerUtils(300L);

    private boolean guiInitLock = false;
    private boolean lockControls = false;

    private boolean isVimeworld = false;

    public KeybindScreen() {
        firstLineKeys.add(new Key("`", Keyboard.KEY_GRAVE));
        firstLineKeys.add(new Key("1", Keyboard.KEY_1));
        firstLineKeys.add(new Key("2", Keyboard.KEY_2));
        firstLineKeys.add(new Key("3", Keyboard.KEY_3));
        firstLineKeys.add(new Key("4", Keyboard.KEY_4));
        firstLineKeys.add(new Key("5", Keyboard.KEY_5));
        firstLineKeys.add(new Key("6", Keyboard.KEY_6));
        firstLineKeys.add(new Key("7", Keyboard.KEY_7));
        firstLineKeys.add(new Key("8", Keyboard.KEY_8));
        firstLineKeys.add(new Key("9", Keyboard.KEY_9));
        firstLineKeys.add(new Key("0", Keyboard.KEY_0));
        firstLineKeys.add(new Key("-", Keyboard.KEY_MINUS));
        firstLineKeys.add(new Key("=", Keyboard.KEY_EQUALS));
        firstLineKeys.add(new Key(new ResourceLocation("minecraft", "keyBack"), Keyboard.KEY_BACK).withWidth(60));

        secondLineKeys.add(new Key("Tab", Keyboard.KEY_TAB).withWidth(50));
        secondLineKeys.add(new Key("Q", Keyboard.KEY_Q));
        secondLineKeys.add(new Key("W", Keyboard.KEY_W));
        secondLineKeys.add(new Key("E", Keyboard.KEY_E));
        secondLineKeys.add(new Key("R", Keyboard.KEY_R));
        secondLineKeys.add(new Key("T", Keyboard.KEY_T));
        secondLineKeys.add(new Key("Y", Keyboard.KEY_Y));
        secondLineKeys.add(new Key("U", Keyboard.KEY_U));
        secondLineKeys.add(new Key("I", Keyboard.KEY_I));
        secondLineKeys.add(new Key("O", Keyboard.KEY_O));
        secondLineKeys.add(new Key("P", Keyboard.KEY_P));
        secondLineKeys.add(new Key("[", Keyboard.KEY_LBRACKET));
        secondLineKeys.add(new Key("]", Keyboard.KEY_RBRACKET));
        secondLineKeys.add(new Key("\\", Keyboard.KEY_BACKSLASH).withWidth(40));

        thirdLineKeys.add(new Key("Caps Lock", Keyboard.KEY_CAPITAL).withWidth(60));
        thirdLineKeys.add(new Key("A", Keyboard.KEY_A));
        thirdLineKeys.add(new Key("S", Keyboard.KEY_S));
        thirdLineKeys.add(new Key("D", Keyboard.KEY_D));
        thirdLineKeys.add(new Key("F", Keyboard.KEY_F));
        thirdLineKeys.add(new Key("G", Keyboard.KEY_G));
        thirdLineKeys.add(new Key("H", Keyboard.KEY_H));
        thirdLineKeys.add(new Key("J", Keyboard.KEY_J));
        thirdLineKeys.add(new Key("K", Keyboard.KEY_K));
        thirdLineKeys.add(new Key("L", Keyboard.KEY_L));
        thirdLineKeys.add(new Key(";", Keyboard.KEY_SEMICOLON));
        thirdLineKeys.add(new Key("'", Keyboard.KEY_APOSTROPHE));
        thirdLineKeys.add(new Key(new ResourceLocation("minecraft", "keyEnter"), Keyboard.KEY_RETURN).withWidth(65));

        fourthLineKeys.add(new Key("Shift", Keyboard.KEY_LSHIFT).withWidth(80));
        fourthLineKeys.add(new Key("Z", Keyboard.KEY_Z));
        fourthLineKeys.add(new Key("X", Keyboard.KEY_X));
        fourthLineKeys.add(new Key("C", Keyboard.KEY_C));
        fourthLineKeys.add(new Key("V", Keyboard.KEY_V));
        fourthLineKeys.add(new Key("B", Keyboard.KEY_B));
        fourthLineKeys.add(new Key("N", Keyboard.KEY_N));
        fourthLineKeys.add(new Key("M", Keyboard.KEY_M));
        fourthLineKeys.add(new Key("<", Keyboard.KEY_COMMA));
        fourthLineKeys.add(new Key(">", Keyboard.KEY_PERIOD));
        fourthLineKeys.add(new Key("/", Keyboard.KEY_SLASH));
        fourthLineKeys.add(new Key("Shift", Keyboard.KEY_RSHIFT).withWidth(80));

        fifthLineKeys.add(new Key("CTRL", Keyboard.KEY_LCONTROL).withWidth(25));
        fifthLineKeys.add(new Key("WIN", Keyboard.KEY_LWIN));
        fifthLineKeys.add(new Key("Alt", Keyboard.KEY_LMENU).withWidth(65));
        fifthLineKeys.add(new Key("SPACE", Keyboard.KEY_SPACE).withWidth(210));
        fifthLineKeys.add(new Key("Alt", Keyboard.KEY_RMENU).withWidth(65));
        fifthLineKeys.add(new Key("WIN", Keyboard.KEY_RWIN));
        fifthLineKeys.add(new Key("Menu", Keyboard.KEY_RMENU));
        fifthLineKeys.add(new Key("CTRL", Keyboard.KEY_RCONTROL).withWidth(25));

        try {
            Field field = Reflection.getField(Entity.class, "OBFVAL_0");
            isVimeworld = field != null;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!isVimeworld)
            shaderUtils.onRender();

        notificationSystem.draw();
        screenAnimation.beginDraw();
        this.draw();
        screenAnimation.endDraw();
    }

    private void draw() {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);

        int centerX = scaledResolution.getScaledWidth() / 2;
        int centerY = scaledResolution.getScaledHeight() / 2;

        int width = firstLineKeys.size() * 35 + 30;
        int height = 5 * 35;
        int xPos = centerX - width / 2;
        int yPos = centerY - height / 2;

        lockControls = this.getCurrentKey() != null;
        if(guiInitLock)
            lockControls = true;

        drawHeader(xPos, yPos, width, height);

        R2DUtils.drawRect(xPos - 10, yPos - 10, xPos + width + 10, yPos + height + 6, ColorUtils.KEYBIND_BACKGROUND_COLOR);
        R2DUtils.drawRect(xPos - 6, yPos + height + 6, xPos + width + 6, yPos + height + 10, ColorUtils.KEYBIND_BACKGROUND_COLOR);

        R2DUtils.drawFullCircle(xPos - 6, yPos + height + 6, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(xPos + width + 6, yPos + height + 6, PositionUtils.CIRCLE_RADIUS, ColorUtils.KEYBIND_BACKGROUND_COLOR, true);

        int storedXPos = xPos;
        yPos += height / 2;
        yPos += 55;

        for(int i = 0; i < fifthLineKeys.size(); i++) {
            Key key = fifthLineKeys.get(i);
            int x = xPos + i * 35;
            key.draw(x, yPos);
            xPos += key.getWidth() - 30;
        }

        xPos = storedXPos;
        yPos -= 35;

        for(int i = 0; i < fourthLineKeys.size(); i++) {
            Key key = fourthLineKeys.get(i);
            int x = xPos + i * 35;
            key.draw(x, yPos);
            xPos += key.getWidth() - 30;
        }

        xPos = storedXPos;
        yPos -= 35;

        for(int i = 0; i < thirdLineKeys.size(); i++) {
            Key key = thirdLineKeys.get(i);
            int x = xPos + i * 35;
            key.draw(x, yPos);
            xPos += key.getWidth() - 30;
        }

        xPos = storedXPos;
        yPos -= 35;

        for(int i = 0; i < secondLineKeys.size(); i++) {
            Key key = secondLineKeys.get(i);
            int x = xPos + i * 35;
            key.draw(x, yPos);
            xPos += key.getWidth() - 30;
        }

        xPos = storedXPos;
        yPos -= 35;

        for(int i = 0; i < firstLineKeys.size(); i++) {
            Key key = firstLineKeys.get(i);
            int x = xPos + i * 35;
            key.draw(x, yPos);

            xPos += key.getWidth() - 30;
        }

        if(!lockControls) {
            if (Keyboard.isCreated()) {
                while (Keyboard.next()) {
                    if (Keyboard.getEventKeyState()) {
                        int pressed = Keyboard.getEventKey();
                        if(pressed == Keyboard.KEY_ESCAPE) {
                            this.mc.displayGuiScreen(null);
                        }
                        if (pressed != 0) {
                            for (Key key : firstLineKeys) {
                                if (key.getKeyID() == pressed) {
                                    key.runAnimation();
                                    key.syncKey();
                                    break;
                                }
                            }
                            for (Key key : secondLineKeys) {
                                if (key.getKeyID() == pressed) {
                                    key.runAnimation();
                                    key.syncKey();
                                    break;
                                }
                            }
                            for (Key key : thirdLineKeys) {
                                if (key.getKeyID() == pressed) {
                                    key.runAnimation();
                                    key.syncKey();
                                    break;
                                }
                            }
                            for (Key key : fourthLineKeys) {
                                if (key.getKeyID() == pressed) {
                                    key.runAnimation();
                                    key.syncKey();
                                    break;
                                }
                            }
                            for (Key key : fifthLineKeys) {
                                if (key.getKeyID() == pressed) {
                                    key.runAnimation();
                                    key.syncKey();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(this.getCurrentKey() != null) {
            keybindSelectPanel.draw();
        }
        if(guiInitLock) {
            if(initLockTimer.isTimeReached()) {
                guiInitLock = false;
            }
        }
    }

    private void drawHeader(int x, int y, int width, int height) {
        x -= 10;
        y -= 35;

        R2DUtils.drawRect(x + 4, y, x + width + 16, y + 8, Color.BLACK.getRGB());
        R2DUtils.drawRect(x, y + 4, x + width + 20, y + 30,  Color.BLACK.getRGB());

        R2DUtils.drawFullCircle(x + 4, y + 4, PositionUtils.CIRCLE_RADIUS, Color.BLACK.getRGB(), true);
        R2DUtils.drawFullCircle(x + width + 16, y + 4, PositionUtils.CIRCLE_RADIUS, Color.BLACK.getRGB(), true);

        Yammi.getInstance().getTabFont().drawStringWithShadow("Keybind manager", x + 5, y + 7, -1);
    }

    public void mouseClicked(int xD, int yD, int mouse) {
        if(mouse == 0) {
            if(this.getCurrentKey() != null) {
                keybindSelectPanel.mouseClicked(xD, yD);
            }
        }
    }

    @Override
    public void initGui() {
        this.initLockGui();
        notificationSystem.initGui();

        if(!isVimeworld)
            shaderUtils.enable();
        screenAnimation.onOpenScreen();
        super.initGui();
    }

    public void initLockGui(){
        guiInitLock = true;
        initLockTimer.setPause();
    }

    @Override
    public void onGuiClosed() {
        guiInitLock = false;
        if(!isVimeworld)
            shaderUtils.disable();
        screenAnimation.onCloseScreen();
        super.onGuiClosed();
    }

    public Key getCurrentKey(){
        return this.getKeybindSelectPanel().getKey();
    }

    public KeybindSelectPanel getKeybindSelectPanel() {
        return keybindSelectPanel;
    }

    public boolean isLockControls() {
        return lockControls;
    }

    public NotificationSystem getNotificationSystem() {
        return notificationSystem;
    }
}
