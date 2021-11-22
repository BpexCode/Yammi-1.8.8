package ru.yammi.module.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.Reflection;

import java.lang.reflect.Field;

public class FastBreak extends Module {

    public FastBreak(){
        super("FastBreak", Category.Misc, "You break blocks very quickly");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            this.doDamageBreak();
        }
    }

    private void doDamageBreak(){
        Field blockDamageField = Reflection.getField(PlayerControllerMP.class, "curBlockDamageMP", "field_78770_f", "e");
        Field blockHitDelayField =  Reflection.getField(PlayerControllerMP.class, "blockHitDelay", "field_78781_i", "g");
        if(this.mc.objectMouseOver != null && this.mc.objectMouseOver.getBlockPos() != null) {
            Block block = this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos()).getBlock();
            if (block != null) {
                try {
                    blockHitDelayField.setInt(this.mc.playerController, 0);
                    blockDamageField.setFloat(this.mc.playerController, 1F);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void faceBlock(final double posX, final double posY, final double posZ) {
        final double diffX = posX - this.mc.thePlayer.posX;
        final double diffZ = posZ - this.mc.thePlayer.posZ;
        final double diffY = posY - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        final EntityPlayerSP thePlayer = this.mc.thePlayer;
        thePlayer.rotationPitch += MathHelper.wrapAngleTo180_float(pitch - this.mc.thePlayer.rotationPitch);
        final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
        thePlayer2.rotationYaw += MathHelper.wrapAngleTo180_float(yaw - this.mc.thePlayer.rotationYaw);
    }
}
