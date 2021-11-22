package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class MultiJump extends Module {

    public MultiJump(){
        super("MultiJump", Category.Movement, "You jump very fast");
    }

    @EventTarget
    public void onUpdate(TickEvent event) {
        if(this.getState()){
            if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.mc.thePlayer.onGround = true;
                this.mc.thePlayer.setJumping(false);
            }
        }
    }

}
