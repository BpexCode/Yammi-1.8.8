package ru.yammi.utils.path;

import net.minecraft.block.properties.IProperty;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.BlockLadder;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import ru.yammi.utils.MoveUtils;

import java.util.ArrayList;

public class WalkPathProcessor extends APathProcessor
{
    public WalkPathProcessor(final ArrayList<PathPos> path) {
        super(path);
    }

    @Override
    public void process() {
        final BlockPos pos = new BlockPos(this.mc.thePlayer);
        final BlockPos nextPos = this.path.get(this.index);
        if (pos.equals(nextPos)) {
            ++this.index;
            if (this.index >= this.path.size()) {
                this.done = true;
            }
            return;
        }
        this.lockControls();
        MoveUtils.faceBlockClientHorizontally(nextPos);
        if (pos.getX() != nextPos.getX() || pos.getZ() != nextPos.getZ()) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
            if (this.mc.thePlayer.isInWater() && this.mc.thePlayer.posY < nextPos.getY()) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
            }
        }
        else if (pos.getY() != nextPos.getY()) {
            if (pos.getY() < nextPos.getY()) {
                if (APathFinder.getBlock(pos) instanceof BlockLadder) {
                    MoveUtils.faceBlockClientHorizontally(pos.offset(((EnumFacing)APathFinder.getState(pos).getValue(BlockLadder.FACING)).getOpposite()));
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
                }
                else {
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), true);
                    if (this.index < this.path.size() - 1) {
                        MoveUtils.faceBlockClientHorizontally(this.path.get(this.index + 1));
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
                    }
                }
            }
            else {
                while (this.index < this.path.size() - 1 && this.path.get(this.index).offset(EnumFacing.DOWN).equals(this.path.get(this.index + 1))) {
                    ++this.index;
                }
                if (this.mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
                }
            }
        }
    }

    @Override
    public void lockControls() {
        super.lockControls();
        this.mc.thePlayer.capabilities.isFlying = false;
    }
}
