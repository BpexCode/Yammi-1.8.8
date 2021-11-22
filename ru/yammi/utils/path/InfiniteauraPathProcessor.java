package ru.yammi.utils.path;

import com.google.common.collect.Iterables;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import ru.yammi.Yammi;

import java.util.ArrayList;

public class InfiniteauraPathProcessor extends APathProcessor
{
    private boolean creativeFlying;
    private boolean stopped;
    public static ArrayList<BlockPos> tpPosList;
    private EntityLivingBase target;

    static {
        InfiniteauraPathProcessor.tpPosList = new ArrayList<BlockPos>();
    }

    public InfiniteauraPathProcessor(final ArrayList<PathPos> path, final boolean creativeFlying, final EntityLivingBase target) {
        super(path);
        this.creativeFlying = creativeFlying;
        this.target = target;
    }
    public static void criticalAtPos(final double x, final double y, final double z) {
        if (Minecraft.getMinecraft().thePlayer.onGround)
        {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, true));
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.1E-5, z, false));
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
        }
    }

    public static void attackEntityAtPos(final EntityLivingBase entity, final boolean crit, final double x, final double y, final double z) {
        if (crit)
            //criticalAtPos(x, y, z);

        Minecraft.getMinecraft().thePlayer.swingItem();

        //final float sharpLevel = EnchantmentHelper.func_152377_a(Minecraft.getMinecraft().thePlayer.getHeldItem(), entity.getCreatureAttribute());
        //final boolean vanillaCrit = Minecraft.getMinecraft().thePlayer.fallDistance > 0.0f && !Minecraft.getMinecraft().thePlayer.onGround && !Minecraft.getMinecraft().thePlayer.isOnLadder() && !Minecraft.getMinecraft().thePlayer.isInWater() && !Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.blindness) && Minecraft.getMinecraft().thePlayer.ridingEntity == null;
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        //if (crit || vanillaCrit) {
        //    Minecraft.getMinecraft().thePlayer.onCriticalHit(entity);
        //}
        //if (sharpLevel > 0.0f) {
        //     Minecraft.getMinecraft().thePlayer.onEnchantmentCritical(entity);
        //}
    }

    @Override
    public void process() {
        if (this.target == null) {
            return;
        }
        for (int o = 0; o < this.path.size(); ++o) {
            final BlockPos pos = new BlockPos(this.mc.thePlayer);
            final BlockPos nextPos = this.path.get(this.index);
            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(nextPos.getX() + 0.5, nextPos.getY(), nextPos.getZ() + 0.5, true));
            InfiniteauraPathProcessor.tpPosList.add(nextPos);
            ++this.index;
            if (this.index < this.path.size()) {
                if (this.creativeFlying && this.index >= 2) {
                    final BlockPos prevPos = this.path.get(this.index - 1);
                    if (!this.path.get(this.index).subtract(prevPos).equals(prevPos.subtract(this.path.get(this.index - 2))) && !this.stopped) {
                        this.stopped = true;
                    }
                }
            }
            else {
                this.done = true;
            }

        }

        attackEntityAtPos(this.target, false, (int)this.target.posX + 0.5, GotoAI.removeDecimals(this.target.posY), (int)this.target.posZ + 0.5);

        for (int o = 0; o < InfiniteauraPathProcessor.tpPosList.size(); ++o) {
            try
            {
                final BlockPos tpPos = (BlockPos)Iterables.getLast((Iterable)InfiniteauraPathProcessor.tpPosList);
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5, true));
                InfiniteauraPathProcessor.tpPosList.remove(InfiniteauraPathProcessor.tpPosList.size() - 1);
            }
            catch (Exception ex) { }
        }
    }

    @Override
    public void lockControls() {
    }
}
