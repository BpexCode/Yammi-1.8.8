package ru.yammi.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.MouseUtils;
import ru.yammi.utils.PositionUtils;
import ru.yammi.utils.R2DUtils;

import java.awt.*;

public class OptionElement implements IElement {

    private Minecraft mc = Minecraft.getMinecraft();
    private Module module;
    private Option option;

    private MouseUtils mouseUtils = new MouseUtils(250L);
    private MouseUtils mouseColorUtils = new MouseUtils(50L);

    public OptionElement(Module module, Option option) {
        this.module = module;
        this.option = option;
    }

    @Override
    public void keyTyped(int key) {

    }

    @Override
    public void draw(int posX, int posY, float partialTicks) {
        int x = posX + 5;
        int y = posY + 5;

        FontGL11 font = Yammi.getInstance().getOptionsFont();
        String text = this.getText();

        int yOffset = 0;
        if(this.option.getOptionType() == Option.OptionType.BOOLEAN_VALUE) {
            y += 3;
            yOffset = -1;
        }
        if(this.option.getOptionType() != Option.OptionType.MODE)
            font.drawStringWithShadow(text, x, y + yOffset, -1);

        if(this.option.getOptionType() == Option.OptionType.DOUBLE_VALUE){
            drawDoubleSlider(x, y);
        }
        if(this.option.getOptionType() == Option.OptionType.FLOAT_VALUE){
            drawFloatSlider(x, y);
        }
        if(this.option.getOptionType() == Option.OptionType.INT_VALUE){
            drawIntSlider(x, y);
        }
        if(this.option.getOptionType() == Option.OptionType.BOOLEAN_VALUE){
            this.drawCheckbox(x, y);
        }
        if(this.option.getOptionType() == Option.OptionType.COLOR) {
            drawRGBRect(x, y + 8, 90, 5, Color.RED.getRGB());
        }
        if(this.option.getOptionType() == Option.OptionType.MODE) {
            this.drawModes(x, y);
        }
    }

    private void drawModes(int x, int y) {
        R2DUtils.drawRect(x, y, x + PositionUtils.OPTIONS_WIDTH - 10, y + 1, ColorUtils.OPTION_MODE_SEPARATOR_COLOR);

        FontGL11 font = Yammi.getInstance().getOptionsFont();
        Option.Mode[] modes = this.option.getModes();

        y += 7;
        for(int i = 0; i < modes.length; i++) {
            int yPos = y + i * 10;

            Option.Mode mode = modes[i];
            String text = mode.getName();

            int wi = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
            int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;

            int endX = x + font.getStringWidth(text) + 1;
            int endY = yPos + 9;

            boolean hovered = wi >= (x - 1) && wi <= endX && j >= yPos && j <= endY;
            int color = hovered ? ColorUtils.MODULE_TAB_COLOR : -1;

            if(this.option.isModeSelect(mode)) {
                drawCircle(x + PositionUtils.OPTIONS_WIDTH - 12, yPos + 4, 2, ColorUtils.MODULE_TAB_COLOR, true);
                R2DUtils.drawRect(0, 0, 0, 0, color);
            }

            font.drawStringWithShadow(text, x, yPos, color);

            if(!TabElement.anyTabClicked) {
                if (hovered) {
                    if (mouseUtils.isMouseButtonDown(0)) {
                        this.option.selectMode(mode);
                        Yammi.getInstance().getScreen().getNotificationSystem().addNotification("Selected mode \u00a7a" + mode.getName(), NotificationSystem.NotificationType.DEFAULT);
                    }
                }
            }
        }
    }

