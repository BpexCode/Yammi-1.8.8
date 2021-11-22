package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class Spider extends Module {

    public Spider(){
        super("Spider", Category.Movement, "You climb walls like a spider!");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()){
            if (this.mc.thePlayer.isCollidedHorizontally) {
                this.mc.thePlayer.motionY = 0.14D;
            }
        }
    }

}
