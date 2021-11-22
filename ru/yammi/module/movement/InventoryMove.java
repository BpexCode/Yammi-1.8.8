package ru.yammi.module.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class InventoryMove extends Module {

    public InventoryMove(){
        super("InventoryMove", Category.Movement, "Allows you to move in inventory and another menu");
    }

    @EventTarget
    public void onUpdate(TickEvent event) {
        if(this.getState()) {
            if (this.mc.currentScreen != null) {
                if(this.mc.currentScreen instanceof GuiChat)
                    return;
                KeyBinding[] keys = new KeyBinding[] { this.mc.gameSettings.keyBindForward,
                        this.mc.gameSettings.keyBindBack, this.mc.gameSettings.keyBindLeft,
                        this.mc.gameSettings.keyBindRight, this.mc.gameSettings.keyBindJump };
                for (KeyBinding up : keys) {
                    KeyBinding.setKeyBindState(up.getKeyCode(), Keyboard.isKeyDown(up.getKeyCode()));
                }
            }
        }
    }

}
