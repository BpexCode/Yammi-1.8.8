package ru.yammi.module.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

import java.util.List;

public class Trajectories extends Module {

    public Trajectories(){
        super("Trajectories", Category.Render, "Show trajectory of arrows, eggs and e.t.c");
    }

    @EventTarget
    public void onRender3D(Render3DEvent render3DEvent) {
        if(this.getState()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4d(1.0D, 1.0D, 0.0D, 0.7D);
            int doubleX = this.drawTraj();
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            if (doubleX > -1)
            {
                if (doubleX == 1)
                {
                    GL11.glColor3d(0.0D, 1.0D, 0.0D);
                }
                else
                {
                    GL11.glColor3d(1.0D, 0.0D, 0.0D);
                }

                this.drawTraj();
            }
        }
    }

    public int drawTraj()
    {
        EntityPlayerSP player = this.mc.thePlayer;
        ItemStack stack = player.inventory.getCurrentItem();

        if (stack == null)
        {
            return -1;
        }
        else
        {
            Item item = stack.getItem();

            if (!(item instanceof ItemBow) && !(item instanceof ItemSnowball) && !(item instanceof ItemEgg) && !(item instanceof ItemEnderPearl) && !(item instanceof ItemFishingRod))
            {
                return -1;
            }
            else
            {
                float renderPartialTicks = Yammi.getInstance().getPartialTicks();
                boolean usingBow = player.inventory.getCurrentItem().getItem() instanceof ItemBow;
                double arrowPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)renderPartialTicks - (double)(MathHelper.cos((float)Math.toRadians((double)player.rotationYaw)) * 0.16F);
                double arrowPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)renderPartialTicks + (double)player.getEyeHeight() - 0.1D;
                double arrowPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)renderPartialTicks - (double)(MathHelper.sin((float)Math.toRadians((double)player.rotationYaw)) * 0.16F);
                float arrowMotionFactor = usingBow ? 1.0F : 0.4F;
                float yaw = (float)Math.toRadians((double)player.rotationYaw);
                float pitch = (float)Math.toRadians((double)player.rotationPitch);
                float arrowMotionX = -MathHelper.sin(yaw) * MathHelper.cos(pitch) * arrowMotionFactor;
                float arrowMotionY = -MathHelper.sin(pitch) * arrowMotionFactor;
                float arrowMotionZ = MathHelper.cos(yaw) * MathHelper.cos(pitch) * arrowMotionFactor;
                double arrowMotion = Math.sqrt((double)(arrowMotionX * arrowMotionX + arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ));
                arrowMotionX = (float)((double)arrowMotionX / arrowMotion);
                arrowMotionY = (float)((double)arrowMotionY / arrowMotion);
                arrowMotionZ = (float)((double)arrowMotionZ / arrowMotion);

