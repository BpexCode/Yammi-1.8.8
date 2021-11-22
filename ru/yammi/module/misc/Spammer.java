package ru.yammi.module.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.TimerUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Spammer extends Module {

    private TimerUtils timerUtils = new TimerUtils(800L);
    private List<EntityPlayer> spam = new CopyOnWriteArrayList<>();

    public Spammer() {
        super("Spammer", Category.Misc, "Zalupa");
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if(this.getState()) {
            if(spam.size() == 0) {
                spam.addAll(this.mc.theWorld.playerEntities);
            }
            if(timerUtils.isTimeReached()) {
                EntityPlayer entityPlayer = spam.get(0);
                if(entityPlayer == this.mc.thePlayer) {
                    spam.remove(0);
                } else {
                    if (entityPlayer != null) {
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("/m " + entityPlayer.getName() + " \u041b\u0443\u0447\u0448\u0438\u0439 \u0447\u0438\u0442 \u043d\u0430 \u0056\u0069\u006d\u0065\u0057\u006f\u0072\u006c\u0064 \u002d \u0068\u0074\u0074\u0070\u0073\u003a\u002f\u002f\u0069\u006e\u0076\u0068\u0061\u0063\u006b\u0073\u002e\u0072\u0075"));
                        spam.remove(0);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        spam.clear();
    }

    @Override
    public void onEnable() {
        spam.clear();
        spam.addAll(this.mc.theWorld.playerEntities);
    }
}
