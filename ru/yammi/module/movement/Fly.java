package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class Fly extends Module {

    public Fly(){
        super("Fly", Category.Movement, "Become a God! You can fly!");
        this.getOptions().add(new Option("Speed", 0, 10));
        this.getOptions().add(new Option("Vanilla fly" ));
    }

    @Override
    public void onDisable() {
        this.mc.thePlayer.capabilities.isFlying = false;
    }

    @Override
    public void onEnable() {
        this.mc.thePlayer.capabilities.isFlying = true;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(this.getState()) {
            this.mc.thePlayer.capabilities.isFlying = true;

            int speed = this.getOption("Speed").getIntValue();
            float var10 = (float) speed / 5.0F * 0.9F;
            this.mc.thePlayer.capabilities.setFlySpeed(0.35F * var10);

            if(this.getOption("Vanilla fly").isBooleanValue())
                return;
            if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.mc.thePlayer.motionY = 0.02D;
            } else {
                this.mc.thePlayer.motionY -= 0.006D;
            }

            if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
                this.mc.thePlayer.motionY -= 0.03D;
            }
        }
    }

}
