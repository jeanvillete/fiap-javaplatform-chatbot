package org.telegram.chatbot.tasks.command.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class CommandProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandProducer.class.getName());

    private Set<BlockingQueue<PayloadCommand>> blockingQueues = new HashSet<>();

    public CommandProducer() {
        LOGGER.debug("Initializing CommandProducer instance.");
    }

    public void produce(Set<PayloadCommand> payloadCommandSet) {
        if (payloadCommandSet.size() > 0) {
            LOGGER.debug(
                    "A new set of side [{}] with payload commands received, " +
                            "so forwarding it to concrete commands through their available queues",
                    payloadCommandSet.size()
            );
        }

        payloadCommandSet.stream()
                .peek(payloadCommand -> LOGGER.debug("Forwarding PayloadCommand [{}]", payloadCommand))
                .forEach(payloadCommand ->
                        this.blockingQueues.stream()
                                .forEach(blockingQueue -> {
                                    try {
                                        blockingQueue.put(payloadCommand);
                                    } catch (InterruptedException e) {
                                        LOGGER.error("Error while forwarding PayloadCommand to a blocking queue; ", e);
                                        throw new RuntimeException(e);
                                    }
                                })
                );
    }

    public void hookupBlockingQueue(BlockingQueue<PayloadCommand> blockingQueue) {
        this.blockingQueues.add(blockingQueue);
        LOGGER.debug("Hooking up blocking queue, so now there're [{}] plugged in.", this.blockingQueues.size());
    }
}
