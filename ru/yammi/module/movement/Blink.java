package ru.yammi.module.movement;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.packet.PacketSendEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Blink extends Module {

    private EntityOtherPlayerMP oldPlayer;

    private List<Packet> packetList = new ArrayList<>();

    public Blink() {
        super("Blink", Category.Movement, "Zalupa");
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if(this.getState()) {
            if(packetSendEvent.packet instanceof C03PacketPlayer
            || packetSendEvent.packet instanceof C03PacketPlayer.C04PacketPlayerPosition
            || packetSendEvent.packet instanceof C03PacketPlayer.C05PacketPlayerLook
            || packetSendEvent.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                packetList.add(packetSendEvent.packet);
                packetSendEvent.setCancel(true);
            }
        }
    }

    public void onEnable() {
        packetList.clear();
        oldPlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());

        oldPlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        oldPlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        oldPlayer.clonePlayer(mc.thePlayer, true);
        oldPlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        oldPlayer.rotationYaw = mc.thePlayer.rotationYaw;

        this.mc.theWorld.addEntityToWorld(-123, oldPlayer);

        //this.mc.thePlayer = fakeEntity;
        //this.mc.setRenderViewEntity(fakeEntity);
    }

    public void onDisable(){
        for(Packet packet : this.packetList) {
            this.mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
        packetList.clear();

        this.mc.theWorld.removeEntityFromWorld(-123);
        //this.mc.setRenderViewEntity(renderViewEntity);
    }
}
