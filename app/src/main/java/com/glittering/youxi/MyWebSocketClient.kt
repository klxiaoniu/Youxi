package com.glittering.youxi

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

open class MyWebSocketClient(serverUri: URI?) : WebSocketClient(serverUri) {

    val TAG = "MyWebSocketClient"

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i(TAG, "onOpen handshakedata=$handshakedata")
    }

    override fun onMessage(message: String?) {
        Log.i(TAG, "onMessage message=$message")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i(TAG, "onClose code=$code reason=$reason remote=$remote")
    }

    override fun onError(ex: Exception?) {
        Log.i(TAG, "onError ex=$ex")
    }
}