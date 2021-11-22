package ru.yammi.event.events.gui;

import net.minecraft.client.gui.GuiScreen;
import ru.yammi.event.Event;

public class GuiDrawEvent extends Event {

    public GuiScreen guiScreen;
    public GuiDrawState state;

    public GuiDrawEvent(GuiScreen guiScreen, GuiDrawState state) {
        this.guiScreen = guiScreen;
        this.state = state;
    }

    public static enum GuiDrawState {
        PRE,
        POST
    }
}
