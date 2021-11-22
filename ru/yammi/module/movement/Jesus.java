package ru.yammi.module.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class Jesus extends Module {

    public Jesus() {
        super("Jesus", Category.Movement, "You can walk on fluids");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()){
            int x = (int)Math.floor(this.mc.thePlayer.posX);
            int y = (int)Math.floor(this.mc.thePlayer.posY);
            int z = (int)Math.floor(this.mc.thePlayer.posZ);
            BlockPos blockPos = new BlockPos(x, y, z);
            IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
            if(blockState.getBlock() instanceof BlockLiquid){
                this.mc.thePlayer.motionY = 0;
                if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode())) {
                    this.mc.thePlayer.motionY = 0.4;
                }
            }
        }
    }

}
