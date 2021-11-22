package ru.yammi.module.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", Category.Movement, "Zalupa");
    }


    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if(this.getState()) {
            if (mc.thePlayer.fallDistance > 2.0f) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            }
        }
    }
}
