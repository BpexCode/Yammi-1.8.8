package ru.yammi.hook;

import net.minecraft.client.renderer.EntityRenderer;

import java.util.HashMap;

public class MapHook extends HashMap{

    @Override
    public boolean isEmpty() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement ste = stackTraceElements[3];
        if(ste.getClassName().equals(EntityRenderer.class.getName()))
            return false;
        return super.isEmpty();
    }
}
