package ru.yammi.gui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import ru.yammi.Yammi;
import ru.yammi.gui.font.FontGL11;

public class Notification {

    private NotificationSystem.NotificationType type;

    private String text;
    private int color;
    private long time;
    private boolean back = false;

    private int x = 0;

    public Notification(String textIn, int color, NotificationSystem.NotificationType type){
        this.text = textIn;
        this.color = color;
        this.type = type;
        this.time = System.currentTimeMillis() + 1000L * 3L;

        FontGL11 font = Yammi.getInstance().getTabFont();

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        x = scaledResolution.getScaledWidth() + font.getStringWidth(text) / 2;
    }

    public int getColor() {
        return color;
    }

    public NotificationSystem.NotificationType getType() {
        return type;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
