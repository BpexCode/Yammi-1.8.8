package ru.yammi.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.event.EventBus;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.event.events.gui.Render2DEvent;

public class ProfilerHook extends Profiler {

    private boolean configLoaded = false;
    private Minecraft mc = Minecraft.getMinecraft();
    private String lastSection = "unknown";

    @Override
    public void startSection(String name) {
        if(!configLoaded) {
            configLoaded = true;
            Config.load();
        }
        if(name.equals("tick")) {
            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventBus.call(new TickEvent());
            }
        }
        if(name.equals("gui")) {
            this.mc.entityRenderer.setupOverlayRendering();
            EventBus.call(new Render2DEvent());
        }
        if(name.equals("weather")) {
            EventBus.call(new Render3DEvent(Yammi.getInstance().getPartialTicks()));
        }
        if(name.equals("gameRenderer")) {
            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventBus.call(new Render3DEvent.Pre(Yammi.getInstance().getPartialTicks()));
            }
        }
        if(name.equalsIgnoreCase("root")) {
            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventBus.call(new UpdateEvent.Pre());
            }
            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventBus.call(new UpdateEvent());
            }
        }
        if(name.equalsIgnoreCase("submit")) {
            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventBus.call(new UpdateEvent.Post());
            }
        }
        lastSection = name;
        super.startSection(name);
    }

    @Override
    public void endSection() {
        if(lastSection.equals("hand")) {
            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                EventBus.call(new Render3DEvent.Post(Yammi.getInstance().getPartialTicks()));
            }
        }
        super.endSection();
    }
}
