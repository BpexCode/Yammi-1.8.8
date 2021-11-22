package ru.yammi.event.events.gui;

import net.minecraft.client.gui.GuiScreen;
import ru.yammi.event.Event;

public class GuiOpenEvent extends Event {

    public GuiScreen screen;

    public GuiOpenEvent(GuiScreen screen) {
        this.screen = screen;
    }
}
