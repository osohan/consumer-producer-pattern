package com.example.consumerproducerpattern;

public interface ProducerConsumerQueue<T> {
    void put(T item) throws InterruptedException;
    T take() throws InterruptedException;
    int size() throws InterruptedException;
}
