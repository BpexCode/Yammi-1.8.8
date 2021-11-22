package ru.yammi.module.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.StringUtils;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.event.events.UpdateEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.combat.AutoArmor;
import ru.yammi.module.option.Option;

import java.util.List;

public class AutoLeave extends Module {

    public AutoLeave(){
        super("AutoLeave", Category.Misc, "Go to the hub when a any player comes in");
        this.getOptions().add(new Option("Radius", 0F, 200F));
    }

    @EventTarget
    public void onUpdate(UpdateEvent updateEvent) {
        if(this.getState()) {
            List<EntityPlayer> players = this.mc.theWorld.playerEntities;
            if(players != null) {
                float distance = this.getOption("Radius").getFloatValue();
                for (EntityPlayer player : players) {
                    if(player != this.mc.thePlayer) {
                        String name = player.getName();
                        int ct = 0;
                        for (String s :Yammi.getInstance().getFriendList()) {
                            if (name.equals(s)) {
                                ct++;
                            }
                        }
                        if (ct == players.size()) {
                            break;
                        }
                        if (this.mc.thePlayer.getDistanceToEntity(player) <= distance) {
                            this.mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("/hub"));
                            this.setState(false);
                            Config.store();
                            break;
                        }
                    }
                }
            }
        }
    }

}
