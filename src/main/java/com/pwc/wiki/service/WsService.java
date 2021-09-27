package com.pwc.wiki.service;

import com.pwc.wiki.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by xuhaocheng on 27/09/2021.
 */
@Service
public class WsService {
    @Resource
    public WebSocketServer
            webSocketServer;

    @Async
    public void sendInfo(String message) {
        webSocketServer.sendInfo(message);
    }
}
