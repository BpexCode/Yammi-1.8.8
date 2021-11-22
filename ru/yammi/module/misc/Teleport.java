package ru.yammi.module.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.path.GotoAI;

public class Teleport extends Module {

    private GotoAI tpAI;
    private int tpDelay = 0;
    private boolean atp = false;

    public Teleport(){
        super("Teleport", Category.Misc, "Teleport you to selected block");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            this.doTeleport();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent render3DEvent) {
        if(this.getState()) {
            this.renderTeleport();
        }
    }

    public void doTeleport() {
        if (mc.thePlayer.isEating() || (mc.thePlayer.getHeldItem() != null
                && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem())) {
            return;
        }

        float renderPartialTicks = Yammi.getInstance().getPartialTicks();

        MovingObjectPosition MOPos = mc.thePlayer.rayTrace(300, renderPartialTicks);
        if (MOPos == null || MOPos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return;

        BlockPos BPos = MOPos.getBlockPos();
        if (BPos == null)
            return;
        if (this.atp && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.inGameHasFocus && tpDelay == 0) {
            (this.tpAI = new GotoAI(new BlockPos(BPos.getX() + 0.5, BPos.getY() + 1.0, BPos.getZ() + 0.5)))
                    .update("TP");
            if (this.tpAI.isDone() || this.tpAI.isFailed()) {
                if (this.tpAI.isFailed()) {
                    this.mc.thePlayer.addChatMessage(new ChatComponentText("Teleport error"));
                }
                this.tpAI.stop();
            }
            tpDelay = 5;
        }
        if (this.tpDelay > 0) {
            --this.tpDelay;
        }
    }

    public void renderTeleport() {
        float renderPartialTicks = Yammi.getInstance().getPartialTicks();
        MovingObjectPosition MOPos = mc.thePlayer.rayTrace(300, renderPartialTicks);
        if (MOPos == null || MOPos.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return;

        BlockPos BPos = MOPos.getBlockPos();
        if (BPos == null)
            return;

        final int x = BPos.getX();
        final int y = BPos.getY();
        final int z = BPos.getZ();
        if (getBlockAtPos(BPos).getMaterial() != Material.air) {
            this.atp = true;
            GL11.glPushMatrix();
            double renderPosX = TileEntityRendererDispatcher.staticPlayerX;
            double renderPosY = TileEntityRendererDispatcher.staticPlayerY;
            double renderPosZ = TileEntityRendererDispatcher.staticPlayerZ;
            drawSolidEntityESP(x + 0.5 - renderPosX, y + 1 - renderPosY, z + 0.5 - renderPosZ, 1 * 0.5,
                    0.1, 0F, 1F, 1F, 1F);
            drawOutlinedEntityESP(x + 0.5 - renderPosX, y + 1 - renderPosY, z + 0.5 - renderPosZ,
                    1 * 0.5, 0.1, 0F, 0F, 0F, 1F, 1F);
            GL11.glPopMatrix();
        } else {
            this.atp = false;
        }
    }

    public void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public void drawOutlinedEntityESP(double x, double y, double z, double width, double height, float red,
                                      float green, float blue, float alpha, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(red, green, blue, alpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void drawSolidEntityESP(double x, double y, double z, double width, double height, float red,
                                   float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public void drawBoundingBox(AxisAlignedBB axisalignedbb) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldRenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        tessellator.draw();
    }

    public Block getBlockAtPos(final BlockPos inBlockPos) {
        final IBlockState s = this.mc.theWorld.getBlockState(inBlockPos);
        return s.getBlock();
    }

}
