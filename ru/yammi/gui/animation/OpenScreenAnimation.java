package ru.yammi.gui.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;

public class OpenScreenAnimation {

    private int ticks = 0;
    private float scale = 0.95F;
    public static float ALPHA_COLOR = 0.0F;
    private boolean scaleRestored = false;

    public void onOpenScreen(){
        scaleRestored = false;
        ticks = 40;
    }

    public void beginDraw(){
        if(ticks != 0) {
            ticks--;
            scale += 0.00125F;
            ALPHA_COLOR += 0.0250F;;
        }
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        GL11.glPushMatrix();

        GL11.glTranslatef(scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2, 0);
        GL11.glScalef(scale, scale, 0F);
        GL11.glTranslatef(-scaledResolution.getScaledWidth() / 2, -scaledResolution.getScaledHeight() / 2, 0);
    }

    public void endDraw(){
        GL11.glPopMatrix();
    }

    public void onCloseScreenNoAlpha(){
        scale = 0.95F;
        ticks = 0;
    }

    public void onCloseScreen(){
        scale = 0.95F;
        ALPHA_COLOR = 0.1F;
        ticks = 0;
    }

    public float smoothTrans(double current, double last){
        return (float) (current * Yammi.getInstance().getTimer().renderPartialTicks + (last * (1.0f - Yammi.getInstance().getTimer().renderPartialTicks)));
    }

}
