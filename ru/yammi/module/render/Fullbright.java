package ru.yammi.module.render;

import ru.yammi.event.EventTarget;
import ru.yammi.event.events.PostLoadEvent;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class Fullbright extends Module {

    private float storedGamma = 0F;

    public Fullbright(){
        super("Fullbright", Category.Render, "Set max gamma");
        this.getOptions().add(new Option("Gamma", 0F, 100F));
    }

    @EventTarget
    public void onLoad(PostLoadEvent postLoadEvent) {
        this.storedGamma = this.mc.gameSettings.gammaSetting;
    }

    @Override
    public void onEnable() {
        this.storedGamma = this.mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        this.mc.gameSettings.gammaSetting = this.storedGamma;
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent) {
        if(this.getState()) {
            float gammaMax = this.getOption("Gamma").getFloatValue();
            this.mc.gameSettings.gammaSetting = gammaMax;
        }
    }

}