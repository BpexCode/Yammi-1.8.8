package ru.yammi.event;

public class Event {

    private boolean cancel = false;

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

}
