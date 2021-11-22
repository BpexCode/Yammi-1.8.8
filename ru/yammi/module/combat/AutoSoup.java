package ru.yammi.module.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.TickEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.option.Option;

public class AutoSoup extends Module {

    private int oldSlot = -1;

    public AutoSoup() {
        super("AutoSoup", Category.Combat, "Auto use soup from inventory");
        this.getOptions().add(new Option("Health", 6.5F, 9.5F));
    }

    @EventTarget
    public void onUpdate(TickEvent updateEvent){
        if(this.getState()) {
            for(int i = 0; i < 36; i++)
            {
                ItemStack stack =
                        this.mc.thePlayer.inventory.getStackInSlot(i);
                if(stack == null || stack.getItem() != Items.bowl || i == 9)
                    continue;

                ItemStack emptyBowlStack =
                        this.mc.thePlayer.inventory.getStackInSlot(9);
                boolean swap = !isEmptySlot(emptyBowlStack)
                        && emptyBowlStack.getItem() != Items.bowl;

                mc.playerController.windowClick(0, i < 9 ? 36 + i : i, 0,
                        0, this.mc.thePlayer);
                mc.playerController.windowClick(0, 9, 0, 0,
                        this.mc.thePlayer);

                if(swap)
                    mc.playerController.windowClick(0, i < 9 ? 36 + i : i, 0,
                            0, this.mc.thePlayer);
            }

            int soupInHotbar = findSoup(0, 9);

            if(soupInHotbar != -1)
            {
                if(!shouldEatSoup())
                {
                    stopIfEating();
                    return;
                }

                if(oldSlot == -1)
                    oldSlot = this.mc.thePlayer.inventory.currentItem;

                this.mc.thePlayer.inventory.currentItem = soupInHotbar;

                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                processRightClick();

                return;
            }

            stopIfEating();

            int soupInInventory = findSoup(9, 36);

            if(soupInInventory != -1)
                mc.playerController.windowClick(0, soupInInventory, 0,
                        1, this.mc.thePlayer);
        }
    }

    public void processRightClick()
    {
        this.mc.playerController.sendUseItem(this.mc.thePlayer,
                this.mc.theWorld,
                this.mc.thePlayer.getCurrentEquippedItem());
    }

    private boolean isEmptySlot(ItemStack slot)
    {
        return slot == null;
    }

    private int findSoup(int startSlot, int endSlot)
    {
        for(int i = startSlot; i < endSlot; i++)
        {
            ItemStack stack =
                    this.mc.thePlayer.inventory.getStackInSlot(i);

            if(stack != null && stack.getItem() instanceof ItemSoup)
                return i;
        }

        return -1;
    }

    private boolean shouldEatSoup()
    {
        if(this.mc.thePlayer.getHealth() > this.getOption("Health").getFloatValue() * 2F)
            return false;

        if(mc.currentScreen == null && mc.objectMouseOver != null)
        {
            Entity entity = mc.objectMouseOver.entityHit;
            if(entity instanceof EntityVillager
                    || entity instanceof EntityTameable)
                return false;

            if(mc.objectMouseOver.getBlockPos() != null && getBlock(
                    mc.objectMouseOver.getBlockPos()) instanceof BlockContainer)
                return false;
        }

        return true;
    }

    public Block getBlock(BlockPos pos)
    {
        return getState(pos).getBlock();
    }

    public IBlockState getState(BlockPos pos)
    {
        return this.mc.theWorld.getBlockState(pos);
    }

    private void stopIfEating()
    {
        if(oldSlot == -1)
            return;

        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindUseItem.getKeyCode(), false);

        this.mc.thePlayer.inventory.currentItem = oldSlot;
        oldSlot = -1;
    }

}
