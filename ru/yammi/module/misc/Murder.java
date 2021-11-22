package ru.yammi.module.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemMap;
import net.minecraft.network.play.client.C01PacketChatMessage;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.TimerUtils;

public class Murder extends Module {

    private TimerUtils timerUtils = new TimerUtils(15000L);

    public Murder(){
        super("Murder", Category.Misc, "Zalupa");
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if(this.getState()) {
            for (Object o : mc.theWorld.loadedEntityList) {
                if (o instanceof EntityPlayer) {
                    EntityPlayer ent = (EntityPlayer) o;
                    if (ent != mc.thePlayer && ent.getCurrentEquippedItem() != null && isMurder(ent.getCurrentEquippedItem().getItem())) {
                        if(timerUtils.isTimeReached()) {
                            this.mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(ent.getName() + " is murderer!"));
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean isMurder(Item item){
        if(item instanceof ItemMap || item.getUnlocalizedName().equalsIgnoreCase("item.ingotGold") ||
                item instanceof ItemBow || item.getUnlocalizedName().equalsIgnoreCase("item.arrow") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.potion") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.paper") ||
                item.getUnlocalizedName().equalsIgnoreCase("tile.tnt") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.web") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.bed") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.compass") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.comparator") ||
                item.getUnlocalizedName().equalsIgnoreCase("item.shovelWood")){
            return false;
        }
        return true;
    }

}
