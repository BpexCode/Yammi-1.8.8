package ru.yammi.event.events;

import ru.yammi.event.Event;

public class Render3DEvent extends Event {

    public float renderPartialTicks;

    public Render3DEvent(float renderPartialTicks) {
        this.renderPartialTicks = renderPartialTicks;
    }

    public static class Post extends Render3DEvent {

        public Post(float renderPartialTicks) {
            super(renderPartialTicks);
        }
    }

    public static class Pre extends Render3DEvent {

        public Pre(float renderPartialTicks) {
            super(renderPartialTicks);
        }
    }
}
