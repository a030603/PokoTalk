package com.murphy.pokotalk.listener;

import com.github.nkzawa.socketio.client.Socket;
import com.murphy.pokotalk.server.PokoServer;
import com.murphy.pokotalk.server.Status;

public class OnDisconnectionListener extends PokoServer.SocketEventListener {
    @Override
    public String getEventName() {
        return Socket.EVENT_DISCONNECT;
    }

    @Override
    public void call(Status status, Object... args) {

    }
}
