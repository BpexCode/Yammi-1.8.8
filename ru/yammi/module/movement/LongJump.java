package ru.yammi.module.movement;

import net.minecraft.util.MathHelper;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class LongJump extends Module {

    public LongJump(){
        super("LongJump", Category.Movement, "You can jump very far");
        this.getOptions().add(new Option("Power", 0D, 100D));
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()) {
            if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                double current = this.getOption("Power").getDoubleValue();
                current = this.getOption("Power").getMaxDoubleValue() - current + 1D;
                this.mc.thePlayer.setSprinting(true);
                final double dspeed = Math.sqrt(this.mc.thePlayer.motionX * this.mc.thePlayer.motionX
                        + this.mc.thePlayer.motionZ * this.mc.thePlayer.motionZ + 10.0 / (10.0 * current));
                this.mc.thePlayer.motionX = -MathHelper.sin(getDirection()) * dspeed;
                this.mc.thePlayer.motionZ = MathHelper.cos(getDirection()) * dspeed;

                if (this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.jump();
                    this.mc.thePlayer.motionY *= 0.94356256;
                } else if (this.mc.thePlayer.isAirBorne && !this.mc.thePlayer.onGround) {
                    final double speed = Math.sqrt(this.mc.thePlayer.motionX * this.mc.thePlayer.motionX
                            + this.mc.thePlayer.motionZ * this.mc.thePlayer.motionZ + 10.0 / (10.0 * current))
                            + current / (current * 5000.0f);
                    this.mc.thePlayer.motionX = -MathHelper.sin(getDirection()) * speed;
                    this.mc.thePlayer.motionZ = MathHelper.cos(getDirection()) * speed;
                }
            }
        }
    }

    public float getDirection() {
        float yaw = this.mc.thePlayer.rotationYaw;
        final float forward = this.mc.thePlayer.moveForward;
        final float strafe = this.mc.thePlayer.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        return yaw * 0.017453292f;
    }

}
