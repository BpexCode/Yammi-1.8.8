package ru.yammi.module.misc;

import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class ResetKeybinds extends Module {

    public ResetKeybinds(){
        super("Reset keybinds", Category.Misc, "Reset modules keybind");
    }

    @Override
    public void onEnable() {
        this.setState(false);
        for(Module module : Yammi.getInstance().getModules()) {
            //Yammi.getInstance().getModules().forEach(module -> {
            if (!module.getName().equalsIgnoreCase("ClickGUI")) {
                module.setKeybind(0);
            }
            //});
        }
        Config.store();
    }
}
