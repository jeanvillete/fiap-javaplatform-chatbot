package org.telegram.chatbot.tasks.command.producer;

import org.telegram.chatbot.tasks.command.payload.PayloadCommand;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class CommandProducer {

    private Set<BlockingQueue<PayloadCommand>> blockingQueues = new HashSet<>();

    public void produce(Set<PayloadCommand> payloadCommandSet) {
        payloadCommandSet.stream()
                .forEach(payloadCommand ->
                        this.blockingQueues.stream()
                                .forEach(blockingQueue -> {
                                    try {
                                        blockingQueue.put(payloadCommand);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                );
    }

    public void hookupBlockingQueue(BlockingQueue<PayloadCommand> blockingQueue) {
        this.blockingQueues.add(blockingQueue);
    }
}
