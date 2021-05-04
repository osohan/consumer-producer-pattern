package com.example.consumerproducerpattern;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConsumerProducerPatternApplicationTests {

    @Test
    public void lockQueueTest() throws InterruptedException {
        int testDataSize = 1000;
        ProducerConsumerQueue<String> queue = new ProducerConsumerQueueLockImpl<>(3);
        testQueue(queue, testDataSize);
    }

    @Test
    public void arrayBlockingQueueTest() throws InterruptedException {
        int testDataSize = 1000;
        ProducerConsumerQueue<String> queue = new ProducerConsumerArrayBlockingQueueImpl<>(3);
        testQueue(queue, testDataSize);
    }

    public void testQueue(ProducerConsumerQueue<String> queue, int testDataSize) throws InterruptedException {
        List<String> produceData = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < testDataSize; i++) {
            produceData.add(String.valueOf(i));
        }
        List<String> result = Collections.synchronizedList(new ArrayList<>(produceData.size()));
        AtomicInteger atomicInteger = new AtomicInteger(testDataSize);

        AtomicBoolean producerFinished = new AtomicBoolean(false);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                produceData.forEach(s -> {
                    try {
                        queue.put(s);
                    } catch (InterruptedException ex) {
//                        ignore
                    }
                });
                producerFinished.set(true);
                countDownLatch.countDown();
            }
        }).start();

        AtomicBoolean resultFlag = new AtomicBoolean();
        for (int z = 0; z < 4; z++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String s = queue.take();
                            result.add(s);
                            int val = atomicInteger.decrementAndGet();

                            if (!producerFinished.get()) continue;
                            if (val == 0) {
                                resultFlag.set(queue.size() == 0);
                                while (countDownLatch.getCount() > 0) countDownLatch.countDown();
                                break;
                            }
                        } catch (InterruptedException ex) {
//                        ignore
                        }
                    }
                }
            }).start();
        }

        countDownLatch.await();

        Collections.sort(result);
        Collections.sort(produceData);

        assertTrue(resultFlag.get());
        assertEquals(result, produceData);
    }


}
