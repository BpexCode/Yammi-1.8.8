package ru.yammi.module.misc;

import net.minecraft.network.play.client.C01PacketChatMessage;
import ru.yammi.command.CommandManager;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.packet.PacketSendEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class CMD extends Module {

    private CommandManager commandManager = new CommandManager();

    public CMD() {
        super("CMD", Category.Misc, "Zalupa");
    }

    @EventTarget
    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if(this.getState()) {
            if(packetSendEvent.packet instanceof C01PacketChatMessage) {
                C01PacketChatMessage chatMessage = (C01PacketChatMessage)packetSendEvent.packet;
                String message = chatMessage.getMessage();
                if(message != null && message.startsWith("#")) {
                    if(commandManager.execute(message))
                        packetSendEvent.setCancel(true);
                }
            }
        }
    }
}
