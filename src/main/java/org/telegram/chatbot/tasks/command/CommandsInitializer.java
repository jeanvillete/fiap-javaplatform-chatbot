package org.telegram.chatbot.tasks.command;

import com.pengrad.telegrambot.TelegramBot;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;
import org.telegram.chatbot.tasks.session.ChatSessionManagement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CommandsInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandsInitializer.class);

    private Set<Command> concreteInstanceSetOfCommands;

    public CommandsInitializer(CommandProducer commandProducer, TelegramBot telegramBot, ChatSessionManagement chatSessionManagement) {
        LOGGER.debug("Initializing concrete commands by reflection.");

        Set<Command> concreteInstanceSetOfCommands = new HashSet<>();

        new Reflections(Command.class.getPackage().getName())
                .getSubTypesOf(Command.class)
                .stream()
                .peek(concreteCommandClass -> LOGGER.debug("Found concrete command implemented through class with name [{}]", concreteCommandClass.getName()))
                .map(concreteCommandClass -> {
                    try {
                        Constructor<? extends Command> commandClassConstructor =
                                concreteCommandClass.getConstructor(TelegramBot.class, CommandsInitializer.class, ChatSessionManagement.class);

                        return commandClassConstructor.newInstance(telegramBot, this, chatSessionManagement);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        LOGGER.error("Error while instantiating concrete command; ", e);
                        throw new RuntimeException(e);
                    }
                })
                .peek(concreteInstanceSetOfCommands::add)
                .peek(concreteCommandInstance -> commandProducer.hookupBlockingQueue(concreteCommandInstance.getBlockingQueue()))
                .map(concreteCommandInstance -> new Thread(concreteCommandInstance, concreteCommandInstance.getClass().getName()))
                .forEach(Thread::start);

        LOGGER.debug("Every listed concrete command instantiated and with its new Thread started, maybe at this point, already working in parallel.");

        this.concreteInstanceSetOfCommands = Collections.unmodifiableSet(concreteInstanceSetOfCommands);
    }

    Set<Command> getConcreteInstanceSetOfCommands() {
        return this.concreteInstanceSetOfCommands;
    }
}
