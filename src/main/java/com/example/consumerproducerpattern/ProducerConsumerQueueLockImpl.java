package com.example.consumerproducerpattern;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerQueueLockImpl<T> implements ProducerConsumerQueue<T> {

    private LinkedList<T> queue;
    private int capacity;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public ProducerConsumerQueueLockImpl(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    @Override
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == this.capacity) {
                notFull.await();
            }
            queue.addLast(item);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            T item = queue.removeFirst();
            notFull.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() throws InterruptedException {
        return queue.size();
    }
}
