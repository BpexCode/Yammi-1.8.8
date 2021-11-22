package ru.yammi.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.TeamUtils;

import java.util.ArrayList;

public class Mobaura extends Module {

    private long prevTime = 0L;
    private int entitycounter = 0;

    public Mobaura(){
        super("Mobaura", Category.Combat, "Auto attacking mobs");

        this.getOptions().add(new Option("CPS", 0, 20));
        this.getOptions().add(new Option("Radius", 0F, 7F));
        this.getOptions().add(new Option("Max targets", 0, 20));
        this.getOptions().add(new Option("Extra knock"));
        this.getOptions().add(new Option("Blocking"));
    }

    @EventTarget
    public void onUpdate(TickEvent event) {
        if(this.getState()) {
            if (!this.hasPassed(1000.0 / this.getCPS())) {
                return;
            }

            final ArrayList<EntityLivingBase> entities = TeamUtils.getClosestEntities((float)this.getOption("Radius").getFloatValue());
            for (final EntityLivingBase entity : entities) {
                if (this.entitycounter >= this.getOption("Max targets").getIntValue()) {
                    break;
                }
                if (entity != null && !(entity instanceof EntityPlayer) && (this.mc.thePlayer.getDistanceToEntity(entity) <= this.getOption("Radius").getFloatValue())) {
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
        this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK.ATTACK));

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

}
