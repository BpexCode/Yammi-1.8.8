package ru.yammi.module.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.MoveHook;

public class NoSlowDown extends Module {

    private MoveHook moveHook = new MoveHook(this.mc.gameSettings);
    private MovementInput origMove;

    public NoSlowDown(){
        super("NoSlowDown", Category.Movement, "You don't slow down when you eat, shoot, e.t.c");
    }

    @Override
    public void onDisable() {
        this.mc.thePlayer.movementInput = origMove;
    }

    @Override
    public void onEnable() {
        origMove = this.mc.thePlayer.movementInput;
    }

    @EventTarget
    public void onMove(TickEvent event) {
        if(this.getState()) {
            if(this.origMove == null) {
                this.origMove = this.mc.thePlayer.movementInput;
            }
            if(this.mc.thePlayer.movementInput != null && !(this.mc.thePlayer.movementInput instanceof MoveHook)){
                this.mc.thePlayer.movementInput = moveHook;
            }
        }
    }

}
