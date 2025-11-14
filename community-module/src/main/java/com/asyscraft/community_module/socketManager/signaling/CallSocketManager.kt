package com.asyscraft.community_module.socketManager.signaling

import android.util.Log
import com.careavatar.core_utils.Constants
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class CallSocketManager(private val listener: SocketEventsListener) {
    private var socket: Socket? = null

    fun connect() {
        if (socket != null) return
        try {
            val opts = IO.Options()
            opts.forceNew = true
            socket = IO.socket(Constants.socket_URL, opts)
            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("Socket","Socket connected: ${socket?.id()}")
            }
            socket?.on("existing-peers") { args ->
                (args.getOrNull(0) as? JSONObject)?.let { listener.onExistingPeers(it) }
            }
            socket?.on("new-peer") { args ->
                (args.getOrNull(0) as? JSONObject)?.let { listener.onNewPeer(it) }
            }
            socket?.on("signal") { args ->
                (args.getOrNull(0) as? JSONObject)?.let { listener.onSignal(it) }
            }
            socket?.on("peer-left") { args ->
                (args.getOrNull(0) as? JSONObject)?.let { listener.onPeerLeft(it) }
            }
            socket?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun joinRoom(roomId: String, userMeta: JSONObject) {
        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("userMeta", userMeta)
        }
        socket?.emit("join-room", payload)
    }

    fun sendSignal(to: String, type: String, data: JSONObject) {
        val payload = JSONObject().apply {
            put("to", to)
            put("from", socket?.id())
            put("type", type)
            put("data", data)
        }
        socket?.emit("signal", payload)
    }

    fun leaveRoom() {
        socket?.emit("leave-room")
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    interface SocketEventsListener {
        fun onExistingPeers(data: JSONObject)
        fun onNewPeer(data: JSONObject)
        fun onSignal(data: JSONObject)
        fun onPeerLeft(data: JSONObject)
    }
}
