package ru.yammi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import ru.yammi.Yammi;

import java.util.ArrayList;

public class TeamUtils {

    private static int[] colorCodes;

    public static ArrayList<EntityLivingBase> getClosestEntities(float range) {
        ArrayList<EntityLivingBase> entities = new ArrayList<EntityLivingBase>();
        for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (isNotItem(o) && !(o instanceof EntityPlayerSP)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (!validEntity(en)) {
                    continue;
                }
                if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en) < range) {
                    entities.add(en);
                }
            }
        }
        return entities;
    }

    public static boolean validEntity(EntityLivingBase en) {
        if (en == null)
            return false;
        if (en.isEntityEqual(Minecraft.getMinecraft().thePlayer)) {
            return false;
        }
        if (en.isDead) {
            return false;
        }
        if (en.getHealth() <= 0) {
            return false;
        }
        if (!(en instanceof EntityLivingBase)) {
            return false;
        }
        if (en instanceof EntityPlayer
                && Yammi.getInstance().getFriendList().contains(StringUtils.stripControlCodes(en.getDisplayName().getUnformattedTextForChat()))) {
            return false;
        }
        if (en instanceof EntityPlayer && IsInTheSameTeam((EntityPlayer) en)) {
            return false;
        }
        if (en instanceof EntityHorse || en instanceof EntityVillager)
            return false;
        if(en instanceof EntityLivingBase)
            return true;

        return true;
    }

    public static boolean IsInTheSameTeam(EntityPlayer pl) {
        if (Yammi.getInstance().isModuleEnabled("TeamPlay"))
        {
            int GreenColor = GetColorCode((char) 167 + "a" + "2281337");

            NetworkPlayerInfo PInfo = Minecraft.getMinecraft().thePlayer.sendQueue
                    .getPlayerInfo(StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText()));
            if (PInfo != null) {
                int CurrentTeamColor = GetColorCode(
                        Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(PInfo).replace((char) 167 + "r", ""));
                if (pl.getCustomNameTag() != null && pl.getCustomNameTag().length() > 2) {
                    String EnemyName = pl.getCustomNameTag();
                    if (EnemyName.contains("<") && EnemyName.contains(">"))
                    {
                        EnemyName = EnemyName.substring(EnemyName.indexOf(">") + 2);
                    }
                    //addChatMessage(EnemyName, true);

                    int EntityTeamColor = GetColorCode(EnemyName);

                    if (EnemyName.contains("[") && EnemyName.contains("]")
                            && EntityTeamColor == GreenColor) {
                        return true;
                    }
                    if (CurrentTeamColor != 0)
                        return CurrentTeamColor == EntityTeamColor;
                }
            }
        }
        return false;
    }

    private static int GetColorCode(String PlayerName) {
        int j = 0;
        int k = 0;

        char FirstCharacter = PlayerName.charAt(0);

        if (FirstCharacter == 167) {
            j = "0123456789abcdefklmnor".indexOf(PlayerName.toLowerCase().charAt(1));

            if (j < 16) {
                if (j < 0 || j > 15) {
                    j = 15;
                }

                k = colorCodes[j];
            }
        } else
            k = 0;
        return k;
    }

    public static boolean isNotItem(Object o) {
        if (!(o instanceof EntityLivingBase)) {
            return false;
        }
        return true;
    }

    static {
        int j = 0;
        int k = 0;
        colorCodes = new int[32];
        for (int c0 = 0; c0 < 32; ++c0) {
            j = (c0 >> 3 & 0x1) * 85;
            k = (c0 >> 2 & 0x1) * 170 + j;
            int l = (c0 >> 1 & 0x1) * 170 + j;
            int i2 = (c0 >> 0 & 0x1) * 170 + j;
            if (c0 == 6) {
                k += 85;
            }
            if (c0 >= 16) {
                k /= 4;
                l /= 4;
                i2 /= 4;
            }
            colorCodes[c0] = ((k & 0xFF) << 16 | (l & 0xFF) << 8 | (i2 & 0xFF));
        }
    }

}
