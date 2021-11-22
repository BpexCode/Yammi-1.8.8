package ru.yammi.module.combat;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;

public class AutoArmor extends Module {

    private int[] bestArmor = new int[4];

    public AutoArmor(){
        super("AutoArmor", Category.Combat, "Auto equip best armor from inventory");
    }

    @EventTarget
    public void onPostUpdate(TickEvent event) {
        if(this.getState()){
            if (this.mc.thePlayer.capabilities.isCreativeMode || this.mc.currentScreen instanceof GuiContainer && !(this.mc.currentScreen instanceof GuiInventory)) {
                return;
            }

            this.bestArmor = new int[4];

            for(int i = 0; i < this.bestArmor.length; ++i) {
                this.bestArmor[i] = -1;
            }

            ItemArmor bestArmor;
            ItemStack itemstack;
            for(int i = 0; i < 36; ++i) {
                itemstack = this.mc.thePlayer.inventory.getStackInSlot(i);
                if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
                    bestArmor = (ItemArmor)itemstack.getItem();
                    if (bestArmor.damageReduceAmount > this.bestArmor[3 - bestArmor.armorType]) {
                        this.bestArmor[3 - bestArmor.armorType] = i;
                    }
                }
            }

            for(int i = 0; i < 4; ++i) {
                itemstack = this.mc.thePlayer.inventory.armorItemInSlot(i);
                ItemArmor currentArmor = itemstack != null && itemstack.getItem() instanceof ItemArmor ? (ItemArmor)itemstack.getItem() : null;

                try {
                    bestArmor = (ItemArmor)this.mc.thePlayer.inventory.getStackInSlot(this.bestArmor[i]).getItem();
                } catch (Exception var18) {
                    bestArmor = null;
                }

                if (bestArmor != null && (currentArmor == null || bestArmor.damageReduceAmount > currentArmor.damageReduceAmount) && (this.mc.thePlayer.inventory.getFirstEmptyStack() != -1 || currentArmor == null)) {
                    this.mc.playerController.windowClick(0, 8 - i, 0, 1, this.mc.thePlayer);
                    this.mc.playerController.windowClick(0, this.bestArmor[i] < 9 ? 36 + this.bestArmor[i] : this.bestArmor[i], 0, 1, this.mc.thePlayer);
                }
            }
        }
    }

}
