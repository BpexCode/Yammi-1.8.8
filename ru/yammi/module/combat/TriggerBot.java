package ru.yammi.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MovingObjectPosition;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.TeamUtils;

import java.util.ArrayList;

public class TriggerBot extends Module {

    private long prevTime = 0L;

    public TriggerBot(){
        super("TriggerBot", Category.Combat, "Attacks the player you are looking at");

        this.getOptions().add(new Option("CPS", 0, 20));
        this.getOptions().add(new Option("Extra knock"));
        this.getOptions().add(new Option("Blocking"));
        this.getOptions().add(new Option("Only sword"));
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            if (!this.hasPassed(1000.0 / this.getCPS())) {
                return;
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit != null
                    && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                Entity target = this.mc.objectMouseOver.entityHit;
                boolean onlySword = this.getOption("Only sword").isBooleanValue();
                if(onlySword) {
                    if(this.mc.thePlayer.getHeldItem() == null)
                        return;
                    if(!(this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))
                        return;
                }
                attackEntity(target);
            }

            this.prevTime = this.getTime();
        }
    }

    private void attackEntity(Entity entity) {
        if(entity instanceof EntityPlayer && TeamUtils.validEntity((EntityLivingBase)entity)) {
            boolean extra = this.getOption("Extra knock").isBooleanValue();
            boolean blocking = this.getOption("Blocking").isBooleanValue();
            blocking = blocking && (mc.thePlayer.getHeldItem() != null
                    && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword));

            if (extra) {
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

            if (extra) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,
                        net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
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

}
