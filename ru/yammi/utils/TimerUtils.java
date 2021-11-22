package ru.yammi.utils;

import org.lwjgl.input.Keyboard;

public class TimerUtils {

    private long pause = 0L;
    private long time = 0L;

    public TimerUtils(long pauseIn){
        this.pause = pauseIn;
    }

    public boolean isTimeReached() {
        if(this.time <= System.currentTimeMillis()) {
            this.time = System.currentTimeMillis() + pause;
            return true;
        }
        return false;
    }

    public void setTimeout(long timeout){
        this.pause = timeout;
    }

    public void setPause(){
        this.time = System.currentTimeMillis() + pause;
    }

}
