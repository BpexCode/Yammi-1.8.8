package ru.yammi.module.combat;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.path.TPAuraProcessor;
import ru.yammi.utils.path.TPnBACKPathProcessor;

public class TPAura extends Module {

    private TPAuraProcessor processor;

    public TPAura(){
        super("TPAura", Category.Combat, "Teleport and attack to another players");
        this.getOptions().add(new Option("Radius", 0D, 100.0D));
        this.getOptions().add(new Option("CPS", 0D, 20.0D));
        this.getOptions().add(new Option("Max targets", 0, 10));
        this.getOptions().add(new Option("Only walk"));

        processor = new TPAuraProcessor();
    }

    @Override
    public void onDisable() {
        this.processor.onDisable();
    }

    @Override
    public void onEnable(){
        this.processor.onEnable();
    }

    @EventTarget
    public void onPreUpdate(UpdateEvent.Pre updateEventPre) {
        if(this.getState()) {
            processor.onPreMotionUpdate();
        }
    }

    @EventTarget
    public void onPostUpdate(UpdateEvent.Post updateEventPost) {
        if(this.getState()) {
            processor.onPostMotionUpdate();
        }
    }

}
