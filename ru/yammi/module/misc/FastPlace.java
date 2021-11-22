package ru.yammi.module.misc;

import net.minecraft.client.Minecraft;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.Reflection;

public class FastPlace extends Module {

    public FastPlace() {
        super("FastPlace", Category.Misc, "You place blocks very fast");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()){
            try {
                Reflection.getField(Minecraft.class, "rightClickDelayTimer", "field_71467_ac", "ap").setInt(this.mc, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
