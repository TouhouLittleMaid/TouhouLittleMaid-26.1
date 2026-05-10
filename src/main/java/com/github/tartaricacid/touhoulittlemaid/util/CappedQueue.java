package com.github.tartaricacid.touhoulittlemaid.util;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.LinkedBlockingDeque;

public class CappedQueue<E> {
    private final LinkedBlockingDeque<E> deque;

    public CappedQueue(int maxSize) {
        this.deque = new LinkedBlockingDeque<>(maxSize);
    }

    public void add(E element) {
        if (!deque.offerFirst(element)) {
            deque.pollLast();
            deque.offerFirst(element);
        }
    }

    @Nullable
    public E remove() {
        return deque.pollFirst();
    }

    @Nullable
    public E peek() {
        return deque.peekFirst();
    }

    public boolean isEmpty() {
        return deque.isEmpty();
    }

    public int size() {
        return deque.size();
    }

    public LinkedBlockingDeque<E> getDeque() {
        return deque;
    }
}
