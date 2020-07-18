package com.chess.websocket;

import com.chess.game.GameService;
import com.chess.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GameService gameService;

    Map<Long,List<WebSocketSession>> sessions = new HashMap<>();


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        String uuid = getSessionUuid(session);
        if(uuid != null) {
            List<WebSocketSession> webSocketSessionsForUuid = sessions.get(gameService.getGameIdByPlayerUuid(uuid));
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
        Player player = new Player();
        player.setUuid(getSessionUuid(session));
        Long gameId = gameService.getNextGame(player);

        if (gameId != null) {
            List<WebSocketSession> webSocketSessions = sessions.get(gameId);
            if(webSocketSessions == null) {
                webSocketSessions = new ArrayList<>();
            }
            webSocketSessions.add(session);
            sessions.put(gameId, webSocketSessions);
            if(webSocketSessions.size() == 2) {
                sendStartGameMessage(webSocketSessions, new TextMessage(new ObjectMapper().writeValueAsString(gameService.getGameById(gameId))));
            }
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

    private void sendStartGameMessage(List<WebSocketSession> webSocketSessions, TextMessage message) {
        System.out.println(message.getPayload());
        for (WebSocketSession webSocketSession : webSocketSessions) {
            if (webSocketSession.isOpen()) {
                try {
                    webSocketSession.sendMessage(message);
                } catch (IOException e) {
                    System.out.println("ERROR: " + e);
                }
            }
        }
    }

}