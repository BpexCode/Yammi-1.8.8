package ru.yammi.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PositionUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static int TAB_WIDTH = 130;
    public static int TAB_HEIGHT = 25;
    public static int CIRCLE_RADIUS = 4;
    public static int TAB_SEPARATOR_WIDTH = 130;
    public static int TAB_SEPARATOR_HEIGHT = 1;
    public static RenderStringPosition TAB_STRING_POSITION = RenderStringPosition.CENTER;

    public static int MODULE_WIDTH = 130;
    public static int MODULE_HEIGHT = 23;
    public static int MODULE_TAB_WIDTH = 2;
    public static int MODULE_TAB_HEIGHT = 23;

    public static int OPTIONS_WIDTH = 100;

    public static RenderStringPosition MODULE_STRING_POSITION = RenderStringPosition.LEFT;

    public static enum RenderStringPosition{
        LEFT,
        CENTER,
        RIGHT;
    }

    public static boolean isInLiquid(AxisAlignedBB par1AxisAlignedBB) {
        par1AxisAlignedBB = par1AxisAlignedBB.contract(0.001D, 0.001D, 0.001D);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (mc.theWorld.getChunkFromBlockCoords(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) == null) {
            return false;
        } else {
            for(int var11 = var4; var11 < var5; ++var11) {
                for(int var12 = var6; var12 < var7; ++var12) {
                    for(int var13 = var8; var13 < var9; ++var13) {
                        Block var14 = mc.theWorld.getBlockState(new BlockPos(var11, var12, var13)).getBlock();
                        if (var14 instanceof BlockLiquid) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    public static boolean isInLiquid() {
        boolean inLiquid = false;
        if (getBlockAtPosC(mc.thePlayer, 0.30000001192092896D, 0.0D, 0.30000001192092896D).getMaterial().isLiquid() || getBlockAtPosC(mc.thePlayer, -0.30000001192092896D, 0.0D, -0.30000001192092896D).getMaterial().isLiquid()) {
            inLiquid = true;
        }

        return inLiquid;
    }

    public static Block getBlockAtPosC(EntityPlayer inPlayer, double x, double y, double z) {
        return getBlockAtPos(new BlockPos(inPlayer.posX - x, inPlayer.posY - y, inPlayer.posZ - z));
    }

    public static Block getBlockAtPos(BlockPos inBlockPos) {
        IBlockState s = mc.theWorld.getBlockState(inBlockPos);
        return s.getBlock();
    }
}
