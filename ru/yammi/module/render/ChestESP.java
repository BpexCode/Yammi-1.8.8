package ru.yammi.module.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.lwjgl.opengl.GL11;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

import java.awt.*;

public class ChestESP extends Module {

    public ChestESP() {
        super("ChestESP", Category.Render, "Highlight chests in the world");
        this.getOptions().add(new Option("Line width", 0F, 5F));
    }

    @EventTarget
    public void onRenderWorld(Render3DEvent render3DEvent) {
        if(this.getState()){
            try {
                for (Object tileEntity : this.mc.theWorld.loadedTileEntityList) {
                    if (tileEntity != null) {
                        if (((tileEntity instanceof TileEntityChest))
                                || ((tileEntity instanceof TileEntityEnderChest))) {
                            this.render2D((TileEntity)tileEntity);
                        }
                    }
                }
            } catch (Throwable throwable){
                throwable.printStackTrace();
            }
        }
    }

    public void render2D(TileEntity tileEntity) {
        try {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            // GL11.glDisable(GL11.GL_LIGHTING);

            float linewidth = this.getOption("Line width").getFloatValue();
            if(linewidth <= 0F) {
                linewidth = 1F;
            }
            GL11.glLineWidth(linewidth);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);

            int color = tileEntity instanceof TileEntityEnderChest ? Color.MAGENTA.getRGB() : Color.ORANGE.getRGB();

            float f3 = (float) (color >> 24 & 255) / 255.0F;
            float f = (float) (color >> 16 & 255) / 255.0F;
            float f1 = (float) (color >> 8 & 255) / 255.0F;
            float f2 = (float) (color & 255) / 255.0F;
            GL11.glColor4f(f, f1, f2, f3);

            double data1 = TileEntityRendererDispatcher.staticPlayerX;
            double data2 = TileEntityRendererDispatcher.staticPlayerY;
            double data3 = TileEntityRendererDispatcher.staticPlayerZ;
            TileEntity tileEntityChest = (TileEntity) tileEntity;
            double renderX = tileEntityChest.getPos().getX() - data1;
            double renderY = tileEntityChest.getPos().getY() - data2;
            double renderZ = tileEntityChest.getPos().getZ() - data3;

            double x = 0.0D;
            double y = 0.0D;
            double z = 0.0D;

            GL11.glTranslated(renderX, renderY, renderZ);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y + 1.0D, z);
            GL11.glVertex3d(x + 1.0D, y + 1.0D, z);
            GL11.glVertex3d(x + 1.0D, y, z);
            GL11.glVertex3d(x + 1.0D, y, z);
            GL11.glVertex3d(x + 1.0D, y + 1.0D, z);
            GL11.glVertex3d(x + 1.0D, y + 1.0D, z + 1.0D);
            GL11.glVertex3d(x + 1.0D, y, z + 1.0D);
            GL11.glVertex3d(x + 1.0D, y, z + 1.0D);
            GL11.glVertex3d(x + 1.0D, y + 1.0D, z + 1.0D);
            GL11.glVertex3d(x, y + 1.0D, z + 1.0D);
            GL11.glVertex3d(x, y, z + 1.0D);
            GL11.glVertex3d(x, y, z + 1.0D);
            GL11.glVertex3d(x, y + 1.0D, z + 1.0D);
            GL11.glVertex3d(x, y + 1.0D, z);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x + 1.0D, y, z);
            GL11.glVertex3d(x + 1.0D, y, z + 1.0D);
            GL11.glVertex3d(x, y, z + 1.0D);
            GL11.glVertex3d(x, y + 1.0D, z);
            GL11.glVertex3d(x + 1.0D, y + 1.0D, z);
            GL11.glVertex3d(x + 1.0D, y + 1.0D, z + 1.0D);
            GL11.glVertex3d(x, y + 1.0D, z + 1.0D);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y, z + 1.0D);
            GL11.glVertex3d(x, y + 1D, z);
            GL11.glVertex3d(x, y + 1D, z + 1D);

            GL11.glVertex3d(x + 1D, y, z);
            GL11.glVertex3d(x + 1D, y, z + 1D);

            GL11.glVertex3d(x + 1D, y + 1.0D, z);
            GL11.glVertex3d(x + 1D, y + 1D, z + 1.0D);
            GL11.glEnd();

            GL11.glTranslated(-renderX, -renderY, -renderZ);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            //GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
