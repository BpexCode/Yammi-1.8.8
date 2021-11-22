package ru.yammi.module.misc;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class Haste extends Module {

    public Haste() {
        super("Haste", Category.Misc, "Makes you mine faster.");
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(this.getState()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 0, 1));
            }
        }
    }

}