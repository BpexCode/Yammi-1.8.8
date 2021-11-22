package ru.yammi.module.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.PostLoadEvent;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class Tracers extends Module {

    private boolean bobbing = false;

    public Tracers(){
        super("Tracers", Category.Render, "Draw a lines from anothers players to you");
    }


    @EventTarget
    public void onRender3D(Render3DEvent render3DEvent) {
        if(this.getState()){
            for(Entity entity : this.mc.theWorld.loadedEntityList) {
                //this.mc.theWorld.loadedEntityList.stream().forEach(entity -> {
                if (entity != null && entity != this.mc.thePlayer && entity instanceof EntityPlayer) {
                    try {
                        EntityPlayer entityPlayer = (EntityPlayer) entity;
                        drawTracer(entity, Yammi.getInstance().getPartialTicks());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                // });
            }
        }
    }

    private void drawTracer(Entity entity, float ticks) {
        double renderPosX = this.mc.getRenderManager().viewerPosX;
        double renderPosY = this.mc.getRenderManager().viewerPosY;
        double renderPosZ = this.mc.getRenderManager().viewerPosZ;
        double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks) - renderPosX;
        double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks)  + entity.height / 2.0f - renderPosY;
        double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks) - renderPosZ;

        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        if (this.mc.thePlayer.getDistanceToEntity(entity) >= 50.0F) {
            GL11.glColor4d(0.1D, 0.9D, 0.1D, 0.9D);
        } else if (this.mc.thePlayer.getDistanceToEntity(entity) <= 50.0F
                && this.mc.thePlayer.getDistanceToEntity(entity) >= 25.0F) {
            GL11.glColor4d(0.9D, 0.7D, 0.1D, 0.9D);
        } else if (this.mc.thePlayer.getDistanceToEntity(entity) <= 25.0F
                && this.mc.thePlayer.getDistanceToEntity(entity) >= 1.0F) {
            GL11.glColor4d(0.9D, 0.1D, 0.1D, 0.9D);
        }
        Vec3 eyes = new Vec3(0D, 0D, 1D).rotatePitch(-(float) Math.toRadians(this.mc.thePlayer.rotationPitch)).rotateYaw(-(float) Math.toRadians(this.mc.thePlayer.rotationYaw));;
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(eyes.xCoord, this.mc.thePlayer.getEyeHeight() + eyes.yCoord, eyes.zCoord);
        GL11.glVertex3d(xPos, yPos, zPos);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    @EventTarget
    public void onPostLoad(PostLoadEvent event) {
        if(this.getState()) {
            this.bobbing = this.mc.gameSettings.viewBobbing;
            this.mc.gameSettings.viewBobbing = false;
        }
    }

    @Override
    public void onEnable() {
        this.bobbing = this.mc.gameSettings.viewBobbing;
        this.mc.gameSettings.viewBobbing = false;
    }

    @Override
    public void onDisable() {
        this.mc.gameSettings.viewBobbing = this.bobbing;
    }
}
