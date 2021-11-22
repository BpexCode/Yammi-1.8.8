package ru.yammi.module.misc;

import org.lwjgl.input.Keyboard;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.PostLoadEvent;
import ru.yammi.gui.YammiScreen;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class ClickGUI extends Module {

    private YammiScreen screen;

    public ClickGUI() {
        super("ClickGUI", Category.Misc, "Ingame cheat menu");
        this.setKeybind(Keyboard.KEY_RSHIFT);
        this.getOptions().add(new Option("Particles"));
    }

    @EventTarget
    public void onPostLoad(PostLoadEvent postLoadEvent) {
        this.setState(false);
    }

    public void onDisable(){
        if(this.mc.currentScreen != null) {
            this.mc.displayGuiScreen(null);
        }
    }

    public void onEnable(){
        if(this.mc.currentScreen != null)
            return;
        this.mc.displayGuiScreen(this.getClickGUIScreen(false));
    }

    public YammiScreen getClickGUIScreen(boolean fromConfig) {
        if(this.screen == null) {
            screen = new YammiScreen(fromConfig);
        }
        return screen;
    }

}
