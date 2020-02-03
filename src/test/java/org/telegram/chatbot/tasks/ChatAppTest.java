package org.telegram.chatbot.tasks;

import org.junit.Test;
import org.telegram.chatbot.tasks.exception.ChatBadResponseException;

public class ChatAppTest {

    @Test(expected = IllegalArgumentException.class)
    public void args_cannot_be_null() throws InterruptedException, ChatBadResponseException {
        ChatApp.main(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void args_must_greater_than_zero() throws InterruptedException, ChatBadResponseException {
        ChatApp.main(new String[]{});
    }

}