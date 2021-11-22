package ru.yammi.module.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.Reflection;

import org.lwjgl.input.Keyboard;

public class BunnyHop extends Module {

    public BunnyHop() {
        super("BunnyHop", Category.Movement, "You jump like a rabbit!");
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if(this.getState()) {
            if(this.mc.currentScreen == null) {
                try {
                    EntityPlayer player = this.mc.thePlayer;
                    if (player.isSneaking() || player.isInWater() || Keyboard.isKeyDown(57) || Keyboard.isKeyDown(31)) {
                        return;
                    }

                    if (Keyboard.isKeyDown(17) || Keyboard.isKeyDown(30) || Keyboard.isKeyDown(32)) {
                        if (player.onGround) {
                            player.jump();
                            player.setSprinting(true);
                            moveBHop(4 * 5 * 0.1);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveBHop(double speed)
    {
        EntityPlayer player = this.mc.thePlayer;
        double yaw = player.rotationYaw;
        double moveForward = player.moveForward;
        double moveStrafing = player.moveStrafing;

        boolean isMoving = moveForward != 0 || moveStrafing != 0;
        boolean isMovingForward = moveForward > 0;
        boolean isMovingBackward = moveForward< 0;
        boolean isMovingRight = moveStrafing> 0;
        boolean isMovingLeft = moveStrafing < 0;
        boolean isMovingSideways = isMovingLeft || isMovingRight;
        boolean isMovingStraight = isMovingForward || isMovingBackward;

        if (isMoving)
        {
            if (isMovingForward && !isMovingSideways)
            {
                yaw += 0;
            }
            else if (isMovingBackward && !isMovingSideways)
            {
                yaw += 180;
            }
            else if (isMovingForward && isMovingLeft)
            {
                yaw += 45;
            }
            else if (isMovingForward)
            {
                yaw -= 45;
            }
            else if (!isMovingStraight && isMovingLeft)
            {
                yaw += 90;
            }
            else if (!isMovingStraight && isMovingRight)
            {
                yaw -= 90;
            }
            else if (isMovingBackward && isMovingLeft)
            {
                yaw += 135;
            }
            else if (isMovingBackward)
            {
                yaw -= 135;
            }

            yaw = Math.toRadians(yaw);

            player.motionX = -Math.sin(yaw) * speed;
            player.motionZ = Math.cos(yaw) * speed;
        }
    }
}
