package ru.yammi.utils;

import org.lwjgl.input.Keyboard;

public class KeyboardUtils {

    private long pause = 0L;
    private long time = 0L;

    public KeyboardUtils(long pauseIn){
        this.pause = pauseIn;
    }

    public boolean isKeyDown(int key) {
        if(this.time <= System.currentTimeMillis()) {
            if(Keyboard.isKeyDown(key)) {
                this.time = System.currentTimeMillis() + pause;
                return true;
            }
        }
        return false;
    }

}
