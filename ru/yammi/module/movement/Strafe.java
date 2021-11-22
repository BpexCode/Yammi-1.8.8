package ru.yammi.module.movement;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class Strafe extends Module {

    public Strafe(){
        super("Strafe", Category.Movement, "Speeds up the movement");
        this.getOptions().add(new Option("Power", 0F, 1F));
    }

    @EventTarget
    public void onUpdate(TickEvent event){
        if(this.getState()){
            if ((this.mc.thePlayer.hurtTime <= 0)
                    && ((this.mc.thePlayer.onGround) || ((true) && (!this.mc.thePlayer.isInWater())))) {
                float dir = this.mc.thePlayer.rotationYaw;
                if (this.mc.thePlayer.moveForward < 0.0F) {
                    dir += 180.0F;
                }
                if (this.mc.thePlayer.moveStrafing > 0.0F) {
                    dir -= 90.0F * (this.mc.thePlayer.moveForward > 0.0F ? 0.68F
                            : this.mc.thePlayer.moveForward < 0.0F ? -0.5F : 1.0F);
                }
                if (this.mc.thePlayer.moveStrafing < 0.0F) {
                    dir += 90.0F * (this.mc.thePlayer.moveForward > 0.0F ? 0.68F
                            : this.mc.thePlayer.moveForward < 0.0F ? -0.5F : 1.0F);
                }
                double hOff = 0.221D;
                if (this.mc.thePlayer.isSprinting()) {
                    hOff *= 0.3190000119209289D;
                }
                if (this.mc.thePlayer.isSneaking()) {
                    hOff *= 0.3D;
                }
                hOff = this.getOption("Power").getFloatValue();
                float var9 = (float) ((float) Math.cos((dir + 90.0F) * 3.141592653589793D / 180.0D)
                        * hOff);
                float zD = (float) ((float) Math.sin((dir + 90.0F) * 3.141592653589793D / 180.0D)
                        * hOff);
                if ((mc.gameSettings.keyBindForward.isKeyDown())
                        || (mc.gameSettings.keyBindLeft.isKeyDown())
                        || (mc.gameSettings.keyBindRight.isKeyDown())
                        || (mc.gameSettings.keyBindBack.isKeyDown()))
                {
                    this.mc.thePlayer.motionX = var9;
                    this.mc.thePlayer.motionZ = zD;
                }
            }
        }
    }

}
