package ru.yammi.utils.path;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;
import java.util.Iterator;

import ru.yammi.Yammi;
import ru.yammi.utils.TeamUtils;
import ru.yammi.utils.path.GotoAI;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class TPAuraProcessor
{
    private Minecraft mc = Minecraft.getMinecraft();

    private GotoAI ai;
    public EntityLivingBase target;
    private int entitycounter;
    private double startPosX;
    private double startPosY;
    private double startPosZ;

    public TPAuraProcessor() {
        this.entitycounter = 0;
    }

    private double getReach() {
        return Yammi.getInstance().getModule("TPAura").getOption("Radius").getDoubleValue();
    }

    private double getCPS() {
        return Yammi.getInstance().getModule("TPAura").getOption("CPS").getDoubleValue();
    }

    private boolean isbHit() {
        return true;
    }

    private boolean isLockView() {
        return false;
    }

    private int getMaxTargets() {
        return Yammi.getInstance().getModule("TPAura").getOption("Max targets").getIntValue();
    }

    private boolean isMultiTarget() {
        return getMaxTargets() != 1.0;
    }

    public boolean isTpBack() {
        return true;
    }

    private long prevTime;

    public boolean hasPassed(final double milli) {
        return this.getTime() - this.prevTime >= milli;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public void reset() {
        this.prevTime = this.getTime();
    }

    public void setup() {
    }

    public static float[] getRotations(final Entity ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.getEntityBoundingBox().maxY - 4.0;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getRotationFromPosition(final double x, final double z, final double y) {
        final double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        final double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        final double yDiff = y - Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight();
        final double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[] { yaw, pitch };
    }

    public void onPreMotionUpdate() {
        {
            if (!this.hasPassed(1000.0 / this.getCPS())) {
                return;
            }

            //final List<EntityLivingBase> entities = collect();
            //final List<EntityLivingBase> entities = TeamUtils.getClosestEntities((float)this.getReach());
            final List<EntityLivingBase> entities = collect();
            for (final EntityLivingBase entity : entities) {
                if (this.entitycounter >= this.getMaxTargets()) {
                    break;
                }
                if (entity != null && entity instanceof EntityLivingBase && (this.mc.thePlayer.getDistanceToEntity(entity) <= this.getReach())) {
                    this.target = entity;
                    ++this.entitycounter;
                    this.tpToEntity(entity);
                }
                else {
                    this.target = null;
                }
            }
            this.entitycounter = 0;
            reset();
        }
    }

    private List<EntityLivingBase> collect(){
        List<EntityLivingBase> result = new ArrayList<EntityLivingBase>();
        for(Object obj : this.mc.theWorld.loadedEntityList) {
            if(obj != null && obj != this.mc.thePlayer && obj instanceof EntityLivingBase) {
                if(this.mc.thePlayer.getDistanceToEntity((Entity)obj) <= this.getReach())
                result.add((EntityLivingBase) obj);
            }
        }

        return result;
    }

    public void onPostMotionUpdate() {
        if (!this.isMultiTarget() && this.target != null && target instanceof EntityPlayer  && (this.mc.thePlayer.getDistanceToEntity(this.target) <= this.getReach()) && this.hasPassed(1000.0 / this.getCPS())) {
            this.tpToEntity(this.target);
            reset();
        }
    }

    private void faceTarget(final EntityLivingBase entity) {
        final float[] rotations = getRotations(entity);
        if (rotations != null) {
            Minecraft.getMinecraft().thePlayer.rotationYaw = rotations[0];
            Minecraft.getMinecraft().thePlayer.rotationPitch = rotations[1] + 1.0f;
        }
    }

    public void tpToEntity(final EntityLivingBase entity) {
        if (entity != null) {
//            final double oldPosX = Fly.fl.mc.thePlayer.posX;
//            final double oldPosY = Fly.fl.mc.thePlayer.posY;
//            final double oldPosZ = Fly.fl.mc.thePlayer.posZ;
            try
            {
                this.ai = new GotoAI(entity);
                this.ai.update("infiniteaura");
                if (this.ai.isDone() || this.ai.isFailed()) {
                    this.ai.isFailed();
                    this.disable();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                this.disable();
            }
            //Fly.fl.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(oldPosX, oldPosY, oldPosZ, true));
        }
    }

    private void disable() {
        this.ai.stop();
    }

    public void onEnable() {
        reset();
        this.entitycounter = 0;
    }

    public void onDisable() {
        this.entitycounter = 0;
    }

    public float[] getRotationsNeeded(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        final double diffZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        }
        else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        return new float[] { Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch) };
    }


}
