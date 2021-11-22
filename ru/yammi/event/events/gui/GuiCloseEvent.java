package ru.yammi.event.events.gui;

import net.minecraft.client.gui.GuiScreen;
import ru.yammi.event.Event;

public class GuiCloseEvent extends Event {

    public GuiScreen screen;

    public GuiCloseEvent(GuiScreen screen) {
        this.screen = screen;
    }
}
