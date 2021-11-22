package ru.yammi.module.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.packet.PacketReadEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.Reflection;

import java.lang.reflect.Field;

public class AntiKnockback extends Module {

    public AntiKnockback() {
        super("AntiKnockback", Category.Combat, "Zalupa");
        this.getOptions().add(new Option("Ignore explosions"));
        this.getOptions().add(new Option("Velocity factor", 0D, 1D));
    }

    @EventTarget
    public void onPacketReceive(PacketReadEvent packetReadEvent) {
        if(this.getState()) {
            if(packetReadEvent.packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity entityVelocity = (S12PacketEntityVelocity)packetReadEvent.packet;
                if(entityVelocity.getEntityID() == this.mc.thePlayer.getEntityId()) {
                    double velocityFactor = this.getOption("Velocity factor").getDoubleValue();
                    packetReadEvent.setCancel(true);
                    if (!(velocityFactor <= 0.0D)) {
                        mc.thePlayer.addVelocity(((double) entityVelocity.getMotionX() / 8000.0D) * velocityFactor,
                                ((double) entityVelocity.getMotionY() / 8000.0D) * velocityFactor,
                                ((double) entityVelocity.getMotionZ() / 8000.0D) * velocityFactor);
                    }
                }
            }
            boolean explosions = this.getOption("Ignore explosions").isBooleanValue();
            if(explosions) {
                if (packetReadEvent.packet instanceof S27PacketExplosion) {
                    S27PacketExplosion explosion = (S27PacketExplosion) packetReadEvent.packet;
                    packetReadEvent.setCancel(true);
                }
            }
        }
    }
}
