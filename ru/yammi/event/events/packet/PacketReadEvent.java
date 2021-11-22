package ru.yammi.event.events.packet;

import net.minecraft.network.Packet;
import ru.yammi.event.Event;

public class PacketReadEvent extends Event {

    public Packet packet;

    public PacketReadEvent(Packet packet) {
        this.packet = packet;
    }
}
