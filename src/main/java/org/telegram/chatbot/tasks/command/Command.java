package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import org.reflections.Reflections;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

public interface Command extends Runnable {

    Set<Command> INSTANCES = new HashSet<>();

    static void hookupListeners(CommandProducer commandProducer, TelegramBot telegramBot) {
        new Reflections(Command.class.getPackage().getName())
                .getSubTypesOf(Command.class)
                .stream()
                .map(concreteCommandClass -> {
                    try {
                        return concreteCommandClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(INSTANCES::add)
                .peek(concreteCommandInstance -> concreteCommandInstance.setTelegramBot(telegramBot))
                .peek(concreteCommandInstance -> commandProducer.hookupBlockingQueue(concreteCommandInstance.getBlockingQueue()))
                .map(Thread::new)
                .forEach(Thread::start);
    }

    String getRegexCommand();
    BlockingQueue<PayloadCommand> getBlockingQueue();
    void setTelegramBot(TelegramBot telegramBot);

    default boolean isItAValidCommand(String plainText) {
        return Pattern
                .compile("^\\/[a-z0-9:]{2,}(\\s.+)*$")
                .matcher(plainText)
                .matches();
    }

    default boolean checkRegexForCurrentConcreteCommandInstance(String plainText) {
        return Pattern
                .compile(this.getRegexCommand())
                .matcher(plainText)
                .matches();
    }

}
