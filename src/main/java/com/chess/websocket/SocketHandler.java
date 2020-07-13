package com.chess.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SocketHandler extends TextWebSocketHandler {


    Map<String,List<WebSocketSession>> sessions = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        String uuid = getSessionUuid(session);
        if(uuid != null) {
            List<WebSocketSession> webSocketSessionsForUuid = sessions.get(uuid);
            System.out.println(message.getPayload());
            System.out.println(webSocketSessionsForUuid);
            for (WebSocketSession webSocketSession : webSocketSessionsForUuid) {
                if (webSocketSession.isOpen()) {
                    try {
                        webSocketSession.sendMessage(message);
                    } catch (EOFException e) {
                        System.out.println("ERROR: " + e);
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uuid = getSessionUuid(session);
        if (uuid != null) {
            List<WebSocketSession> webSocketSessions = sessions.get(uuid);
            if(webSocketSessions == null) {
                webSocketSessions = new ArrayList<>();
            }
            webSocketSessions.add(session);
            sessions.put(uuid, webSocketSessions);
        } else {
            System.out.println("COULD NOT FIND SESSION UUID FOR SESSION " + session);
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("CLOSING: " + session);
    }


    public String getSessionUuid(WebSocketSession session) {
        if (session.getUri() != null) {
            String[] SplitURL = session.getUri().getPath().split("/");
            return SplitURL[SplitURL.length - 1];

        } else {
            return null;
        }
    }

}