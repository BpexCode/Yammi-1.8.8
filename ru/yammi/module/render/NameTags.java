package ru.yammi.module.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.misc.ClickGUI;
import ru.yammi.module.option.Option;
import ru.yammi.utils.R2DUtils;

import java.util.List;

public class NameTags extends Module {

    public NameTags(){
        super("NameTags", Category.Render, "Advanced name tags");
        this.getOptions().add(new Option("Health"));
        this.getOptions().add(new Option("Items"));
    }

    @EventTarget
    public void onRender3D(Render3DEvent render3DEvent) {
        if(this.getState()) {
            final List<EntityPlayer> list = this.mc.theWorld.playerEntities;
            for (final EntityPlayer player : list) {
                if (player != null && player != this.mc.thePlayer) {
                    try {
                        float partialTicks = Yammi.getInstance().getPartialTicks();
                        double renderPosX = TileEntityRendererDispatcher.staticPlayerX;
                        double renderPosY = TileEntityRendererDispatcher.staticPlayerY;
                        double renderPosZ = TileEntityRendererDispatcher.staticPlayerZ;
                        final double x = ((EntityPlayer) player).lastTickPosX + (((EntityPlayer) player).posX - ((EntityPlayer) player).lastTickPosX) * partialTicks - renderPosX;
                        final double y = ((EntityPlayer) player).lastTickPosY + (((EntityPlayer) player).posY - ((EntityPlayer) player).lastTickPosY) * partialTicks - renderPosY;
                        final double z = ((EntityPlayer) player).lastTickPosZ + (((EntityPlayer) player).posZ - ((EntityPlayer) player).lastTickPosZ) * partialTicks - renderPosZ;
                        this.renderNametag((EntityPlayer) player, x, y, z);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }

    public void renderNametag(final EntityPlayer player, final double x, final double y, final double z) {
        String playerName = this.getPlayerName(player);
        if(playerName.startsWith("-")) return;

        final double size = this.getSize(player) * -0.0225;
        final FontRenderer var13 = this.mc.fontRendererObj;
        GL11.glPushMatrix();

        boolean health = this.getOption("Health").isBooleanValue();
        boolean armor = this.getOption("Items").isBooleanValue();

        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glTranslated((double)(float)x, (float)y + player.height + 0.5, (double)(float)z);
        GL11.glRotatef(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScaled(size, size, size);
        final int var16 = health ? (var13.getStringWidth(String.valueOf(String.valueOf(this.getPlayerName(player))) + " " + this.getHealth(player)) / 2) : (var13.getStringWidth(this.getPlayerName(player)) / 2);
        final int bordercolor = 1879048192;
        final int maincolor = 1879048192;
        R2DUtils.drawBorderedRect(-var16 - 2, -(this.mc.fontRendererObj.FONT_HEIGHT - 6), var16 + 2, (float)(this.mc.fontRendererObj.FONT_HEIGHT + 0.5), 1.0f, -1879048192, bordercolor);
        GL11.glDisable(2929);

        if (!health) {
            var13.drawStringWithShadow(this.getPlayerName(player), -var13.getStringWidth(playerName) / 2, 0.0f, 15790320);
        }
        else if (health) {
            var13.drawStringWithShadow(this.getPlayerName(player), -var13.getStringWidth(playerName + " " + this.getHealth(player)) / 2, 0.0f, 15790320);
            var13.drawStringWithShadow(this.getHealth(player), (var13.getStringWidth(playerName + " " + this.getHealth(player)) - var13.getStringWidth(this.getHealth(player)) * 2) / 2, 0.0f, this.getHealthColorHEX(player));
        }
        if(armor) {
            this.renderArmor(player);
        }
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void renderArmor(final EntityPlayer player) {
        int xOffset = 0;
        ItemStack[] arrayOfItemStack1;
        for (int j = (arrayOfItemStack1 = player.inventory.armorInventory).length, i = 0; i < j; ++i) {
            final ItemStack armourStack = arrayOfItemStack1[i];
            if (armourStack != null) {
                xOffset -= 8;
            }
        }
        if (player.getHeldItem() != null) {
            xOffset -= 8;
            final ItemStack stock = player.getHeldItem().copy();
            if (stock.hasEffect() && (stock.getItem() instanceof ItemTool || stock.getItem() instanceof ItemArmor)) {
                stock.stackSize = 1;
            }
            renderItemStack(stock, xOffset, -19);
            xOffset += 16;
        }
        final ItemStack[] renderStack = player.inventory.armorInventory;
        for (int index = 3; index >= 0; --index) {
            final ItemStack armourStack2 = renderStack[index];
            if (armourStack2 != null) {
                final ItemStack renderStack2 = armourStack2;
                renderItemStack(renderStack2, xOffset, -19);
                xOffset += 16;
            }
        }
    }

    public void renderItemStack(final ItemStack stack, final int x, final int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        //GlStateManager.clear(256);
        this.mc.getRenderItem().zLevel = -150.0f;

        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();

        this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack, x, y);
        this.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();

        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();

        drawitemStackEnchants(stack, x * 2, y * 2);

        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);

        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void drawitemStackEnchants(final ItemStack stak, final int x, final int y) {
        final NBTTagList enchants = stak.getEnchantmentTagList();
        if (enchants != null) {
            int ency = 0;
            for (int index = 0; index < enchants.tagCount(); ++index) {
                final short id = enchants.getCompoundTagAt(index).getShort("id");
                final short level = enchants.getCompoundTagAt(index).getShort("lvl");
                final Enchantment enc = Enchantment.getEnchantmentById(id);
                if (enc != null)
                {
                    final String encName = enc.getTranslatedName(level).substring(0, 2).toLowerCase();
                    final String[] ShownEnchants = { "Efficiency", "Unbreaking", "Sharpness", "FireAspect", "" };
                    this.mc.fontRendererObj.drawStringWithShadow(String.valueOf(String.valueOf(encName)) + "\u00a7b" + level, x, y + ency,
                            -5592406);
                    ency += this.mc.fontRendererObj.FONT_HEIGHT;
                    if (index > 4) {
                        this.mc.fontRendererObj.drawStringWithShadow("\u00a7f+ others", x, y + ency, -5592406);
                        break;
                    }
                }
            }
        }
    }

    private int getHealthColorHEX(final EntityPlayer e) {
        final int health = Math.round(20.0f * (e.getHealth() / e.getMaxHealth()));
        int color = -1;
        if (health >= 20) {
            color = 5030935;
        }
        else if (health >= 18) {
            color = 9108247;
        }
        else if (health >= 16) {
            color = 10026904;
        }
        else if (health >= 14) {
            color = 12844472;
        }
        else if (health >= 12) {
            color = 16633879;
        }
        else if (health >= 10) {
            color = 15313687;
        }
        else if (health >= 8) {
            color = 16285719;
        }
        else if (health >= 6) {
            color = 16286040;
        }
        else if (health >= 4) {
            color = 15031100;
        }
        else if (health >= 2) {
            color = 16711680;
        }
        else if (health >= 0) {
            color = 16190746;
        }
        return color;
    }

    private String getHealth(final EntityPlayer e) {
        String hp = "";
        final double abs = 2.0f * (e.getAbsorptionAmount() / 4.0f);
        double health = (10.0 + abs) * (e.getHealth() / e.getMaxHealth());
        health = Double.valueOf(Math.abs(health));
        final int ihealth = (int)health;
        if (health % 1.0 != 0.0) {
            hp = String.valueOf(health);
        }
        else {
            hp = String.valueOf(ihealth);
        }

        float tmpFloat = Math.abs((float)health);
        String f0 = String.format("%.01f", tmpFloat);
        f0 = f0.replace(",", ".");
        return f0;
    }

    private String getPlayerName(final EntityPlayer player) {
        String name = "";
        name = player.getDisplayName().getFormattedText();
        return name;
    }

    private float getSize(final EntityPlayer player) {
        final Entity ent = this.mc.thePlayer;
        final boolean angle = isFacingAtEntity(player, 22.0);
        final float dist = ent.getDistanceToEntity(player) / 6.0f;
        final float size = (dist <= 2.0f) ? 1.3f : dist;
        return size;
    }

    public boolean isFacingAtEntity(final Entity cunt, double angleHowClose) {
        final Entity ent = this.mc.thePlayer;
        final float[] yawPitch = getYawAndPitch(cunt);
        angleHowClose /= 4.5;
        final float yaw = yawPitch[0];
        final float pitch = yawPitch[1];
        return AngleDistance(ent.rotationYaw, yaw) < angleHowClose && AngleDistance(ent.rotationPitch, pitch) < angleHowClose;
    }

    public float[] getYawAndPitch(final Entity target) {
        final Entity ent = this.mc.thePlayer;
        final double x = target.posX - ent.posX;
        final double z = target.posZ - ent.posZ;
        final double y = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - this.mc.thePlayer.posY;
        final double helper = MathHelper.sqrt_double(x * x + z * z);
        final float newYaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float newPitch = (float)(Math.atan2(y * 1.0, helper) * 180.0 / 3.141592653589793);
        return new float[] { newYaw, newPitch };
    }

    private float AngleDistance(final float par1, final float par2) {
        float angle = Math.abs(par1 - par2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }

}
