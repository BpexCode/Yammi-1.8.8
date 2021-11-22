package ru.yammi.gui.elements;

import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.gui.YammiScreen;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.Module;
import ru.yammi.module.misc.HUD;
import ru.yammi.module.option.Option;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.MouseUtils;
import ru.yammi.utils.PositionUtils;
import ru.yammi.utils.R2DUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModuleElement implements IElement{

    private Module module;
    private Minecraft mc = Minecraft.getMinecraft();

    private boolean hovered = false;
    private boolean clicked = false;

    private MouseUtils mouseUtils = new MouseUtils(250L);
    private List<IElement> elements = new ArrayList<IElement>();

    private boolean optionsOpened = false;

    public ModuleElement(Module moduleIn) {
        this.module = moduleIn;

        List<Option> options = module.getOptions();
        if(options != null) {
            for(Option option : options)
                elements.add(new OptionElement(module, option));
        }
        elements.sort(new Comparator<IElement>() {
            @Override
            public int compare(IElement o1, IElement o2) {
                OptionElement optionElement = (OptionElement) o2;
                if(optionElement.getOption().getOptionType() == Option.OptionType.COLOR)
                    return -1;
                if(optionElement.getOption().getOptionType() == Option.OptionType.MODE)
                    return -2;
                return 0;
            }
        });
    }


    @Override
    public void keyTyped(int key) {
        this.keyPress(key);
    }

    @Override
    public void draw(int posX, int posY, float partialTicks) {
        int x = posX;
        int y = posY + 15 + PositionUtils.TAB_SEPARATOR_HEIGHT;

        this.checkHovered(x, y);

        int color = -1;
        boolean isModuleEnabled = module.getState();

        if(!hovered)
            color = isModuleEnabled ? ColorUtils.MODULE_BACKGROUND_ENABLED_COLOR : ColorUtils.MODULE_BACKGROUND_NOT_ENABLED_COLOR;
        else
            color = ColorUtils.MODULE_BACKGROUND_HOVERED_COLOR;

        if(!TabElement.anyTabClicked) {
            if (hovered) {
                if (mouseUtils.isMouseButtonClicked(0)) {
                    color = ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR;
                }
                if (mouseUtils.isMouseButtonDown(0)) {
                    module.setState(!module.getState());
                    boolean state = module.getState();
                    if (state)
                        module.onEnable();
                    else
                        module.onDisable();

                    String notificationText = module.getName() + (state ? " \u00a7a[Enabled]" : " \u00a77[Disabled]");

                    NotificationSystem.NotificationType type = state ? NotificationSystem.NotificationType.OK : NotificationSystem.NotificationType.CANCEL;
                    Yammi.getInstance().getScreen().getNotificationSystem().addNotification(notificationText, type);
                    Config.store();
                }
            }
        }

        int width = PositionUtils.MODULE_WIDTH - 30;
        int height = PositionUtils.MODULE_HEIGHT - 6;
        R2DUtils.drawRect(x, y, x + width, y + height, color);

        PositionUtils.RenderStringPosition stringPosition = PositionUtils.MODULE_STRING_POSITION;
        if(isModuleEnabled) {
            if(stringPosition == PositionUtils.RenderStringPosition.LEFT || stringPosition == PositionUtils.RenderStringPosition.CENTER)
                R2DUtils.drawRect(x, y, x + PositionUtils.MODULE_TAB_WIDTH, y + height, ColorUtils.MODULE_TAB_COLOR);
            else
                R2DUtils.drawRect(x + width - PositionUtils.MODULE_TAB_WIDTH, y, x + width, y + height, ColorUtils.MODULE_TAB_COLOR);
        }

        String text = this.module.getName();
        FontGL11 font = Yammi.getInstance().getModuleFont();

        int xOffset = 0;
        switch (stringPosition) {
            case LEFT: {
                xOffset = 7;
                break;
            }
            case CENTER: {
                xOffset = (width/ 2) - (font.getStringWidth(text) / 2);
                break;
            }
            case RIGHT: {
                xOffset = width - font.getStringWidth(text) - 7;
                break;
            }
        }

        int stringColor = -1;
        font.drawStringWithShadow(text, x + xOffset, y + 5, stringColor);

        if(this.elements.size() > 0) {
            if (this.optionsOpened) {
                this.drawLine(x + width - 5, y + 5, -3, 3, -1);
                this.drawLine(x + width - 8, y + 8, 3, 3, -1);
            } else {
                this.drawLine(x + width - 8, y + 6, 3, 3, -1);
                this.drawLine(x + width - 5, y + 9, -3, 3, -1);
            }

            if(!TabElement.anyTabClicked) {
                this.checkOptionsOpen(x, y);
            }
            if (this.elements.size() > 0 && this.optionsOpened) {
                this.drawOptions(x + width + 5, y, partialTicks);
            }

        }
    }

    private void keyPress(int key) {
        if(this.hovered) {
            boolean fuckKey = key == Keyboard.KEY_RSHIFT || key == Keyboard.KEY_ESCAPE
                    || key == Keyboard.KEY_RETURN;
            if (fuckKey) {
                HUD hud = Yammi.getInstance().getModule(HUD.class);
                hud.getNotificationSystem().addNotification("Invalid key \u00a7a" + Keyboard.getKeyName(key), NotificationSystem.NotificationType.CANCEL);
            } else {
                if (key == Keyboard.KEY_LCONTROL) {
                    this.getModule().setKeybind(0);
                    HUD hud = Yammi.getInstance().getModule(HUD.class);
                    hud.getNotificationSystem().addNotification("Module \u00a73" + this.getModule().getName() +
                            " \u00a7runbinded", NotificationSystem.NotificationType.OK);
                    Config.store();
                } else {
                    Module meme = getModuleByKey(key);
                    if (meme != null) {
                        meme.setKeybind(0);
                    }
                    this.getModule().setKeybind(key);
                    HUD hud = Yammi.getInstance().getModule(HUD.class);
                    hud.getNotificationSystem().addNotification("Module \u00a73" + this.getModule().getName() +
                            " \u00a7rbinded to \u00a73" + Keyboard.getKeyName(key), NotificationSystem.NotificationType.OK);
                    Config.store();
                }
            }
        }
    }

    private Module getModuleByKey(int key){
        for(Module module : Yammi.getInstance().getModules()){
            if(module.getKeybind() == key)
                return module;
        }
        return null;
    }

    private void drawOptions(int x, int y, float partialTicks){
        //int size = calcSize();
        //int prefSize = 21;

        /*R2DUtils.drawRect(x + 3, y, x + PositionUtils.OPTIONS_WIDTH - 3, y + 6, ColorUtils.OPTIONS_BACKGROUND_COLOR);
        R2DUtils.drawRect(x, y + 4, x + PositionUtils.OPTIONS_WIDTH, y + size * prefSize - 4, ColorUtils.OPTIONS_BACKGROUND_COLOR);
        R2DUtils.drawRect(x + 3, y + size * prefSize - 6, x + PositionUtils.OPTIONS_WIDTH - 3, y + size * prefSize, ColorUtils.OPTIONS_BACKGROUND_COLOR);

        R2DUtils.drawFullCircle(x + 3, y + 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + PositionUtils.OPTIONS_WIDTH - 3, y + 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + 3, y + size * prefSize - 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + PositionUtils.OPTIONS_WIDTH - 3, y + size * prefSize - 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);*/

        int height = getHeight();
        R2DUtils.drawRect(x + 3, y, x + PositionUtils.OPTIONS_WIDTH - 3, y + 6, ColorUtils.OPTIONS_BACKGROUND_COLOR);
        R2DUtils.drawRect(x, y + 4, x + PositionUtils.OPTIONS_WIDTH, y + height - 4, ColorUtils.OPTIONS_BACKGROUND_COLOR);
        R2DUtils.drawRect(x + 3, y + height - 6, x + PositionUtils.OPTIONS_WIDTH - 3, y + height, ColorUtils.OPTIONS_BACKGROUND_COLOR);

        R2DUtils.enableGL2D();
        R2DUtils.drawFullCircle(x + 3, y + 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + PositionUtils.OPTIONS_WIDTH - 3, y + 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + 3, y + height - 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + PositionUtils.OPTIONS_WIDTH - 3, y + height - 3, 3, ColorUtils.OPTIONS_BACKGROUND_COLOR, true);
        R2DUtils.disableGL2D();

        for(int i = 0; i < elements.size(); i++) {
            OptionElement element = (OptionElement)elements.get(i);
            element.draw(x, y + i * 21, partialTicks);
        }
        /*R2DUtils.drawRect(x - 1, yPos - 4 - yOffset / 2, x + font.getStringWidth(text) + 1, yPos - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);
        R2DUtils.drawRect(x - 5, yPos - yOffset / 2, x + font.getStringWidth(text) + 5, yPos + 13 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);
        R2DUtils.drawRect(x - 1, yPos + 13 - yOffset / 2, x + font.getStringWidth(text) + 1, yPos + 17 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);

        R2DUtils.drawFullCircle(x - 1, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
        R2DUtils.drawFullCircle(x + font.getStringWidth(text) + 5 - PositionUtils.CIRCLE_RADIUS, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
        R2DUtils.drawFullCircle(x - 1, yPos + 13 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
        R2DUtils.drawFullCircle(x + font.getStringWidth(text) + 5 - PositionUtils.CIRCLE_RADIUS, yPos + 13 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);*/
    }

    private int getHeight(){
        int res = 0;

        boolean anyMode = false;
        for(IElement element : elements) {
            OptionElement optionElement = (OptionElement) element;

            Option option = optionElement.getOption();
            switch (option.getOptionType()) {
                case MODE: {
                    anyMode = true;
                    res += option.getModes().length * 11;
                    break;
                }
                default: {
                    res += 21;
                    break;
                }
            }
        }
        if(anyMode)
            res += 9;
        return res;
    }

    private int calcSize(){
        int rSize = 0;
        for(IElement element : elements) {
            OptionElement optionElement = (OptionElement) element;
            if(optionElement.getOption().getOptionType() == Option.OptionType.MODE) {
                rSize += optionElement.getOption().getModes().length;
            } else {
                rSize++;
            }
        }
        return rSize;
    }

    private void checkOptionsOpen(int x, int y) {
        if(this.mc.currentScreen != null) {
            int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
            int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
            int startSettingsBoxPosX = x;
            int startSettingsBoxPosY = y;
            int endSettingsBoxPosX = x + PositionUtils.MODULE_WIDTH - 30;
            int endSettingsBoxPosY = y + 14;
            boolean hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
            if (hovered) {
                if (mouseUtils.isMouseButtonDown(1)) {
                    optionsOpened = !optionsOpened;
                }
            }
        }
    }

    @Override
    public void mouseReleased(int xD, int yD, int state) {

    }

    @Override
    public void mouseClicked(int xD, int yD, int mouse) {
    }

    @Override
    public void mouseClickMove(int xD, int yD, int mouse, long time) {

    }

    private void checkHovered(int x, int y){
        if(this.mc != null && this.mc.currentScreen != null) {
            int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
            int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
            int startSettingsBoxPosX = x;
            int startSettingsBoxPosY = y;
            int endSettingsBoxPosX = x + 99;
            int endSettingsBoxPosY = y + 16;
            hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
        }
    }

    public List<IElement> getElements() {
        return elements;
    }

    @Override
    public boolean shouldRenderNextElement (){
        return true;
    }

    private void drawLine(final int x, final int y, final int xLen, final int yLen, int color) {

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        GL11.glPushMatrix();
        /*final Module m = Yammi.getInstance().getModule("ClickGUI");
        final float red = m.getFloatValue("Red Color");
        final float green = m.getFloatValue("Green Color");
        final float blue = m.getFloatValue("Blue Color");
        final float[] colors = { red, green, blue, 1.0f };*/
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

    public Module getModule() {
        return module;
    }
}
