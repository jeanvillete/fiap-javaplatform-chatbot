package org.fiap.test;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws InterruptedException {

        //Criação do objeto bot com as informações de acesso
        TelegramBot bot = TelegramBotAdapter.build("838479335:AAGJqj6BjD9B3ya3DLPZbRcvecHPOrm-sfE");

        //objeto responsável por receber as mensagens
        GetUpdatesResponse updatesResponse;
        //objeto responsável por gerenciar o envio de respostas
        SendResponse sendResponse;
        //objeto responsável por gerenciar o envio de ações do chat
        BaseResponse baseResponse;

        //controle de off-set, isto é, a partir deste ID será lido as mensagens pendentes na fila
        int m = 0;

        //loop infinito pode ser alterado por algum timer de intervalo curto
        while (true) {
            System.out.println("still running... on thread; " + Thread.currentThread().getName());

            //executa comando no Telegram para obter as mensagens pendentes a partir de um off-set (limite inicial)
            updatesResponse = bot.execute(new GetUpdates().limit(100).offset(m));

            //lista de mensagens
            List<Update> updates = updatesResponse.updates();

            //análise de cada ação da mensagem
            for (Update update : updates) {

                //atualização do off-set
                m = update.updateId() + 1;

                System.out.println("Recebendo mensagem:" + update.message().text());
                System.out.println("Msg Id; " + update.message().chat().id());

                //envio de "Escrevendo" antes de enviar a resposta
                baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
                Thread.sleep(1000);

                //verificação de ação de chat foi enviada com sucesso
                System.out.println("Resposta de Chat Action Enviada?" + baseResponse.isOk());

                //envio da mensagem de resposta
                sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Não entendi..."));
                //verificação de mensagem enviada com sucesso
                System.out.println("Mensagem Enviada?" + sendResponse.isOk());

            }

        }


    }
}
