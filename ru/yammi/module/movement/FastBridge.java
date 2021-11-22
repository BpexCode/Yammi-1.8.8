package ru.yammi.module.movement;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class FastBridge extends Module {

    public FastBridge(){
        super("FastBridge", Category.Movement, "Silent placing of blocks under you");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            BlockPos blockPos = new BlockPos(this.mc.thePlayer).add(0, -1, 0);
            boolean bl = this.mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air;
            if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
                if (!bl) {
                    setjump(false);
                }
                setsneak((this.mc.thePlayer.onGround) || (bl));
                if (!this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.motionX = 0.0D;
                    this.mc.thePlayer.motionZ = 0.0D;
                }
            } else {
                setsneak(bl);
            }
        }
    }

    public void setsneak(boolean value) {
        this.mc.thePlayer.movementInput.sneak = value;
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), value);
    }

    public void setjump(boolean value) {
        this.mc.thePlayer.movementInput.jump = value;
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), value);
    }

}
