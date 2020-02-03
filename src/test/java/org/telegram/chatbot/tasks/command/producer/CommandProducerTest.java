package org.telegram.chatbot.tasks.command.producer;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandProducerTest {

    @Test
    public void blocking_queues_are_properly_being_filled() {
        Update commandInsert = mockResponseListUpdate(123L, "/nova lembrar de fazer dever de casa :(");
        Update commandHelp = mockResponseListUpdate(123L, "/ajuda");
        Update commandMarkAsDone = mockResponseListUpdate(123L, "/pronta:a1b2");
        Update commandRemove = mockResponseListUpdate(123L, "/remover:a1b2");

        Set<PayloadCommand> payloadCommands = Arrays.asList(commandInsert, commandHelp, commandMarkAsDone, commandRemove)
                .stream()
                .map(PayloadCommand::new)
                .collect(Collectors.toSet());

        // TODO verificar quantas iteracoes aconteceram no conjunto blockingQueues:Set<BlockingQueue<PayloadCommand>>
//        CommandProducer commandProducer = new CommandProducer();
//        commandProducer.produce(payloadCommands);
//        commandProducer.getBlockingQueues();
    }

    private Update mockResponseListUpdate(Long chatId, String plainText) {
        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(chatId);

        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn(plainText);

        Update chatUpdate = mock(Update.class);
        when(chatUpdate.message()).thenReturn(message);

        return chatUpdate;
    }
}