package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class AutoSprint extends Module {

    public AutoSprint(){
        super("AutoSprint", Category.Movement, "Toggle sprinting");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            if (this.mc.gameSettings.keyBindForward.isKeyDown() || this.mc.gameSettings.keyBindRight.isKeyDown()
                    || this.mc.gameSettings.keyBindLeft.isKeyDown() || this.mc.gameSettings.keyBindBack.isKeyDown()) {
                this.mc.thePlayer.setSprinting(true);
            }
        }
    }

}
