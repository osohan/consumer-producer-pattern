package com.example.consumerproducerpattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerArrayBlockingQueueImpl<T> implements ProducerConsumerQueue<T> {

    private Logger logger = LoggerFactory.getLogger(ProducerConsumerArrayBlockingQueueImpl.class);

    private BlockingQueue<T> blockingQueue;

    public ProducerConsumerArrayBlockingQueueImpl(int capacity) {
        logger.info("initialized with capacity = " + capacity);
        blockingQueue = new ArrayBlockingQueue<T>(capacity);
    }

    @Override
    public void put(T item) throws InterruptedException {
        logger.info("put item: " + item);
        blockingQueue.put(item);
    }

    @Override
    public T take() throws InterruptedException {
        T item = blockingQueue.take();
        logger.info("take item: " + item);
        return item;
    }

    @Override
    public int size() throws InterruptedException {
        return blockingQueue.size();
    }
}
