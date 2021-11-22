package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class FastLadder extends Module {

    public FastLadder(){
        super("FastLadder", Category.Movement, "You climb the ladder very quickly");
    }

    @EventTarget
    public void onUpdate(TickEvent event) {
        if(this.getState()) {
            if(this.mc.thePlayer.isOnLadder()) {
                if(this.mc.thePlayer.rotationPitch < 0) {
                    this.mc.thePlayer.motionY = 1.4D;
                } else {
                    this.mc.thePlayer.motionY = -1.4D;
                    this.mc.thePlayer.moveEntity(this.mc.thePlayer.motionX, this.mc.thePlayer.motionY, this.mc.thePlayer.motionZ);
                }
            }
        }
    }

}
