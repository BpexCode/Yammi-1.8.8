package ru.yammi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import ru.yammi.Yammi;
import ru.yammi.module.movement.NoSlowDown;

public class MoveHook extends MovementInput {

    private GameSettings gameSettings;
    private Minecraft mc = Minecraft.getMinecraft();

    public MoveHook(GameSettings par1GameSettings) {
        this.gameSettings = par1GameSettings;
    }

    public void updatePlayerMoveState() {
        if(this.mc.currentScreen != null) {
            if(this.mc.currentScreen instanceof GuiChat) {
                super.updatePlayerMoveState();
                return;
            }
        }
        super.moveStrafe = 0.0F;
        super.moveForward = 0.0F;
        if (Keyboard.isKeyDown(this.gameSettings.keyBindForward.getKeyCode())) {
            ++super.moveForward;
        }

        if (Keyboard.isKeyDown(this.gameSettings.keyBindBack.getKeyCode())) {
            --super.moveForward;
        }

        if (Keyboard.isKeyDown(this.gameSettings.keyBindLeft.getKeyCode())) {
            ++super.moveStrafe;
        }

        if (Keyboard.isKeyDown(this.gameSettings.keyBindRight.getKeyCode())) {
            --super.moveStrafe;
        }

        super.jump = Keyboard.isKeyDown(this.gameSettings.keyBindJump.getKeyCode());
        super.sneak = Keyboard.isKeyDown(this.gameSettings.keyBindSneak.getKeyCode());
        if (super.sneak) {
            super.moveStrafe = (float) ((double) super.moveStrafe * 0.3D);
            super.moveForward = (float) ((double) super.moveForward * 0.3D);
        }

        if(this.mc.currentScreen != null) {
            if(this.mc.currentScreen instanceof GuiChat)
                return;
        }
        if (Yammi.getInstance().isModuleEnabled(NoSlowDown.class)) {
            super.moveStrafe *= 5.0F;
            super.moveForward *= 5.0F;
        }
    }
}
