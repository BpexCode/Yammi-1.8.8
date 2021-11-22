package ru.yammi.utils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import ru.yammi.gui.animation.OpenScreenAnimation;

import java.awt.*;

public class R2DUtils {

    public static void glColor(Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final float alpha, final int redRGB, final int greenRGB, final int blueRGB) {
        final float red = 0.003921569f * redRGB;
        final float green = 0.003921569f * greenRGB;
        final float blue = 0.003921569f * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawBorderedRect(final float x, final float y, final float x1, final float y1, final float width, final int internalColor, final int borderColor) {
        enableGL2D();
        glColor(internalColor);
        drawRect(x + width, y + width, x1 - width, y1 - width);
        glColor(borderColor);
        drawRect(x + width, y, x1 - width, y + width);
        drawRect(x, y, x + width, y1);
        drawRect(x1 - width, y, x1, y1);
        drawRect(x + width, y1 - width, x1 - width, y1);
        disableGL2D();
    }

    public static void drawRect(final float x, final float y, final float x1, final float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    public static void drawRect(int left, int top, int right, int bottom, int color)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = OpenScreenAnimation.ALPHA_COLOR >= 1.0F ? (float)(color >> 24 & 255) / 255.0F : OpenScreenAnimation.ALPHA_COLOR;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void enableGL2D() {
       // GL11.glDisable(2929);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        //GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        //GL11.glEnable(2929);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawFullCircleIngame(int cx, int cy, double r, final int c, boolean full) {
        r *= 2.0;
        cx *= 2;
        cy *= 2;
        float f = (c >> 24 & 0xFF) / 255.0f;
        float f2 = (c >> 16 & 0xFF) / 255.0f;
        float f3 = (c >> 8 & 0xFF) / 255.0f;
        float f4 = (c & 0xFF) / 255.0f;

        enableGL2D();

        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        GL11.glColor4f(f2, f3, f4, f);

        if(!full) {
            GL11.glBegin(GL11.GL_LINES);
        } else {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        }

        for (int i = 0; i <= 360; ++i) {
            double x = Math.sin(i * Math.PI / 180.0) * r;
            double y = Math.cos(i * Math.PI / 180.0) * r;

            GL11.glVertex2d(cx + x, cy + y);
        }

        //GlStateManager.disableBlend();
        GL11.glEnd();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        // GL11.glDisable(GL11.GL_BLEND);
        disableGL2D();
    }

    public static void drawFullCircle(int cx, int cy, double r, final int c, boolean full) {
        r *= 2.0;
        cx *= 2;
        cy *= 2;
        float f = (c >> 24 & 0xFF) / 255.0f;
        float f2 = (c >> 16 & 0xFF) / 255.0f;
        float f3 = (c >> 8 & 0xFF) / 255.0f;
        float f4 = (c & 0xFF) / 255.0f;

        //enableGL2D();

        //GL11.glEnable(GL11.GL_BLEND);
       //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        f = OpenScreenAnimation.ALPHA_COLOR >= 1.0F ? f : OpenScreenAnimation.ALPHA_COLOR;
        GL11.glColor4f(f2, f3, f4, f);

        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        if(!full) {
            GL11.glBegin(GL11.GL_LINES);
        } else {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        }

        for (int i = 0; i <= 360; ++i) {
            double x = Math.sin(i * Math.PI / 180.0) * r;
            double y = Math.cos(i * Math.PI / 180.0) * r;

            GL11.glVertex2d(cx + x, cy + y);
        }

        //GlStateManager.disableBlend();
        GL11.glEnd();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
       // GL11.glDisable(GL11.GL_BLEND);
        //disableGL2D();
    }

}
