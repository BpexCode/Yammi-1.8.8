package ru.yammi.module.misc;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class ChestStealer extends Module {

    public ChestStealer(){
        super("ChestStealer", Category.Misc, "Auto steal chest");
        this.getOptions().add(new Option("Auto close"));
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()){
            if (this.mc.thePlayer.openContainer != null
                    && this.mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) this.mc.thePlayer.openContainer;
                String strName = chest.getLowerChestInventory().getDisplayName().getUnformattedText()
                        .toLowerCase();
                if(strName.contains("\u0441\u0443\u043d\u0434\u0443\u043a")) {
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory()
                            && this.mc.thePlayer.openContainer != null; ++i) {
                        Slot slot = (Slot) chest.inventorySlots.get(i);
                        if (slot.getStack() != null) {
                            this.mc.playerController.windowClick(chest.windowId, i, 0, 1, this.mc.thePlayer);
                            // return;
                        }
                    }
                    if(this.getOption("Auto close").isBooleanValue()) {
                        if (this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiChest) {
                            mc.thePlayer.closeScreen();
                        }
                    }
                }
            }
        }
    }

}
