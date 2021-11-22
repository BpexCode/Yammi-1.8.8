package ru.yammi.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.event.EventBus;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.gui.GuiCloseEvent;
import ru.yammi.event.events.gui.GuiDrawEvent;
import ru.yammi.event.events.gui.GuiOpenEvent;
import ru.yammi.event.events.packet.PacketReadEvent;
import ru.yammi.event.events.packet.PacketSendEvent;
import ru.yammi.gui.animation.OpenScreenAnimation;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.Module;
import ru.yammi.module.misc.HUD;

import java.io.File;
import java.io.FileOutputStream;

public class EventUtils {

    private KeyboardUtils keyboardUtils = new KeyboardUtils(250L);
    private Minecraft mc = Minecraft.getMinecraft();

    private ShaderUtils shaderUtils = new ShaderUtils();
    private OpenScreenAnimation screenAnimation = new OpenScreenAnimation();

    private boolean shaderInventoryState = false;
    private NetHandlerPlayClient lastHandler = null;

    @EventTarget
    public void onDrawGui(GuiDrawEvent event) {
    }

    @EventTarget
    public void onOpenGui(GuiOpenEvent event) {
    }

    @EventTarget
    public void onCloseGui(GuiCloseEvent event) {
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {

            if (this.mc != null && this.mc.thePlayer != null && this.mc.theWorld != null) {
                NetHandlerPlayClient clientHandler = this.mc.thePlayer.sendQueue;
                if (clientHandler != null) {
                    if(lastHandler == null || lastHandler != clientHandler) {
                        lastHandler = clientHandler;
                        try {
                            Channel channel = (Channel) Reflection.getField(NetworkManager.class, "channel", "field_150746_k", "k").get(clientHandler.getNetworkManager());
                            channel.pipeline().addBefore("packet_handler", "xtrafrancyz", new ChannelDuplexHandler() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
                                    Packet packet = (Packet) in;
                                    PacketReadEvent packetReadEvent = new PacketReadEvent(packet);
                                    EventBus.call(packetReadEvent);
                                    if (!packetReadEvent.isCancel()) {
                                        super.channelRead(ctx, in);
                                    }
                                }

                                @Override
                                public void write(ChannelHandlerContext ctx, Object out, ChannelPromise pr) throws Exception {
                                    Packet packet = (Packet) out;
                                    PacketSendEvent packetSendEvent = new PacketSendEvent(packet);

                                    EventBus.call(packetSendEvent);
                                    if (!packetSendEvent.isCancel()) {
                                        super.write(ctx, out, pr);
                                    }
                                    if (packetSendEvent.newPacket != null) {
                                        super.write(ctx, packetSendEvent.newPacket, pr);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

        }
        if(this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiInventory) {
            if(!shaderInventoryState) {
                shaderInventoryState = true;
                shaderUtils.enable();
            }
            shaderUtils.onRender();
        }
        if(this.mc.currentScreen == null) {
            if(shaderInventoryState) {
                shaderInventoryState = false;
                shaderUtils.disable();
            }
            for (Module module : Yammi.getInstance().getModules()) {
                if (keyboardUtils.isKeyDown(module.getKeybind())) {
                    module.setState(!module.getState());
                    boolean state = module.getState();
                    if (state)
                        module.onEnable();
                    else
                        module.onDisable();

                    String notificationText = module.getName() + (state ? " \u00a7a[Enabled]" : " \u00a77[Disabled]");

                    NotificationSystem.NotificationType type = state ? NotificationSystem.NotificationType.OK : NotificationSystem.NotificationType.CANCEL;
                    Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(notificationText, type);
                }
            }
        }
    }

}
