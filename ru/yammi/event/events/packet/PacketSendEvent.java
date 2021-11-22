package ru.yammi.event.events.packet;

import net.minecraft.network.Packet;
import ru.yammi.event.Event;

public class PacketSendEvent extends Event {

    public Packet packet;
    public Packet newPacket;

    public PacketSendEvent(Packet packet) {
        this.packet = packet;
    }
}
