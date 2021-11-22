package ru.yammi.utils.path;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class PathQueue {
    private final PriorityQueue<Entry> queue;

    public PathQueue() {
        //this.queue = new PriorityQueue<Entry>((e1, e2) -> Float.compare(e1.priority, e2.priority));
        this.queue = new PriorityQueue<Entry>(new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return Float.compare(o1.priority, o2.priority);
            }
        });
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public boolean add(final PathPos pos, final float priority) {
        return this.queue.add(new Entry(pos, priority));
    }

    public PathPos[] toArray() {
        final PathPos[] array = new PathPos[this.size()];
        final Iterator<Entry> itr = this.queue.iterator();
        for (int i = 0; i < this.size() && itr.hasNext(); ++i) {
            array[i] = itr.next().pos;
        }
        return array;
    }

    public int size() {
        return this.queue.size();
    }

    public void clear() {
        this.queue.clear();
    }

    public PathPos poll() {
        return this.queue.poll().pos;
    }

    private class Entry {
        private PathPos pos;
        private float priority;

        public Entry(final PathPos pos, final float priority) {
            this.pos = pos;
            this.priority = priority;
        }
    }
}
