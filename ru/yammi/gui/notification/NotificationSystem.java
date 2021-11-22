package ru.yammi.gui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.PositionUtils;
import ru.yammi.utils.R2DUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationSystem {

    private List<Notification> notifications = new CopyOnWriteArrayList<Notification>();
    private int maxNotifications;

    private int yOffset = 0;

    private static Minecraft mc = Minecraft.getMinecraft();
    private static ResourceLocation okImageLocation;
    private static ResourceLocation cancelImageLocation;

    public NotificationSystem(int maxNotificationsIn){
        this.maxNotifications = maxNotificationsIn;
    }

    public void initGui(){
        notifications.clear();
    }

    public void draw(){
        FontGL11 font = Yammi.getInstance().getNotificationsFont();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        long currentTime = System.currentTimeMillis();

        int idx = 0;
        int y = scaledResolution.getScaledHeight() - 20;

        if(yOffset < 0)
            yOffset = 0;
        if(yOffset != 0)
            yOffset--;

        for(Notification notification : notifications) {
            String text = notification.getText();

            boolean removed = false;
            int xOffset = notification.getType() == NotificationType.DEFAULT ? 0 : 15;

            if(!notification.isBack()) {
                int neededX = scaledResolution.getScaledWidth() - 10 - xOffset - font.getStringWidth(text);
                if(notification.getX() != neededX) {
                    notification.setX(notification.getX() - 1);
                }

                long notificationTime = notification.getTime();
                if (currentTime >= notificationTime) {
                    notification.setBack(true);
                }
            } else {
                int neededX = scaledResolution.getScaledWidth() + font.getStringWidth(text) / 2;
                if(notification.getX() != neededX) {
                    notification.setX(notification.getX() + 1);
                } else {
                    notifications.remove(notification);
                    removed = true;
                }
            }

            int x = notification.getX();
            int yPos = y - idx * 19;

            //R2DUtils.drawRect(x - 1, yPos - 4 - yOffset / 2, x + font.getStringWidth(text) + 1, yPos + 17 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);

            R2DUtils.drawRect(x - 7, yPos - yOffset / 2, x - 5, yPos + 10 - yOffset / 2, ColorUtils.TAB_SEPARATOR_COLOR);

            R2DUtils.enableGL2D();
            R2DUtils.drawFullCircle(x - 3, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_SEPARATOR_COLOR, true);
            R2DUtils.drawFullCircle(x - 3, yPos + 10 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_SEPARATOR_COLOR, true);
            R2DUtils.disableGL2D();

            R2DUtils.drawRect(x - 1, yPos - 4 - yOffset / 2, x + font.getStringWidth(text) + 1 + xOffset, yPos - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);
            R2DUtils.drawRect(x - 5, yPos - yOffset / 2, x + font.getStringWidth(text) + 5 + xOffset, yPos + 10 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);
            R2DUtils.drawRect(x - 1, yPos + 10 - yOffset / 2, x + font.getStringWidth(text) + 1 + xOffset, yPos + 14 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);

            R2DUtils.enableGL2D();
            R2DUtils.drawFullCircle(x - 1, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.drawFullCircle(x + font.getStringWidth(text) + 5 + xOffset - PositionUtils.CIRCLE_RADIUS, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.drawFullCircle(x - 1, yPos + 10 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.drawFullCircle(x + font.getStringWidth(text) + 5 + xOffset - PositionUtils.CIRCLE_RADIUS, yPos + 10 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.disableGL2D();

            switch (notification.getType()) {
                case OK: {
                    drawImage(okImageLocation, x - 5, yPos - 5 - yOffset / 2);
                    break;
                }
                case CANCEL: {
                    drawImage(cancelImageLocation, x - 5, yPos - 5 - yOffset / 2);
                    break;
                }
            }
            font.drawString(notification.getText(), x + xOffset, yPos + 1 - yOffset / 2, notification.getColor());

            if(removed)
                yOffset = 44;
            idx++;
        }
    }

    public void drawIngame() {
        FontGL11 font = Yammi.getInstance().getNotificationsFont();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        long currentTime = System.currentTimeMillis();

        int idx = 0;
        int y = scaledResolution.getScaledHeight() - 20;

        if(yOffset != 0)
            yOffset--;

        for(Notification notification : notifications) {
            String text = notification.getText();

            boolean removed = false;
            int xOffset = notification.getType() == NotificationType.DEFAULT ? 0 : 15;

            if(!notification.isBack()) {
                int neededX = scaledResolution.getScaledWidth() - 10 - xOffset - font.getStringWidth(text);
                if(notification.getX() != neededX) {
                    notification.setX(notification.getX() - 1);
                }

                long notificationTime = notification.getTime();
                if (currentTime >= notificationTime) {
                    notification.setBack(true);
                }
            } else {
                int neededX = scaledResolution.getScaledWidth() + font.getStringWidth(text) / 2;
                if(notification.getX() != neededX) {
                    notification.setX(notification.getX() + 1);
                } else {
                    notifications.remove(notification);
                    removed = true;
                }
            }

            int x = notification.getX();
            int yPos = y - idx * 19;

            //R2DUtils.drawRect(x - 1, yPos - 4 - yOffset / 2, x + font.getStringWidth(text) + 1, yPos + 17 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);

            Gui.drawRect(x - 7, yPos - yOffset / 2, x - 5, yPos + 10 - yOffset / 2, ColorUtils.TAB_SEPARATOR_COLOR);

            R2DUtils.drawFullCircleIngame(x - 3, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_SEPARATOR_COLOR, true);
            R2DUtils.drawFullCircleIngame(x - 3, yPos + 10 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_SEPARATOR_COLOR, true);

            Gui.drawRect(x - 1, yPos - 4 - yOffset / 2, x + font.getStringWidth(text) + 1 + xOffset, yPos - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);
            Gui.drawRect(x - 5, yPos - yOffset / 2, x + font.getStringWidth(text) + 5 + xOffset, yPos + 10 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);
            Gui.drawRect(x - 1, yPos + 10 - yOffset / 2, x + font.getStringWidth(text) + 1 + xOffset, yPos + 14 - yOffset / 2, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR);

            R2DUtils.drawFullCircleIngame(x - 1, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.drawFullCircleIngame(x + font.getStringWidth(text) + 5 + xOffset - PositionUtils.CIRCLE_RADIUS, yPos - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.drawFullCircleIngame(x - 1, yPos + 10 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);
            R2DUtils.drawFullCircleIngame(x + font.getStringWidth(text) + 5 + xOffset - PositionUtils.CIRCLE_RADIUS, yPos + 10 - yOffset / 2, PositionUtils.CIRCLE_RADIUS, ColorUtils.MODULE_BACKGROUND_CLICKED_COLOR, true);

            switch (notification.getType()) {
                case OK: {
                    drawImage(okImageLocation, x - 5, yPos - 5 - yOffset / 2);
                    break;
                }
                case CANCEL: {
                    drawImage(cancelImageLocation, x - 5, yPos - 5 - yOffset / 2);
                    break;
                }
            }
            font.drawString(notification.getText(), x + xOffset, yPos + 1 - yOffset / 2, notification.getColor());

            if(removed)
                yOffset = 44;
            idx++;
        }
    }

    public void addNotification(String text, NotificationType type) {
        addNotification(text, -1, type);
    }

    public void addNotification(String text, int color, NotificationType type) {
        this.notifications.add(new Notification(text, color, type));
    }

    private void drawImage(ResourceLocation resourceLocation, int x, int y){
        GL11.glPushMatrix();

        this.mc.getTextureManager().bindTexture(resourceLocation);
        this.mc.getTextureManager().getTexture(resourceLocation).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x + 5, y + 5, 0.0f, 0.0f ,10, 10, 10, 10);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GL11.glPopMatrix();
    }

    public static enum NotificationType {
        OK,
        CANCEL,
        DEFAULT
    }

    static {
        okImageLocation = new ResourceLocation("minecraft", "okImage");
        cancelImageLocation = new ResourceLocation("minecraft", "cancelImage");
    }

}