    private void drawRGBRect(int x, int y, int w, int h, int color){
        int lastPos = x;
        for(int i = 0; i < w; i++){
            int currentColor = rainbow(color, i + i * 55000000L, 1F);
            drawGradientRect(lastPos, y, lastPos + 1, y + h, currentColor, currentColor);
            lastPos += 1;

            int wi = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
            int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;

            int startX = lastPos;
            int endX = startX + 1;
            int endY = y + h;

            boolean hovered = wi >= startX && wi <= endX && j >= y && j <= endY;

            if(!TabElement.anyTabClicked) {
                if (hovered) {
                    if (mouseColorUtils.isMouseButtonDown(0)) {
                        int selectedColor = currentColor;
                        this.getOption().setSliderX(i);
                        this.getOption().setColorValue(selectedColor);
                    }
                }
            }
        }

        R2DUtils.drawRect(x + this.getOption().getSliderX(), y - 1, x + this.getOption().getSliderX() + 2, y + h + 1, Color.WHITE.getRGB());
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        worldrenderer.pos((double)right, (double)top, (double)0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)0D).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)0D).color(f5, f6, f7, f4).endVertex();

        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private int rainbow(int colorIn, long offset, float fade) {
        final float hue = (colorIn + offset) / 5.0E9f % 1.0f; //5.0E9
        final long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0f, 1.0f))), 16);
        final Color c = new Color((int) color);
        return new Color(c.getRed() / 255.0f * fade, c.getGreen() / 255.0f * fade, c.getBlue() / 255.0f * fade,
                c.getAlpha() / 255.0f).getRGB();
    }

    private void drawCheckbox(int x, int y) {
        boolean b = this.option.isBooleanValue();
        boolean hovered = this.isCircleHovered(x, y + 3);
        int color = hovered ? ColorUtils.MODULE_ENABLED_COLOR : Color.BLACK.getRGB();
        color = this.option.isBooleanValue() ? ColorUtils.CHECKBOX_ENABLED_COLOR : color;

        if(hovered){
            if(!TabElement.anyTabClicked) {
                if (mouseUtils.isMouseButtonClicked(0)) {
                    color = ColorUtils.BOX_CLICKED_COLOR;
                }
                if (mouseUtils.isMouseButtonDown(0)) {
                    this.option.setBooleanValue(!this.option.isBooleanValue());
                    Config.store();
                }
            }
        }

        R2DUtils.enableGL2D();
        this.drawCircle(x + PositionUtils.OPTIONS_WIDTH - 14, y + 2, 4D, -1, false);
        this.drawCircle(x + PositionUtils.OPTIONS_WIDTH - 14, y + 2, 3D, color, true);
        R2DUtils.disableGL2D();
        Gui.drawRect(0, 0, 0, 0, -1);
        //this.drawCircle(x + PositionUtils.OPTIONS_WIDTH - 14, y + 2, 4D, -1, false);
    }

    private void drawCircle(int cx, int cy, double r, final int c, boolean full) {
        R2DUtils.enableGL2D();
        R2DUtils.drawFullCircle(cx, cy, r, c, full);
        R2DUtils.disableGL2D();
    }

    private boolean isCircleHovered(int x, int y) {
        x = x + PositionUtils.OPTIONS_WIDTH - 14;
        int startX = x - 3;
        int startY = y - 3;
        int endX = x + 3;
        int endY = y + 3;
        final int xD = getMouseX();
        final int yD = getMouseY();
        return xD >= startX && xD <= endX && yD >= startY && yD <= endY;
    }

    private void drawDoubleSlider(int x, int y) {
        R2DUtils.drawRect(x, y + 8, x + PositionUtils.OPTIONS_WIDTH - 10, y + 13, ColorUtils.SLIDER_BACKGROUND_COLOR);

        int sliderX = option.getSliderX();
        double value = option.getDoubleValue();
        double maxValue = option.getMaxDoubleValue();

        R2DUtils.drawRect(x, y + 8, x + sliderX, y + 13, ColorUtils.SLIDER_FILLED_COLOR);

        if(sliderX >= 90) {
            sliderX = 90;
            option.setSliderX(sliderX);
        }

        if(!TabElement.anyTabClicked) {
            if (this.isSliderHovered(x, y + 8) && Mouse.isButtonDown(0)) {
                int mouseX = this.getMouseX();

                if (mouseX >= x) {
                    option.setSliderX(mouseX - x);
                } else {
                    option.setSliderX(mouseX - (x + 90));
                }

                double newValue = sliderX / (90 / maxValue);
                if (newValue >= maxValue) {
                    newValue = maxValue;
                }
                newValue = Math.abs(newValue);
                String f0 = String.format("%.01f", newValue);
                f0 = f0.replace(",", ".") + "D";
                option.setDoubleValue(Double.valueOf(f0));
                Config.store();
            }
        }
    }

    private void drawIntSlider(int x, int y) {
        R2DUtils.drawRect(x, y + 8, x + PositionUtils.OPTIONS_WIDTH - 10, y + 13, ColorUtils.SLIDER_BACKGROUND_COLOR);

        int sliderX = option.getSliderX();
        int value = option.getIntValue();
        int maxValue = option.getMaxIntValue();

        R2DUtils.drawRect(x, y + 8, x + sliderX, y + 13, ColorUtils.SLIDER_FILLED_COLOR);

        if(sliderX >= 90) {
            sliderX = 90;
            option.setSliderX(sliderX);
        }

        if(!TabElement.anyTabClicked) {
            if (this.isSliderHovered(x, y + 8) && Mouse.isButtonDown(0)) {
                int mouseX = this.getMouseX();

                if (mouseX >= x) {
                    option.setSliderX(mouseX - x);
                } else {
                    option.setSliderX(mouseX - (x + 90));
                }

                /*if (maxValue <= 0) {
                    maxValue = 1;
                }
                int delta = 90 / maxValue;
                if (delta <= 0) {
                    delta = 1;
                }

                int newValue = sliderX / delta;
                if (newValue >= maxValue) {
                    newValue = maxValue;
                }*/

                float v = 90 / (float)maxValue;

                System.out.println(v);
                if(v == 0)
                    v = 1;
                int newValue = (int)(sliderX / v);
                if (newValue >= maxValue) {
                    newValue = maxValue;
                }
                newValue = Math.abs(newValue);
                option.setIntValue(newValue);
                Config.store();
            }
        }
    }

    private void drawFloatSlider(int x, int y) {
        R2DUtils.drawRect(x, y + 8, x + PositionUtils.OPTIONS_WIDTH - 10, y + 13, ColorUtils.SLIDER_BACKGROUND_COLOR);

        int sliderX = option.getSliderX();
        float value = option.getFloatValue();
        float maxValue = option.getMaxFloatValue();

        R2DUtils.drawRect(x, y + 8, x + sliderX, y + 13, ColorUtils.SLIDER_FILLED_COLOR);

        if(sliderX >= 90) {
            sliderX = 90;
            option.setSliderX(sliderX);
        }

        if(!TabElement.anyTabClicked) {
            if (this.isSliderHovered(x, y + 8) && Mouse.isButtonDown(0)) {
                int mouseX = this.getMouseX();

                if (mouseX >= x) {
                    option.setSliderX(mouseX - x);
                } else {
                    option.setSliderX(mouseX - (x + 90));
                }

                float newValue = sliderX / (90 / maxValue);
                if (newValue >= maxValue) {
                    newValue = maxValue;
                }
                newValue = Math.abs(newValue);
                String f0 = String.format("%.01f", newValue);
                f0 = f0.replace(",", ".") + "F";
                option.setFloatValue(Float.valueOf(f0));
                Config.store();
            }
        }
    }

    private boolean isSliderHovered(int x, int y) {
        int startX = x;
        int startY = y;
        int endX = x + PositionUtils.OPTIONS_WIDTH - 10;
        int endY = y + 5;
        final int xD = getMouseX();
        final int yD = getMouseY();
        return xD >= startX && xD <= endX && yD >= startY && yD <= endY;
    }

    private int getMouseY(){
        return this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
    }

    private int getMouseX(){
        return Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
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

    public Option getOption() {
        return option;
    }

    private String getText() {
        String optionName = option.getName();
        if(option.getOptionType() == Option.OptionType.FLOAT_VALUE)
            return optionName + ": " + option.getFloatValue();
        if(option.getOptionType() == Option.OptionType.DOUBLE_VALUE)
            return optionName + ": " + option.getDoubleValue();
        if(option.getOptionType() == Option.OptionType.INT_VALUE)
            return optionName + ": " + option.getIntValue();
        return optionName;
    }

}
