package ru.yammi.utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MouseUtils {

    private long pause = 0L;
    private long time = 0L;

    public MouseUtils(long pauseIn){
        this.pause = pauseIn;
    }

    public boolean isMouseButtonDown(int key) {
        if(this.time <= System.currentTimeMillis()) {
            if(Mouse.isButtonDown(key)) {
                this.time = System.currentTimeMillis() + pause;
                return true;
            }
        }
        return false;
    }

    public boolean isMouseButtonClicked(int key) {
        return Mouse.isButtonDown(key);
    }

}

