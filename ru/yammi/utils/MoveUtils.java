package ru.yammi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class MoveUtils {

    public static void faceBlockClientHorizontally(final BlockPos blockPos) {
        final double diffX = blockPos.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
        final double diffZ = blockPos.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        Minecraft.getMinecraft().thePlayer.rotationYaw += MathHelper
                .wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
    }

}
