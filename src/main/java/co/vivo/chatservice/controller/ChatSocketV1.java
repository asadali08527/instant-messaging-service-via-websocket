package co.vivo.chatservice.controller;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ServerEndpoint("/v1/chat")
@ApplicationScoped
public class ChatSocketV1 {

    Logger logger = LoggerFactory.getLogger(ChatSocketV1.class);

    private Set<Session> sessions = new HashSet<>();

    @Inject
    MessageRepository messageRepository;

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        broadcast("User joined the chat");
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        broadcast("User left the chat");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        CompletableFuture.runAsync(() -> {
            MessageEntity msgEntity = new MessageEntity();
            msgEntity.setContent(message);
            msgEntity.setTimestamp(LocalDateTime.now());
            messageRepository.saveMessage(msgEntity);
            logger.info("Message :{} saved", message.toString());
        });
        broadcast(message);
    }

    private void broadcast(String message) {
        for (Session session : sessions) {
            session.getAsyncRemote().sendText(message);
        }
    }
}
