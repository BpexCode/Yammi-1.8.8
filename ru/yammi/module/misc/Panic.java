package ru.yammi.module.misc;

import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.event.EventTarget;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class Panic extends Module {

    public Panic(){
        super("Panic", Category.Misc, "Disable all modules");
    }

    public void onEnable(){
        for(Module module : Yammi.getInstance().getModules()){
            module.setState(false);
        }
        Config.store();
    }

}