                if (usingBow)
                {
                    /*FastBow fastBow = Yammi.getInstance().getModule(FastBow.class);
                    boolean renderManager = fastBow.getState();

                    if (!renderManager && player.getItemInUseCount() == 0)
                    {
                        return -1;
                    }*/

                    float gravity = (float)(72000 - player.getItemInUseCount()) / 20.0F;

                    /*if (renderManager)
                    {
                        gravity = (float)fastBow.getOption("Speed").getIntValue() / 20.0F;
                    }*/

                    gravity = (gravity * gravity + gravity * 2.0F) / 3.0F;

                    if (gravity > 1.0F)
                    {
                        gravity = 1.0F;
                    }

                    if (gravity <= 0.1F)
                    {
                        gravity = 1.0F;
                    }

                    gravity *= 3.0F;
                    arrowMotionX *= gravity;
                    arrowMotionY *= gravity;
                    arrowMotionZ *= gravity;
                }
                else
                {
                    arrowMotionX = (float)((double)arrowMotionX * 1.5D);
                    arrowMotionY = (float)((double)arrowMotionY * 1.5D);
                    arrowMotionZ = (float)((double)arrowMotionZ * 1.5D);
                }

                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL13.GL_MULTISAMPLE);
                GL11.glDepthMask(false);
                GL11.glLineWidth(1.8F);
                double var35 = usingBow ? 0.05D : (item instanceof ItemPotion ? 0.4D : (item instanceof ItemFishingRod ? 0.15D : 0.03D));
                Vec3 playerVector = new Vec3(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                boolean intercept = false;
                Entity inter = null;

                for (int i = 0; i < 1000; ++i)
                {
                    GL11.glVertex3d(arrowPosX - TileEntityRendererDispatcher.staticPlayerX, arrowPosY -TileEntityRendererDispatcher.staticPlayerY, arrowPosZ - TileEntityRendererDispatcher.staticPlayerZ);
                    Vec3 cur = new Vec3(arrowPosX, arrowPosY, arrowPosZ);
                    arrowPosX += (double)arrowMotionX * 0.1D;
                    arrowPosY += (double)arrowMotionY * 0.1D;
                    arrowPosZ += (double)arrowMotionZ * 0.1D;
                    arrowMotionX = (float)((double)arrowMotionX * 0.999D);
                    arrowMotionY = (float)((double)arrowMotionY * 0.999D);
                    arrowMotionZ = (float)((double)arrowMotionZ * 0.999D);
                    arrowMotionY = (float)((double)arrowMotionY - var35 * 0.1D);

                    if (this.mc.theWorld.rayTraceBlocks(playerVector, new Vec3(arrowPosX, arrowPosY, arrowPosZ)) != null)
                    {
                        intercept = true;
                    }

                    Vec3 last = new Vec3(arrowPosX, arrowPosY, arrowPosZ);
                    List list = this.mc.theWorld.loadedEntityList;

                    if (!intercept)
                    {
                        for (int j = 0; j < list.size(); ++j)
                        {
                            Entity entity1 = (Entity)list.get(j);

                            if (entity1 instanceof EntityLivingBase && entity1.canBeCollidedWith() && entity1 != this.mc.thePlayer)
                            {
                                float f = 0.3F;
                                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f, (double)f, (double)f);
                                MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(cur, last);

                                if (movingobjectposition1 != null)
                                {
                                    intercept = true;
                                    inter = entity1;
                                    break;
                                }
                            }
                        }
                    }

                    if (intercept)
                    {
                        if (intercept)
                        {
                            GL11.glVertex3d(arrowPosX - TileEntityRendererDispatcher.staticPlayerX, arrowPosY - TileEntityRendererDispatcher.staticPlayerY, arrowPosZ - TileEntityRendererDispatcher.staticPlayerZ);
                            GL11.glVertex3d(arrowPosX + 0.5D - TileEntityRendererDispatcher.staticPlayerX, arrowPosY - TileEntityRendererDispatcher.staticPlayerY, arrowPosZ + 0.5D - TileEntityRendererDispatcher.staticPlayerZ);
                            GL11.glVertex3d(arrowPosX - 0.5D - TileEntityRendererDispatcher.staticPlayerX, arrowPosY - TileEntityRendererDispatcher.staticPlayerY, arrowPosZ - 0.5D - TileEntityRendererDispatcher.staticPlayerZ);
                            GL11.glVertex3d(arrowPosX - TileEntityRendererDispatcher.staticPlayerX, arrowPosY - TileEntityRendererDispatcher.staticPlayerY, arrowPosZ - TileEntityRendererDispatcher.staticPlayerZ);
                            GL11.glVertex3d(arrowPosX - 0.5D - TileEntityRendererDispatcher.staticPlayerX, arrowPosY - TileEntityRendererDispatcher.staticPlayerY, arrowPosZ + 0.5D - TileEntityRendererDispatcher.staticPlayerZ);
                            GL11.glVertex3d(arrowPosX + 0.5D - TileEntityRendererDispatcher.staticPlayerX, arrowPosY - TileEntityRendererDispatcher.staticPlayerY, arrowPosZ - 0.5D - TileEntityRendererDispatcher.staticPlayerZ);
                        }

                        break;
                    }
                }

                GL11.glEnd();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL13.GL_MULTISAMPLE);
                GL11.glDepthMask(true);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glPopMatrix();
                return inter == null ? 0 : 1;
            }
        }
    }

}
