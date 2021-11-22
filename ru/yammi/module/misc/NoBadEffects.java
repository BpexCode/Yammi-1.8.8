package ru.yammi.module.misc;

import net.minecraft.potion.Potion;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class NoBadEffects extends Module {

    public NoBadEffects(){
        super("NoBadEffects", Category.Misc, "Delete all bad effects from you");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            if (this.mc.thePlayer.isPotionActive(Potion.blindness)) {
                this.mc.thePlayer.removePotionEffect(Potion.blindness.id);
            }

            if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                this.mc.thePlayer.removePotionEffect(Potion.confusion.id);
            }

            if (this.mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
                this.mc.thePlayer.removePotionEffect(Potion.digSlowdown.id);
            }
        }
    }

}