package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.PositionUtils;
import ru.yammi.utils.TimerUtils;

public class FastSwim extends Module {

    private TimerUtils timerUtils = new TimerUtils(1000);

    public FastSwim(){
        super("FastSwim", Category.Movement, "Zalupa");
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if(this.getState()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (PositionUtils.isInLiquid()) {
                    this.mc.thePlayer.motionX *= 1.0D;
                    this.mc.thePlayer.motionZ *= 1.0D;
                    this.mc.thePlayer.motionY = 0.4D;
                }
            }
        }
    }

}
