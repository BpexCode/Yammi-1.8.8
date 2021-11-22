package ru.yammi.gui.elements;

import net.minecraft.client.Minecraft;

public interface IElement {
    public void draw(int posX, int posY, float partialTicks);
    public void mouseReleased(final int xD, final int yD, final int state);
    public void mouseClicked(final int xD, final int yD, final int mouse);
    public void mouseClickMove(final int xD, final int yD, final int mouse, final long time);
    public void keyTyped(int key);

    default boolean shouldRenderNextElement(){return true;}
}
