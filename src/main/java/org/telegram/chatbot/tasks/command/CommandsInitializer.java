package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import org.reflections.Reflections;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommandsInitializer {

    private Set<Command> concreteInstanceSetOfCommands;

    public CommandsInitializer(CommandProducer commandProducer, TelegramBot telegramBot, ChatSessionManagement chatSessionManagement) {
        Set<Command> concreteInstanceSetOfCommands = new HashSet<>();

        new Reflections(Command.class.getPackage().getName())
                .getSubTypesOf(Command.class)
                .stream()
                .map(concreteCommandClass -> {
                    try {
                        Constructor<? extends Command> commandClassConstructor =
                                concreteCommandClass.getConstructor(TelegramBot.class, CommandsInitializer.class, ChatSessionManagement.class);

                        return commandClassConstructor.newInstance(telegramBot, this, chatSessionManagement);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(concreteInstanceSetOfCommands::add)
                .peek(concreteCommandInstance -> commandProducer.hookupBlockingQueue(concreteCommandInstance.getBlockingQueue()))
                .map(Thread::new)
                .forEach(Thread::start);

        this.concreteInstanceSetOfCommands = Collections.unmodifiableSet(concreteInstanceSetOfCommands);
    }

    Set<Command> getConcreteInstanceSetOfCommands() {
        return this.concreteInstanceSetOfCommands;
    }
}
