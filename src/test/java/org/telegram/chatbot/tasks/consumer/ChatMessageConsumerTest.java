package org.telegram.chatbot.tasks.consumer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.telegram.chatbot.tasks.command.producer.CommandProducer;
import org.telegram.chatbot.tasks.command.payload.PayloadCommand;
import org.telegram.chatbot.tasks.exception.ChatBadResponseException;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChatMessageConsumerTest {

    @Mock
    private TelegramBot telegramBot;

    @Captor
    private ArgumentCaptor<Set<PayloadCommand>> payloadCommandArgumentCaptor;

    @Test(expected = ChatBadResponseException.class)
    public void throw_ChatBadResponse_exception_when_response_is_not_ok() throws ChatBadResponseException {
        // [+] given
        GetUpdatesResponse getUpdatesResponse = mock(GetUpdatesResponse.class);
        when(getUpdatesResponse.isOk()).thenReturn(false);
        // [-] given

        // [+] when
        when(this.telegramBot.execute(any(GetUpdates.class))).thenReturn(getUpdatesResponse);
        ChatMessageConsumer chatMessageConsumer = new ChatMessageConsumer(this.telegramBot, null);
        chatMessageConsumer.consume();
        // [-] when
    }

    @Test
    public void retrieves_empty_list_message_from_api_so_provides_empty_set_to_CommandProducer() throws ChatBadResponseException {
        // [+] given
        GetUpdatesResponse getUpdatesResponse = mock(GetUpdatesResponse.class);
        when(getUpdatesResponse.isOk()).thenReturn(true);
        when(getUpdatesResponse.updates()).thenReturn(emptyList());

        CommandProducer commandProducer = mock(CommandProducer.class);
        // [-] given

        // [+] when
        when(this.telegramBot.execute(any(GetUpdates.class))).thenReturn(getUpdatesResponse);
        ChatMessageConsumer chatMessageConsumer = new ChatMessageConsumer(this.telegramBot, commandProducer);
        chatMessageConsumer.consume();
        // [-] when

        // [+] then
        verify(commandProducer).produce(eq(emptySet()));
        // [-] then
    }

    @Test
    public void retrieves_valid_messages_from_api_so_provides_the_resulting_set_to_CommandProducer() throws ChatBadResponseException {
        // [+] given
        GetUpdatesResponse getUpdatesResponse = mock(GetUpdatesResponse.class);
        when(getUpdatesResponse.isOk()).thenReturn(true);

        Update commandInsert = mockResponseListUpdate(123L, "/nova lembrar de fazer dever de casa :(");
        Update commandHelp = mockResponseListUpdate(123L, "/ajuda");
        Update commandMarkAsDone = mockResponseListUpdate(123L, "/pronta:a1b2");
        Update commandRemove = mockResponseListUpdate(123L, "/remover:a1b2");
        when(getUpdatesResponse.updates()).thenReturn(
                asList(commandInsert, commandHelp, commandMarkAsDone, commandRemove)
        );

        CommandProducer commandProducer = mock(CommandProducer.class);
        // [-] given

        // [+] when
        when(this.telegramBot.execute(any(GetUpdates.class))).thenReturn(getUpdatesResponse);
        ChatMessageConsumer chatMessageConsumer = new ChatMessageConsumer(this.telegramBot, commandProducer);
        chatMessageConsumer.consume();
        // [-] when

        // [+] then
        verify(commandProducer).produce(this.payloadCommandArgumentCaptor.capture());
        Set<PayloadCommand> payloadCommands = this.payloadCommandArgumentCaptor.getValue();
        assertThat(payloadCommands, is(not(nullValue())));
        assertThat(payloadCommands.size(), equalTo(4));
        assertThat(
                payloadCommands.stream()
                        .map(PayloadCommand::getChatId)
                        .collect(Collectors.toSet())
                        .size(),
                equalTo(1)
        );
        assertThat(
                payloadCommands.stream()
                        .map(PayloadCommand::getPlainText)
                        .collect(Collectors.toSet()),
                hasItems(
                        "/nova lembrar de fazer dever de casa :(",
                        "/ajuda",
                        "/remover:a1b2",
                        "/pronta:a1b2"
                )
        );
        // [-] then
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
