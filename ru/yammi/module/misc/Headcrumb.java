package ru.yammi.module.misc;

import net.minecraft.network.play.client.C03PacketPlayer;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.event.events.packet.PacketSendEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.Reflection;

import java.lang.reflect.Field;
import java.util.Random;

public class Headcrumb extends Module {

    private Random random = new Random();
    private float prevYaw = Float.MAX_VALUE;
    private float prevPitch =  Float.MAX_VALUE;
    private int newYaw = 0;
    private int newPitch = 0;

    public Headcrumb() {
        super("Headcrumb", Category.Misc, "Zaebal sdelai opisanie");
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if(this.getState()) {
            if(packetSendEvent.packet instanceof C03PacketPlayer || packetSendEvent.packet instanceof C03PacketPlayer.C04PacketPlayerPosition
            || packetSendEvent.packet instanceof C03PacketPlayer.C05PacketPlayerLook || packetSendEvent.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                newYaw = random.nextInt(90);
                newPitch = random.nextInt(90);
                int bound = random.nextInt(2);
                if(bound == 1) {
                    newYaw = -newYaw;
                    newPitch = -newPitch;
                }

                prevYaw = this.mc.thePlayer.rotationYaw;
                prevPitch = this.mc.thePlayer.rotationPitch;

                try {
                    Reflection.getField(C03PacketPlayer.class, "yaw", "field_149476_e", "d").setFloat(packetSendEvent.packet, newYaw);
                    Reflection.getField(C03PacketPlayer.class, "pitch", "field_149473_f", "e").setFloat(packetSendEvent.packet, newPitch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventTarget
    public void onPost(UpdateEvent.Post tickEvent) {
        if(this.getState()) {
            if(prevYaw != Float.MAX_VALUE) {
                //this.mc.thePlayer.rotationYaw = prevYaw;
            }
            if(prevPitch != Float.MAX_VALUE) {
                //this.mc.thePlayer.rotationPitch = prevPitch;
            }
        }
    }

    public void onDisable(){
        prevYaw = Float.MAX_VALUE;
        prevPitch = Float.MAX_VALUE;
        this.mc.thePlayer.rotationYaw = 45F;
        this.mc.thePlayer.rotationPitch = 0F;
    }
}
