package ru.yammi.module.movement;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.*;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.TimerUtils;

import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {

    public TimerUtils timer;
    boolean isBridging = false;
    BlockPos blockDown = null;
    public static float[] facingCam = null;
    float startYaw = 0;
    float startPitch = 0;
    public List blacklist = Arrays.asList(Blocks.air, Blocks.water, Blocks.torch, Blocks.redstone_torch, Blocks.ladder,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.chest, Blocks.torch, Blocks.anvil,
            Blocks.web, Blocks.redstone_torch, Blocks.brewing_stand, Blocks.waterlily, Blocks.farmland, Blocks.sand,
            Blocks.beacon);

    public Scaffold(){
        super("Scaffold", Category.Movement, "You place blocks under you when you walk through the air");
    }

    @EventTarget
    public void onUpdate(TickEvent event) {
        if(this.getState()) {
            int i;
            if (this.isHotbarEmpty()) {
                for (i = 9; i < 36; ++i) {
                    Item item;
                    if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                            || !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()) instanceof ItemBlock)
                            || this.blacklist.contains(((ItemBlock) item).getBlock()))
                        continue;
                    this.swap(i, 7);
                    break;
                }
            }
            i = 36;
            while (i < 45) {
                Item item;
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                        && (item = (mc.thePlayer.inventoryContainer.getSlot(i).getStack())
                        .getItem()) instanceof ItemBlock
                        && !this.blacklist.contains(((ItemBlock) item).getBlock())) {
                    BlockPos blockUnder = null;
                    BlockPos blockBef = null;

                    BlockPos under = new BlockPos(mc.thePlayer.posX + mc.thePlayer.motionX * 2, mc.thePlayer.posY - 0.01,
                            mc.thePlayer.posZ + mc.thePlayer.motionZ * 2);
                    if (mc.theWorld.getBlockState(under).getBlock().getMaterial() == Material.air) {
                        blockUnder = new BlockPos(mc.thePlayer.posX + mc.thePlayer.motionX * 2, mc.thePlayer.posY - 0.01,
                                mc.thePlayer.posZ + mc.thePlayer.motionZ * 2);
                        for (EnumFacing facingh : EnumFacing.values()) {
                            BlockPos offset = blockUnder.offset(facingh);
                            if (mc.theWorld.getBlockState(offset).getBlock().getMaterial() != Material.air) {
                                blockBef = offset;
                                break;
                            }
                        }
                    }

                    if (blockUnder == null) {
                        return;
                    }
                    if (blockBef == null) {
                        return;
                    }

                    MovingObjectPosition pos = mc.theWorld.rayTraceBlocks(getVec3(blockUnder).addVector(0.5, 0.5, 0.5),
                            getVec3(blockBef).addVector(0.5, 0.5, 0.5));
                    if (pos == null) {
                        return;
                    }
                    Vec3 hitVec = pos.hitVec;
                    float f = 0;
                    float f1 = 0;
                    float f2 = 0;

                    int last = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i - 36));
                    mc.thePlayer.inventory.currentItem = i - 36;
                    mc.thePlayer.swingItem();
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(blockBef, pos.sideHit.getIndex(),
                            mc.thePlayer.getCurrentEquippedItem(), f, f1, f2));
                    mc.thePlayer.inventory.currentItem = last;
                }
                ++i;
            }
        }
    }

    public void doScafold(){

    }

    private boolean isHotbarEmpty() {
        for (int i = 36; i < 45; ++i) {
            Item item;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    || !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock)
                    || this.blacklist.contains(((ItemBlock) item).getBlock()))
                continue;
            return false;
        }
        return true;
    }

    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                continue;
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (!(is.getItem() instanceof ItemBlock) || this.blacklist.contains(((ItemBlock) item).getBlock()))
                continue;
            blockCount += is.stackSize;
        }
        return blockCount;
    }

    protected void swap(int slot, int hotbarNum) {
        this.mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, mc.thePlayer);
    }

    public Vec3 getVec3(BlockPos blockPos) {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Vec3 scale(Vec3 vec, double factor)
    {
        return new Vec3(vec.xCoord * factor, vec.yCoord * factor, vec.zCoord * factor);
    }

    public float[] getNeededRotations(Vec3 vec) {
        final Vec3 eyesPos = getEyesPos();
        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { this.mc.thePlayer.rotationYaw + wrapDegrees(yaw - this.mc.thePlayer.rotationYaw), this.mc.thePlayer.rotationPitch + wrapDegrees(pitch - this.mc.thePlayer.rotationPitch) };
    }

    public float wrapDegrees(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }

    public Vec3 getEyesPos() {
        return new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY +this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
    }

    public boolean canBeClicked(final BlockPos pos) {
        return this.mc.theWorld.getBlockState(pos).getBlock().canCollideCheck(this.mc.theWorld.getBlockState(pos), false);
    }

    @Override
    public void onEnable() {
        blockDown = null;
        facingCam = null;
        isBridging = false;
        startYaw = 0;
        startPitch = 0;
    }

    @Override
    public void onDisable() {
        facingCam = null;
    }

}
