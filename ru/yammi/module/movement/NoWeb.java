package ru.yammi.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.Reflection;

public class NoWeb extends Module {

    public NoWeb() {
        super("NoWeb", Category.Movement, "You don't slow down in the web");
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()) {
            try {
                Reflection.getField(Entity.class, "isInWeb", "field_70134_J", "H").setBoolean(this.mc.thePlayer, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
