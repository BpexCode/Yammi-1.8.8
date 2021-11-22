package ru.yammi.module.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.packet.PacketSendEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.TimerUtils;

public class Regen extends Module {

    private TimerUtils timerUtils = new TimerUtils(50L);

    public Regen() {
        super("Regen", Category.Combat, "Regen");
        this.getOptions().add(new Option("Speed", 0, 250));
        this.getOptions().add(new Option("Delay", 0, 1000));
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if(this.getState()) {
            timerUtils.setTimeout(this.getOption("Delay").getIntValue());
            if(timerUtils.isTimeReached()) {
                if (this.mc.thePlayer.onGround && !this.mc.thePlayer.capabilities.isCreativeMode
                        && this.mc.thePlayer.getFoodStats().getFoodLevel() >= 18
                        && this.mc.thePlayer.onGround) {
                    for (int jopa = 0; jopa < this.getOption("Speed").getIntValue(); jopa++) {
                        C03PacketPlayer.C04PacketPlayerPosition currentSpeed = new C03PacketPlayer.C04PacketPlayerPosition(
                                this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ,
                                this.mc.thePlayer.onGround);
                        this.mc.thePlayer.sendQueue.addToSendQueue(currentSpeed);
                    }
                }
            }
        }
    }
}
