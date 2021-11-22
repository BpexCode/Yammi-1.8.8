package ru.yammi.module.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import ru.yammi.Yammi;
import ru.yammi.event.EventTarget;
import ru.yammi.event.events.Render3DEvent;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.R3DUtils;

public class RangeSpheres extends Module {

    public RangeSpheres() {
        super("RangeSpheres", Category.Render, "Zalupa");
    }

    @EventTarget
    public void onRender3D(Render3DEvent render3DEvent) {
        if(this.getState()) {
            for (EntityPlayer en : mc.theWorld.playerEntities) {
                if (en.isEntityEqual(mc.thePlayer)) {
                    continue;
                }
                int lines = 600 / Math.round(Math.max((mc.thePlayer.getDistanceToEntity(en)), 1));
                lines = Math.min(lines, 25);

                float pt = Yammi.getInstance().getPartialTicks();
                double rX = TileEntityRendererDispatcher.staticPlayerX;
                double rY = TileEntityRendererDispatcher.staticPlayerY;
                double rZ = TileEntityRendererDispatcher.staticPlayerZ;

                double xPos = (en.lastTickPosX + (en.posX - en.lastTickPosX) * pt)
                        - rX;
                double yPos = en.getEyeHeight()
                        + (en.lastTickPosY + (en.posY - en.lastTickPosY) * pt)
                        - rY;
                double zPos = (en.lastTickPosZ + (en.posZ - en.lastTickPosZ) * pt)
                        - rZ;
                float range = 3.5f;
                if (Yammi.getInstance().getFriendList().contains(en.getName())) {
                    R3DUtils.drawSphere(0.0, 1, 1, 0.5, xPos, yPos, zPos, range, lines, lines, 2);
                    continue;
                }
                if (mc.thePlayer.getDistanceToEntity(en) >= range) {
                    if (mc.thePlayer.isOnSameTeam(en)) {
                        R3DUtils.drawSphere(0.5, 1, 0.5, 0.5, xPos, yPos, zPos, range, lines, lines, 2);
                    } else {
                        R3DUtils.drawSphere(1, 0.8, 0.4, 0.5, xPos, yPos, zPos, range, lines, lines, 2);
                    }

                } else {
                    if (mc.thePlayer.isOnSameTeam(en)) {
                        R3DUtils.drawSphere(1, 0.4, 0.6, 0.7, xPos, yPos, zPos, range, lines, lines, 2);
                    } else {
                        R3DUtils.drawSphere(1, 0.6, 0.4, 0.7, xPos, yPos, zPos, range, lines, lines, 2);
                    }
                }

            }
        }
    }
}
