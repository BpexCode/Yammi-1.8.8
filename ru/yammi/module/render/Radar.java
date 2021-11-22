package ru.yammi.module.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.gui.Render2DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.R2DUtils;

public class Radar extends Module {

    public Radar() {
        super("Radar", Category.Render, "Zalupa");
    }

    @EventTarget
    public void onRender2D(Render2DEvent render2DEvent)  {
        if(this.getState() && this.mc.thePlayer != null && this.mc.theWorld != null) {
            GL11.glPushMatrix();
            //R2DUtils.enableGL2D();
            int x1 = 10;
            int x2 = x1 + 80;
            int y1 = 60;
            int y2 = y1 + 80;
           // GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.5f);
            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2d(x1, y1);
                GL11.glVertex2d(x2, y1);
                GL11.glVertex2d(x2, y2);
                GL11.glVertex2d(x1, y2);
            }
            GL11.glEnd();
            GL11.glLineWidth(2f);

            int color = ColorUtils.TAB_SEPARATOR_COLOR;
            float f3 = (float)(color >> 24 & 255) / 255.0F;
            float f = (float)(color >> 16 & 255) / 255.0F;
            float f1 = (float)(color >> 8 & 255) / 255.0F;
            float f2 = (float)(color & 255) / 255.0F;

            GL11.glColor4f(f, f1, f2, f3);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                GL11.glVertex2d(x1, y1);
                GL11.glVertex2d(x2, y1);
                GL11.glVertex2d(x2, y2);
                GL11.glVertex2d(x1, y2);
            }
            GL11.glEnd();
            GL11.glColor4f(0.1f, 1f, 0.1f, 1f);
            GL11.glLineWidth(1f);
            float rotation = -((mc.thePlayer.prevRotationYawHead
                    + (mc.thePlayer.rotationYawHead - mc.thePlayer.prevRotationYawHead) * Yammi.getInstance().getPartialTicks()));
            for (Entity en : mc.theWorld.loadedEntityList) {
                if (!(en instanceof EntityLivingBase)) {
                    continue;
                }
                if (en instanceof EntityPlayer) {
                    if (Yammi.getInstance().getFriendList().contains(((EntityPlayer)en).getName())) {
                        GL11.glColor4f(0.0f, 1f, 0f, 1f);
                    } else {
                        if (mc.thePlayer.isOnSameTeam((EntityLivingBase) en)) {
                            GL11.glColor4f(0.5f, 1f, 0.5f, 1f);
                        } else {
                            GL11.glColor4f(1f, 0.8f, 0.4f, 1f);
                        }
                    }
                }
                if (en instanceof IMob) {
                    GL11.glColor4f(f, f1, f2, f3);
                }
                if (en instanceof EntityAnimal) {
                    GL11.glColor4f(1, 1f, 0.5f, 1f);
                }
                if (en instanceof INpc || en instanceof EntityIronGolem) {
                    GL11.glColor4f(1, 0.5f, 1f, 1f);
                }
                GL11.glTranslated((x1 + x2) / 2, (y1 + y2) / 2, 0);
                GL11.glRotatef(rotation, 0, 0, 1);
                GL11.glRotatef(180, 0, 0, 1);

                float pt = Yammi.getInstance().getPartialTicks();
                double posX = -(mc.thePlayer.posX
                        - ((en.lastTickPosX + (en.posX - en.lastTickPosX) * pt))) / 3.1;
                double posZ = -(mc.thePlayer.posZ
                        - ((en.lastTickPosZ + (en.posZ - en.lastTickPosZ) * pt))) / 3.1;
                double posY = -(mc.thePlayer.posY
                        - ((en.lastTickPosY + (en.posY - en.lastTickPosY) * pt))) / 500;
                GL11.glPushMatrix();
                GL11.glScaled((1.4 + posY), (1.4 + posY), 1);
                //GL11.glEnable(GL11.GL_BLEND);
			//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glBegin(GL11.GL_TRIANGLE_FAN);
                {
                    for (int i = 0; i <= 10; i++) {
                        double angle = 2 * Math.PI * i / 10;
                        double x = Math.cos(angle);
                        double y = Math.sin(angle);
                        GL11.glVertex3d(x + posX, y + posZ, 365 + posY);
                    }
                }
                GL11.glEnd();

                GL11.glPopMatrix();
                GL11.glRotatef(-rotation, 0, 0, 1);
                GL11.glRotatef(-180, 0, 0, 1);
                GL11.glTranslated(-((x1 + x2) / 2), -((y1 + y2) / 2), 0);
            }
            //GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            //R2DUtils.disableGL2D();
            GL11.glPopMatrix();
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }
}
