package ru.yammi.utils.path;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import ru.yammi.utils.MoveUtils;

import java.util.ArrayList;

public class FlyPathProcessor extends APathProcessor
{
    private final boolean creativeFlying;
    private boolean stopped;

    public FlyPathProcessor(final ArrayList<PathPos> path, final boolean creativeFlying) {
        super(path);
        this.creativeFlying = creativeFlying;
    }
    @Override
    public void process() {
        final BlockPos pos = new BlockPos(this.mc.thePlayer);
        final BlockPos nextPos = this.path.get(this.index);
        if (pos.equals(nextPos)) {
            ++this.index;
            if (this.index < this.path.size()) {
                if (this.creativeFlying && this.index >= 2) {
                    final BlockPos prevPos = this.path.get(this.index - 1);
                    if (!this.path.get(this.index).subtract(prevPos).equals(prevPos.subtract(this.path.get(this.index - 2))) && !this.stopped) {
                        final EntityPlayerSP thePlayer = this.mc.thePlayer;
                        thePlayer.motionX /= Math.max(Math.abs(this.mc.thePlayer.motionX) * 50.0, 1.0);
                        final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
                        thePlayer2.motionY /= Math.max(Math.abs(this.mc.thePlayer.motionY) * 50.0, 1.0);
                        final EntityPlayerSP thePlayer3 = this.mc.thePlayer;
                        thePlayer3.motionZ /= Math.max(Math.abs(this.mc.thePlayer.motionZ) * 50.0, 1.0);
                        this.stopped = true;
                    }
                }
            }
            else {
                if (this.creativeFlying) {
                    final EntityPlayerSP thePlayer4 = this.mc.thePlayer;
                    thePlayer4.motionX /= Math.max(Math.abs(this.mc.thePlayer.motionX) * 50.0, 1.0);
                    final EntityPlayerSP thePlayer5 = this.mc.thePlayer;
                    thePlayer5.motionY /= Math.max(Math.abs(this.mc.thePlayer.motionY) * 50.0, 1.0);
                    final EntityPlayerSP thePlayer6 = this.mc.thePlayer;
                    thePlayer6.motionZ /= Math.max(Math.abs(this.mc.thePlayer.motionZ) * 50.0, 1.0);
                }
                this.done = true;
            }
            return;
        }
        this.stopped = false;
        this.lockControls();
        MoveUtils.faceBlockClientHorizontally(nextPos);
        this.mc.thePlayer.motionY = MathHelper.clamp_double(this.mc.thePlayer.motionY, -0.25, 0.25);
        if (pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ()) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
            if (this.mc.thePlayer.isCollidedHorizontally) {
                if (this.mc.thePlayer.posY > nextPos.getY() + 0.2) {
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
                else if (this.mc.thePlayer.posY < nextPos.getY()) {
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
                }
            }
        }
        else if (pos.getY() != nextPos.getY()) {
            if (pos.getY() < nextPos.getY()) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
            }
            else {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
            if (this.mc.thePlayer.isCollidedVertically) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
            }
        }
    }

    @Override
    public void lockControls() {
        super.lockControls();
        this.mc.thePlayer.capabilities.isFlying = this.creativeFlying;
    }
}
