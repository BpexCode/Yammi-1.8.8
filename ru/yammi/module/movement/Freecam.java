package ru.yammi.module.movement;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class Freecam extends Module {

    private BlockPos oldBlockPos;
    private EntityOtherPlayerMP oldPlayer;

    public Freecam() {
        super("Freecam", Category.Movement, "Zalupa");
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setPosition(oldBlockPos.getX(), oldBlockPos.getY(), oldBlockPos.getZ());
        mc.thePlayer.noClip = false;

        mc.thePlayer.rotationYawHead = oldPlayer.rotationYawHead;
        mc.thePlayer.rotationPitch = oldPlayer.rotationPitch;

        mc.theWorld.removeEntityFromWorld(-123);
        oldPlayer = null;
    }

    @Override
    public void onEnable() {
        oldBlockPos = new BlockPos(mc.thePlayer.getPosition());

        oldPlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());

        oldPlayer.setPosition(oldBlockPos.getX(), oldBlockPos.getY(), oldBlockPos.getZ());

        oldPlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        oldPlayer.clonePlayer(mc.thePlayer, true);
        oldPlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        oldPlayer.rotationYaw = mc.thePlayer.rotationYaw;

        mc.theWorld.addEntityToWorld(-123, oldPlayer);
    }

    @EventTarget
    public void onUpdate(UpdateEvent updateEvent) {
        if(this.getState()) {
            mc.thePlayer.noClip = true;
        }
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if(this.getState()) {
            mc.thePlayer.noClip = true;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.jumpMovementFactor = 0.1f;
            mc.thePlayer.onGround = false;
            oldPlayer.setSneaking(mc.thePlayer.isSneaking());
            oldPlayer.setSprinting(mc.thePlayer.isSprinting());
            oldPlayer.isSwingInProgress = mc.thePlayer.isSwingInProgress;
            oldPlayer.swingProgress = mc.thePlayer.swingProgress;
            oldPlayer.swingProgressInt = mc.thePlayer.swingProgressInt;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.motionY += 0.4;
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.thePlayer.motionY += -0.4;
            }
        }
    }
}
