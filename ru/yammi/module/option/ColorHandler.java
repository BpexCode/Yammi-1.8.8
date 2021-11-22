package ru.yammi.module.option;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ColorHandler extends CustomValueHandler {

    private int selectedColor = 0;
    private long lasttime = 0L;
    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public Object getCustomValue() {
        return selectedColor;
    }

    @Override
    public void setCustomValue(Object object) {
        this.selectedColor = Integer.valueOf(String.valueOf(object));
        this.getOption().setCustomValue(this.selectedColor);
    }

    @Override
    public void doRender(int x, int y) {
        try {
            y += 8;
            drawRGBRect(x + 5, y + 4, 90, 6, Color.RED.getRGB());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawRGBRect(int x, int y, int w, int h, int color){
        int lastPos = x;
        for(int i = 0; i < w; i++){
            int currentColor = rainbow(color, i + i * 55000000L, 1F);
            drawGradientRect(lastPos, y, lastPos + 1, y + h, currentColor, currentColor);
            lastPos += 1;

            if (Mouse.isButtonDown(0)) {
                int wi = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
                int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;

                int startX = lastPos;
                int endX = startX + 1;
                int endY = y + h;

                boolean hovered = wi >= startX && wi <= endX && j >= y && j <= endY;
                if (hovered) {
                    if(this.lasttime <= System.currentTimeMillis()) {
                        this.lasttime = System.currentTimeMillis() + 75L;
                        this.selectedColor = currentColor;

                        this.getOption().setSliderX(i);
                        this.getOption().setCustomValue(this.selectedColor);

                        //Config.store();
                    }
                }
            }
        }

        Gui.drawRect(x + this.getOption().getSliderX(), y - 1, x + this.getOption().getSliderX() + 2, y + h + 1, Color.WHITE.getRGB());
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

}
