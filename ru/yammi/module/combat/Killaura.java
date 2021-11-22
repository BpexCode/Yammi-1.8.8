package ru.yammi.module.combat;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.gui.Render2DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.R2DUtils;
import ru.yammi.utils.TeamUtils;

import java.util.ArrayList;

public class Killaura extends Module {

    private long prevTime = 0L;
    private int entitycounter = 0;

    public Killaura(){
        super("Killaura", Category.Combat, "Attack another players");
        this.getOptions().add(new Option("CPS", 0, 20));
        this.getOptions().add(new Option("Radius", 0F, 7F));
        this.getOptions().add(new Option("Max targets", 0, 20));
        this.getOptions().add(new Option("Extra knock"));
        this.getOptions().add(new Option("Blocking"));
        this.getOptions().add(new Option("FOV", 0, 360));
    }

    @EventTarget
    public void onRender2D(Render2DEvent render2DEvent) {
        if(this.getState()) {
            int fov = this.getOption("FOV").getIntValue();
            if(fov == 0 || fov == this.getOption("FOV").getMaxIntValue())
                return;

            ScaledResolution scaledResolution = new ScaledResolution(this.mc);

            int centerX = scaledResolution.getScaledWidth() / 2;
            int centerY = scaledResolution.getScaledHeight() / 2;

            fov *= scaledResolution.getScaleFactor();
            R2DUtils.drawFullCircleIngame(centerX, centerY, fov, -1, false);
        }
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            if (!this.hasPassed(1000.0 / this.getCPS())) {
                return;
            }

            int fov = this.getOption("FOV").getIntValue();
            ArrayList<EntityLivingBase> entities = TeamUtils.getClosestEntities((float)this.getOption("Radius").getFloatValue());
            for (EntityLivingBase entity : entities) {
                if (this.entitycounter >= this.getOption("Max targets").getIntValue()) {
                    break;
                }
                if (entity != null && entity instanceof EntityPlayer && (this.mc.thePlayer.getDistanceToEntity(entity) <= this.getOption("Radius").getFloatValue())) {
                    if(fov != 0) {
                        if(!isInAttackFOV(entity)) {
                            continue;
                        }
                    }
                    ++this.entitycounter;
                    this.attackEntity(entity);
                }
            }
            this.entitycounter = 0;
            this.prevTime = this.getTime();
        }
    }

    private void attackEntity(Entity entity) {
        boolean extra = this.getOption("Extra knock").isBooleanValue();
        boolean blocking = this.getOption("Blocking").isBooleanValue();
        blocking = blocking && (mc.thePlayer.getHeldItem() != null
                && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword));

        if(extra) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,
                    net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING));
            this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,
                    net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING));
        }

        if (blocking && mc.thePlayer.getCurrentEquippedItem() != null) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.getCurrentEquippedItem());
        }

        this.mc.thePlayer.swingItem();
        this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));

        if(extra) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,
                    net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    private int getCPS(){
        return this.getOption("CPS").getIntValue();
    }

    public boolean hasPassed(final double milli) {
        return this.getTime() - this.prevTime >= milli;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    private float wrapDegrees(float Value) {
        Value = ((int)Value % 360);

        if (Value >= 180.0F)
        {
            Value -= 360.0F;
        }

        if (Value < -180.0F)
        {
            Value += 360.0F;
        }

        return Value;
    }

    private float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }

        Entity player = this.mc.thePlayer;
        double playerX = player.posX;
        double playerY = player.posY;
        double playerZ = player.posZ;

        float rotationYaw = player.rotationYaw;
        float rotationPitch = player.rotationPitch;

        double entityX = entity.posX;
        double entityY = entity.posY;
        double entityZ = entity.posZ;

        float enemyHeight = entity.height;
        float eyeHeight = player.getEyeHeight();

        double diffX = entityX - playerX;
        double diffZ = entityZ - playerZ;
        double diffY = entityY + entity.getEyeHeight() - (playerY + eyeHeight);

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0F;
        float pitch = (float)-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);

        return new float[] {
            rotationYaw
                    + wrapDegrees(yaw - rotationYaw),
                    rotationPitch
                            + wrapDegrees(pitch - rotationPitch) };
    }

    private int GetDistanceFromMouse(Entity entity) {
        float[] neededRotations = getRotationsNeeded(entity);
        if (neededRotations != null) {
            Entity player = this.mc.thePlayer;
            float neededYaw = player.rotationYaw - neededRotations[0];
            float neededPitch = player.rotationPitch - neededRotations[1];
            float distanceFromMouse = (float)Math.sqrt(neededYaw * neededYaw + neededPitch * neededPitch * 2.0f);
            return (int)distanceFromMouse;
        }
        return -1;
    }


    private boolean isInAttackFOV(Entity entity) {
        int mouse = GetDistanceFromMouse(entity);
        return mouse <= this.getOption("FOV").getIntValue();
    }

}
