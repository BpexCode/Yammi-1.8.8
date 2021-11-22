package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class InstantJump extends Module {

    public InstantJump(){
        super("InstantJump", Category.Movement, "You can jump in air");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()) {
            if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.mc.thePlayer.jump();
            }
        }
    }



}
