package ru.yammi.module.misc;

import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.event.EventBus;
import ru.yammi.gui.YammiScreen;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class SelfDestruct extends Module {

    public SelfDestruct(){
        super("SelfDestruct", Category.Misc, "Unload cheat from game");
    }

    @Override
    public void onEnable() {
        this.setState(false);
        EventBus.clear();
        Config.store();
        if(this.mc.currentScreen != null && this.mc.currentScreen instanceof YammiScreen){
            this.mc.displayGuiScreen(null);
        }
    }
}
