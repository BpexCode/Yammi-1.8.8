package ru.yammi.module.misc;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.gui.Render2DEvent;
import ru.yammi.gui.YammiScreen;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;
import ru.yammi.utils.ColorUtils;

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUD extends Module {

    private NotificationSystem notificationSystem = new NotificationSystem(4);
    public HUD() {
        super("HUD", Category.Misc, "Ingame cheat hud");
        this.getOptions().add(new Option("Notifications"));
        this.getOptions().add(new Option("Show server info"));
    }

    @EventTarget
    public void onRenderOverlay(Render2DEvent render2DEvent) {
        if(this.getState()){
            String text = "Yammi";

            char[] chars = text.toCharArray();

            int pos = 2;
            for(int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                int color = i == 0 ? ColorUtils.TAB_SEPARATOR_COLOR : Color.WHITE.getRGB();
                String newText = new String(new char[] {ch});
                Yammi.getInstance().getCopyrightFont().drawStringWithShadow(newText, pos, 24, color);
                pos += Yammi.getInstance().getCopyrightFont().getStringWidth(newText);
            }

            Yammi.getInstance().getModuleFont().drawString("b" + Yammi.getInstance().getVersion(), pos + 2, 29, Color.WHITE.getRGB());
            String copyright = "Coded by InvHacks";
            pos = 2;
            Yammi.getInstance().getCopyrightFont().drawStringWithShadow(copyright, pos, 38, -1);

            boolean showServerInfo = this.getOption("Show server info").isBooleanValue();
            if(showServerInfo) {
                if(this.mc.thePlayer != null && this.mc.thePlayer.sendQueue != null && this.mc.thePlayer.sendQueue.getNetworkManager() != null) {
                    if(this.mc.thePlayer.sendQueue.getNetworkManager().getRemoteAddress() instanceof InetSocketAddress) {
                        InetSocketAddress socketAddress = (InetSocketAddress) this.mc.thePlayer.sendQueue.getNetworkManager().getRemoteAddress();
                        if (socketAddress != null) {
                            String serverInfo = "Server: " + socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
                            Yammi.getInstance().getModuleFont().drawStringWithShadow(serverInfo, pos, 54, -1);
                        }
                    }
                }
            }

            List<String> arrlist = new ArrayList<String>();
            for(Module module : Yammi.getInstance().getModules()) {
                //Yammi.getInstance().getModules().stream().forEach(module -> {
                if (module.getState()) {
                    String str = module.getName();
                    for (Option option : module.getOptions()) {
                        if (option.getMode() != null) {
                            str += " \u00a77" + option.getMode().getName();
                        }
                    }
                    arrlist.add(str);
                }
                //});
            }

            arrlist.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Yammi.getInstance().getModuleFont().getStringWidth(o2) - Yammi.getInstance().getModuleFont().getStringWidth(o1);
                }
            });

            int index = arrlist.size();
            int lastLen = 0;
            int lastSize = 0;
            int lastY = 0;

            ScaledResolution sr = new ScaledResolution(this.mc);
            for (int j = 0; j < arrlist.size(); ++j) {
                final String var4 = arrlist.get(j);
                if (!Strings.isNullOrEmpty(var4)) {
                    int var5 = Yammi.getInstance().getModuleFont().getStringHeight(var4);
                    int var6 = Yammi.getInstance().getModuleFont().getStringWidth(var4);
                    int var7 = sr.getScaledWidth() - 3 - var6;
                    int var8 = (var5 + 2) * j + 1;
                    int rainbow = this.rainbow(index + index * 200000000L, 1.0f);
                    if (j == 0) {
                        Gui.drawRect(var7 - 2, var8 - 1, var7 + Yammi.getInstance().getModuleFont().getStringWidth(var4) + 2, var8, rainbow);
                    }

                    Gui.drawRect(var7 - 2, var8, var7 + Yammi.getInstance().getModuleFont().getStringWidth(var4) + 2, var8 + 11, 1140850688);
                    Gui.drawRect(sr.getScaledWidth() - 1, var8 + 11, sr.getScaledWidth(), var8 - 1, rainbow);
                    Gui.drawRect(var7 - 2, var8 - 1, var7 - 1, var8 + 11, rainbow);
                    Gui.drawRect(lastLen, lastY, lastLen + (lastSize - Yammi.getInstance().getModuleFont().getStringWidth(var4)), lastY + 1, rainbow);

                    lastSize = Yammi.getInstance().getModuleFont().getStringWidth(var4);
                    lastLen = var7 - 2;
                    lastY = var8 + 10;
                    //Gui.drawRect(var7 + Yammi.getInstance().getModuleFont().getStringWidth(var4), var8 - 1, var7 + Yammi.getInstance().getModuleFont().getStringWidth(var4) + 1, var8 + 11, rainbow);
                    if (j == arrlist.size() - 1) {
                        Gui.drawRect(var7 - 2, var8 + 10, var7 + Yammi.getInstance().getModuleFont().getStringWidth(var4) + 2, var8 + 11, rainbow);
                    }

                    int rva = 0;
                    //int rva = Yammi.getInstance().isVimeworld() ? 2 : 0;
                    Yammi.getInstance().getModuleFont().drawStringWithShadow(var4, var7, var8 + 1 + rva, rainbow);
                    --index;
                }
            }
            arrlist.clear();

            if(this.getOption("Notifications").isBooleanValue())
                notificationSystem.drawIngame();
        }
    }

    @Override
    public void onDisable() {
        notificationSystem.initGui();
    }

    @Override
    public void onEnable() {
        notificationSystem.initGui();
    }

    public NotificationSystem getNotificationSystem() {
        return notificationSystem;
    }

    private int rainbow(long offset, float fade) {
        final float hue = (System.nanoTime() + offset) / 5.0E9f % 1.0f; //5.0E9
        final long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0f, 1.0f))), 16);
        final Color c = new Color((int) color);
        return new Color(c.getRed() / 255.0f * fade, c.getGreen() / 255.0f * fade, c.getBlue() / 255.0f * fade,
                c.getAlpha() / 255.0f).getRGB();
    }

}
