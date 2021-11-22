package ru.yammi.module.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.Reflection;
import ru.yammi.utils.TimerUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Nuker extends Module {

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private TimerUtils timer = new TimerUtils(1000L);

    private List<Thread> workers = new ArrayList<Thread>();

    public Nuker(){
        super("Nuker", Category.Misc, "Destroy all blocks in selected radius");
        this.getOptions().add(new Option("Radius", 0, 10));
        this.getOptions().add(new Option("Delay", 0, 10));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(this.getState()) {
            int delay = this.getOption("Delay").getIntValue();
            timer.setTimeout(100 * delay);
            if(timer.isTimeReached()) {
                int radius = Nuker.this.getOption("Radius").getIntValue();
                List<BlockPos> blocks = Nuker.this.getBlocks(radius);

                Field blockDamageField = Reflection.getField(PlayerControllerMP.class, "curBlockDamageMP", "field_78770_f", "e");
                Field blockHitDelayField = Reflection.getField(PlayerControllerMP.class, "blockHitDelay", "field_78781_i", "g");

                float prevBlockDamage = 0.0F;
                int prevBlockHitDelay = 0;

                try {
                    prevBlockDamage = blockDamageField.getFloat(this.mc.playerController);
                    prevBlockHitDelay = blockHitDelayField.getInt(this.mc.playerController);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for(BlockPos blockPos : blocks) {
                    float prevYaw = Nuker.this.mc.thePlayer.rotationYaw;
                    float prevPitch = Nuker.this.mc.thePlayer.rotationPitch;

                    faceBlock(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
                    EnumFacing facing = Nuker.this.getEnumFacing(blockPos.getX() + 0.5f, blockPos.getX() + 0.5f, blockPos.getX() + 0.5f);

                    try {
                        blockHitDelayField.setInt(Nuker.this.mc.playerController, 0);
                        blockDamageField.setFloat(Nuker.this.mc.playerController, 1F);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    destroyBlock(blockPos, facing);
                    Nuker.this.mc.thePlayer.rotationYaw = prevYaw;
                    Nuker.this.mc.thePlayer.rotationPitch = prevPitch;
                }

                try {
                    blockHitDelayField.setInt(Nuker.this.mc.playerController, prevBlockHitDelay);
                    blockDamageField.setFloat(Nuker.this.mc.playerController, prevBlockDamage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void destroyBlock(BlockPos blockPos, EnumFacing facing) {
        Nuker.this.mc.thePlayer.swingItem();
        Nuker.this.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, facing));
        Nuker.this.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, facing));
    }

    public EnumFacing getEnumFacing(final float posX, final float posY, final float posZ) {
        return EnumFacing.getFacingFromVector(posX, posY, posZ);
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

    private List<BlockPos> getBlocks(int radius) {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        for (int i = radius; i >= -radius; i--) {
            for (int k = radius; k >= -radius; k--) {
                for (int j = -radius; j <= radius; j++) {
                    int x = (int) (Minecraft.getMinecraft().thePlayer.posX + i);
                    int y = (int) (Minecraft.getMinecraft().thePlayer.posY + j);
                    int z = (int) (Minecraft.getMinecraft().thePlayer.posZ + k);

                    BlockPos blockPos = new BlockPos(x, y, z);
                    IBlockState block = Minecraft.getMinecraft().theWorld.getBlockState(blockPos);
                    if ((block.getBlock().getMaterial() == Material.air))
                        continue;

                    float blockDamage = block.getBlock().getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, blockPos);
                    if(blockDamage < 1.0F)
                        continue;;
                    blocks.add(blockPos);
                }
            }
        }
        return blocks;
    }

}
