package ru.yammi.module.render;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.ColorHandler;
import ru.yammi.module.option.Option;

import java.awt.*;

public class BlockOverlay extends Module {

    public BlockOverlay() {
        super("BlockOverlay", Category.Render, "Highlight block you are looking at");
        this.getOptions().add(new Option(Option.OptionType.COLOR,"Far away color"));
        this.getOptions().add(new Option(Option.OptionType.COLOR,"Nearly color"));
    }

    @EventTarget
    public void onRenderWorld(Render3DEvent render3DEvent) {
        if(this.getState()){
            MovingObjectPosition position = this.mc.objectMouseOver;
            if(position != null && this.mc.theWorld != null) {
                IBlockState blockState = this.mc.theWorld.getBlockState(position.getBlockPos());
                Block block = blockState.getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    this.mc.entityRenderer.disableLightmap();
                    int x = position.getBlockPos().getX(), y = position.getBlockPos().getY(), z = position.getBlockPos().getZ();

                    int color = (int) this.getOption("Nearly color").getColorValue();

                    if(color == 0) {
                        color = Color.RED.getRGB();
                    }


                    float f3 = (float)(color >> 24 & 255) / 255.0F;
                    float f = (float)(color >> 16 & 255) / 255.0F;
                    float f1 = (float)(color >> 8 & 255) / 255.0F;
                    float f2 = (float)(color & 255) / 255.0F;

                    drawOutlinedEspBlock(x, y, z, 0.0f, 0.0f, 0.0f, 1.0F, 1);
                    drawEspBlock(x, y, z, f, f1, f2, 0.4f, 1);
                    this.mc.entityRenderer.enableLightmap();
                    return;
                }
            }
            MovingObjectPosition mop = this.mc.getRenderViewEntity().rayTrace(200, 1.0F);
            if(mop != null) {
                int x = mop.getBlockPos().getX(), y = mop.getBlockPos().getY(), z = mop.getBlockPos().getZ();
                if(this.mc.theWorld.getBlockState(mop.getBlockPos()).getBlock().getMaterial() != Material.air) {

                    int color = (int) this.getOption("Far away color").getColorValue();

                    if(color == 0) {
                        color = Color.RED.getRGB();
                    }


                    float f3 = (float)(color >> 24 & 255) / 255.0F;
                    float f = (float)(color >> 16 & 255) / 255.0F;
                    float f1 = (float)(color >> 8 & 255) / 255.0F;
                    float f2 = (float)(color & 255) / 255.0F;

                    drawOutlinedEspBlock(x, y, z, 0.0f, 0.0f, 0.0f, 1.0F, 1);
                    drawEspBlock(x, y, z, f, f1, f2, 0.4f, 1);
                    this.mc.entityRenderer.enableLightmap();
                }
            }
        }
    }

    public void drawEspBlock(double x, double y, double z, float r, float g, float b, float a, float scale) {
        double pX = TileEntityRendererDispatcher.staticPlayerX;
        double pY = TileEntityRendererDispatcher.staticPlayerY;
        double pZ = TileEntityRendererDispatcher.staticPlayerZ;
        float tr = (1 - scale) / 2;
        GL11.glPushMatrix();
        GL11.glTranslated(-pX, -pY, -pZ);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(r, g, b, a);
        GL11.glTranslated(x, y, z);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glTranslatef(tr, tr, tr);
        GL11.glScalef(scale, scale, scale);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 0, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(0, 1, 1);
        GL11.glVertex3f(0, 1, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(0, 0, 1);
        GL11.glVertex3f(1, 1, 0);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 0, 1);
        GL11.glVertex3f(1, 0, 0);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void drawOutlinedEspBlock(double x, double y, double z, float r, float g, float b, float a, float scale) {
        double pX = TileEntityRendererDispatcher.staticPlayerX;
        double pY = TileEntityRendererDispatcher.staticPlayerY;
        double pZ = TileEntityRendererDispatcher.staticPlayerZ;
        float tr = (1 - scale) / 2;
        GL11.glPushMatrix();
        GL11.glTranslated(-pX, -pY, -pZ);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(r, g, b, a);
        GL11.glTranslated(x, y, z);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glTranslatef(tr, tr, tr);
        GL11.glScalef(scale, scale, scale);
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
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

}
