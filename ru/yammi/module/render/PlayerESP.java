package ru.yammi.module.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.*;

import java.awt.*;

public class PlayerESP extends Module {

    private int[] colorCodes = new int[32];

    public PlayerESP(){
        super("PlayerESP", Category.Render, "Highlight players in the world");
        this.getOptions().add(new Option("Render mode", this.getModes("Box", "2D", "Outline")));
        this.getOptions().add(new Option(Option.OptionType.COLOR,"Color"));
        this.getOptions().add(new Option("Team color"));
        this.getOptions().add(new Option("Line width", 0F, 5F));
        this.getOptions().add(new Option("Box width", 0F, 5F));
        this.getOptions().add(new Option("Box height", 0F, 5F));
        initColorCodes();
    }

    private void initColorCodes(){
        for (int i = 0; i < 32; ++i)
        {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6)
            {
                k += 85;
            }

            if (i >= 16)
            {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCodes[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if(this.getState()) {
            RenderMode mode = this.getCurrentMode();
            switch (mode){
                case BOX: {
                    renderBoxes(event.renderPartialTicks);
                    break;
                }
                case RENDER_2D:
                    render2D(event.renderPartialTicks);
                    break;
                case OUTLINE: {
                    renderOutline(event.renderPartialTicks);
                    break;
                }
            }
        }
    }

    private void renderOutline(float pt) {
        try {
            int list = GL11.glGenLists(1);
            MCStencil.checkSetupFBO();

            Stencil.getInstance().startLayer();
            GL11.glPushMatrix();
            Reflection.getMethod(EntityRenderer.class, new Class[] {float.class, int.class}, "setupCameraTransform", "func_78479_a", "a").invoke(mc.entityRenderer, new Object[] {pt, 0});

            //mc.entityRenderer.setupCameraTransform(pt, 0);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            Stencil.getInstance().setBuffer(true);
            GL11.glNewList(list, GL11.GL_COMPILE);
            GlStateManager.enableLighting();

            for(Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity != null && entity != this.mc.thePlayer) {
                    if (entity instanceof EntityPlayer) {
                        double renderPosX = TileEntityRendererDispatcher.staticPlayerX;
                        double renderPosY = TileEntityRendererDispatcher.staticPlayerY;
                        double renderPosZ = TileEntityRendererDispatcher.staticPlayerZ;

                        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt;
                        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt;
                        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt;

                        int color = this.getOption("Color").getColorValue();
                        if(color == 0)
                            color = Color.RED.getRGB();

                        if(this.getOption("Team color").isBooleanValue()) {
                            String text = entity.getDisplayName().getFormattedText();
                            if(Character.toLowerCase(text.charAt(0)) == '\u00a7'){

                                char oneMore = Character.toLowerCase(text.charAt(1));
                                int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);

                                if (colorCode < 16) {
                                    try {
                                        int newColor = colorCodes[colorCode];
                                        color = getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                                    } catch (ArrayIndexOutOfBoundsException ignored) {
                                    }
                                }
                            }
                        }

                        glColor(color);
                        pre3D();
                        GL11.glLineWidth(3.5f);
                        //  GL11.glTranslated(posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ);
                        Render entityRender = mc.getRenderManager().getEntityRenderObject(entity);
                        if (entityRender != null) {
                            float distance = mc.thePlayer.getDistanceToEntity(entity);
                            if (entity instanceof EntityLivingBase) {
                                GlStateManager.disableLighting();
                                //RendererLivingEntity.renderLayers = false;
                                //RendererLivingEntity.rendername = false;
                                //ChatUtil.printChat("" + entity);
                                entityRender.doRender(entity, posX - renderPosX, posY - renderPosY, posZ - renderPosZ, pt, pt);
                                //RendererLivingEntity.renderLayers = true;
                                //RendererLivingEntity.rendername = true;
                                GlStateManager.enableLighting();

                            }
                        }
                        post3D();

                        GL11.glEndList();
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        GL11.glCallList(list);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_POINT);
                        GL11.glCallList(list);
                        Stencil.getInstance().setBuffer(false);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                        GL11.glCallList(list);
                        Stencil.getInstance().cropInside();
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        GL11.glCallList(list);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_POINT);
                        GL11.glCallList(list);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                        Stencil.getInstance().stopLayer();
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glDeleteLists(list, 1);
                        GL11.glPopMatrix();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }


    public void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    private void render2D(float partialTicks) {
        for(Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity != null && entity != this.mc.thePlayer) {
                if (entity instanceof EntityPlayer) {
                    double renderPosX = TileEntityRendererDispatcher.staticPlayerX;
                    double renderPosY = TileEntityRendererDispatcher.staticPlayerY;
                    double renderPosZ = TileEntityRendererDispatcher.staticPlayerZ;
                    double x = entity.lastTickPosX + (entity.posX- entity.lastTickPosX) * partialTicks - renderPosX;
                    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY - 0.1;
                    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ;

                    this.render2D(entity, x, y, z, partialTicks);
                }
            }
        }
    }

    private void render2D(Entity entity, double x, double y, double z, float pt) {
        GL11.glPushMatrix();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glTranslated((double)(float)x, (float)y + entity.height + 0.4, (double)(float)z);
        GL11.glRotatef(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.mc.getRenderManager().playerViewX, 0.0f, 1.0f, 0.0f);

        //final double size = this.getSize(entity) * -0.0225;

        float mod = -0.02666667F;
        float diff = this.mc.thePlayer.getDistanceToEntity(entity) / 1000.0F;
        mod -= diff;

        float yDiff =  this.mc.thePlayer.getDistanceToEntity(entity) / 10000.0F;
        float yMod = -0.02666667F - yDiff;
        //GL11.glScalef(mod, yMod, mod);
        GL11.glScalef(-0.02666667F, -0.02666667F, -0.02666667F);

        int color = this.getOption("Color").getColorValue();
        if(color == 0)
            color = Color.RED.getRGB();

        if(this.getOption("Team color").isBooleanValue()) {
            String text = entity.getDisplayName().getFormattedText();
            if(Character.toLowerCase(text.charAt(0)) == '\u00a7'){

                char oneMore = Character.toLowerCase(text.charAt(1));
                int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);

                if (colorCode < 16) {
                    try {
                        int newColor = colorCodes[colorCode];
                        color = getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            }
        }

        int renderXPos = (int)entity.width / 2;
        int lineSize = 1;

        Gui.drawRect(-15, 5, -14, 87, color); // слева
        Gui.drawRect(14, 5, 15, 87, color); // справа
        Gui.drawRect(-15, 4, 15, 5, color);//верх
        Gui.drawRect(-15, 87, 15, 88, color);//низ

        int healthColor = Color.GREEN.getRGB();

        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;

            int size = 82;
            int health = (int)player.getHealth();
            int maxHealth = (int)player.getMaxHealth();
            int delimiter = (size / maxHealth);

            Gui.drawRect(-16, 88, -15, 88 - delimiter * (health + 1), healthColor);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    private float getSize(EntityPlayer player) {
        final Entity ent = this.mc.thePlayer;
        final boolean angle = isFacingAtEntity(player, 22.0);
        final float dist = ent.getDistanceToEntity(player) / 6.0f;
        final float size = (dist <= 2.0f) ? 1.3f : dist;
        return size;
    }

    public boolean isFacingAtEntity(final Entity cunt, double angleHowClose) {
        final Entity ent = this.mc.thePlayer;
        final float[] yawPitch = getYawAndPitch(cunt);
        angleHowClose /= 4.5;
        final float yaw = yawPitch[0];
        final float pitch = yawPitch[1];
        return AngleDistance(ent.rotationYaw, yaw) < angleHowClose && AngleDistance(ent.rotationPitch, pitch) < angleHowClose;
    }

    public float[] getYawAndPitch(final Entity target) {
        final Entity ent = this.mc.thePlayer;
        final double x = target.posX - ent.posX;
        final double z = target.posZ - ent.posZ;
        final double y = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - this.mc.thePlayer.posY;
        final double helper = MathHelper.sqrt_double(x * x + z * z);
        final float newYaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float newPitch = (float)(Math.atan2(y * 1.0, helper) * 180.0 / 3.141592653589793);
        return new float[] { newYaw, newPitch };
    }

    private float AngleDistance(final float par1, final float par2) {
        float angle = Math.abs(par1 - par2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }


    private void renderBoxes(float pt) {
        for(Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity != null && entity != this.mc.thePlayer) {
                if (entity instanceof EntityPlayer) {
                    this.renderBox(entity, pt);
                }
            }
        }
    }

    private void renderBox(Entity entity, float pt){
       int color = this.getOption("Color").getColorValue();
       if(color == 0)
           color = Color.RED.getRGB();

       if(this.getOption("Team color").isBooleanValue()) {
           String text = entity.getDisplayName().getFormattedText();
           if(Character.toLowerCase(text.charAt(0)) == '\u00a7'){

               char oneMore = Character.toLowerCase(text.charAt(1));
               int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);

               if (colorCode < 16) {
                   try {
                       int newColor = colorCodes[colorCode];
                       color = getColor((newColor >> 16), (newColor >> 8 & 0xFF), (newColor & 0xFF), 255);
                   } catch (ArrayIndexOutOfBoundsException ignored) {
                   }
               }
           }
       }

       float width = this.getOption("Line width").getFloatValue();
       if(width == 0.0F)
           width += 0.1F;

        float f = (color >> 24 & 0xFF) / 255.0f;
        float f2 = (color >> 16 & 0xFF) / 255.0f;
        float f3 = (color >> 8 & 0xFF) / 255.0f;
        float f4 = (color & 0xFF) / 255.0f;

        float boxWidth =  this.getOption("Box width").getFloatValue();
        float boxHeight =  this.getOption("Box height").getFloatValue();
        if(boxHeight <= 0.0F)
            boxHeight = 0.1F;
        if(boxWidth <= 0.0F)
            boxWidth = 0.1F;

        R3DUtils.drawOutlinedEspBlock(entity.posX - 0.5, entity.posY, entity.posZ - 0.5, f2, f3, f4, f, width, entity.width, entity.height, boxWidth, boxHeight);
    }

    public RenderMode getCurrentMode(){
        String strmode = this.getOption("Render mode").getMode().getName();
        if(strmode.equals("Box"))
            return RenderMode.BOX;
        if(strmode.equals("2D"))
            return RenderMode.RENDER_2D;
        if(strmode.equals("Outline"))
            return RenderMode.OUTLINE;
        if(strmode.equals("Shader"))
            return RenderMode.SHADER;
        return RenderMode.RENDER_2D;
    }

    public static enum RenderMode {
        RENDER_2D,
        BOX,
        OUTLINE,
        SHADER;
    };

    public int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

}
