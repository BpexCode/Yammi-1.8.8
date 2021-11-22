package ru.yammi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class R3DUtils {

    public static void enableDefaults() {
        //Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GL11.glEnable(3042 /* GL_BLEND */);
        GL11.glDisable(3553 /* GL_TEXTURE_2D */);
        //GL11.glDisable(2896 /* GL_LIGHTING */);
        // GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848 /* GL_LINE_SMOOTH */);
        GL11.glPushMatrix();
    }

    public static void disableDefaults() {
        GL11.glPopMatrix();
        GL11.glDisable(2848 /* GL_LINE_SMOOTH */);
        GL11.glDepthMask(true);
        // GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3553 /* GL_TEXTURE_2D */);
        //GL11.glEnable(2896 /* GL_LIGHTING */);
        GL11.glDisable(3042 /* GL_BLEND */);
        //Minecraft.getMinecraft().entityRenderer.enableLightmap();
    }

    public static void drawSphere(double red, double green, double blue, double alpha, double x, double y, double z,
                                  float size, int slices, int stacks, float lWidth) {
        Sphere sphere = new Sphere();

        enableDefaults();
        GL11.glColor4d(red, green, blue, alpha);
        GL11.glTranslated(x, y, z);
        GL11.glLineWidth(lWidth);
        sphere.setDrawStyle(GLU.GLU_SILHOUETTE);
        sphere.draw(size, slices, stacks);
        disableDefaults();
    }

    public static void drawOutlinedEspBlock(double x, double y, double z, float r, float g, float b, float a, float width, float w, float h, float reW, float reH) {
        double pX = TileEntityRendererDispatcher.staticPlayerX;
        double pY = TileEntityRendererDispatcher.staticPlayerY;
        double pZ = TileEntityRendererDispatcher.staticPlayerZ;

        w += reW;

        y -= reH / 2;
        h += 0.2;

        float trw = (1 - w) / 2;
        float trh = (1 - h) / 2;
        GL11.glPushMatrix();
        GL11.glTranslated(-pX, -pY, -pZ);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glColor4f(r, g, b, a);
        GL11.glTranslated(x, y, z);
        GL11.glLineWidth(width);
        GL11.glPushMatrix();
        GL11.glTranslatef(trw, 0, trw);
        GL11.glScalef(w, h + reH, w);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 1, 1);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

}
