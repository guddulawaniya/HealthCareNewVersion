package com.asyscraft.community_module.socketManager

import android.util.Log
import com.careavatar.core_utils.Constants
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject

object SocketManager {
    private var mSocket: Socket? = null

    // Event flows for observing data
    private val _onGroupMessageReceived = MutableSharedFlow<JSONObject>(
        replay = 1,               // store last emitted event
        extraBufferCapacity = 1   // prevent event drop
    )

    val onGroupMessageReceived = _onGroupMessageReceived.asSharedFlow()




    private val _onUserTyping = MutableSharedFlow<String>()
    val onUserTyping = _onUserTyping.asSharedFlow()

    fun connect(userId: String) {
        try {
            if (mSocket == null) {
                val options = IO.Options().apply {
                    reconnection = true
                    query = "userId=$userId"
                }
                mSocket = IO.socket(Constants.socket_URL, options)
            }

            mSocket?.apply {
                on(Socket.EVENT_CONNECT) {
                    Log.d("SocketManager", "Connected ✅")
                }

                on("receiveGroupMessage") { args ->
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        Log.d("SocketManager", "Message received: $data")
                        _onGroupMessageReceived.tryEmit(data)
                    }
                }

                on("typing") { args ->
                    if (args.isNotEmpty()) {
                        val user = args[0].toString()
                        _onUserTyping.tryEmit(user)
                    }
                }

                connect()
            }
        } catch (e: Exception) {
            Log.e("SocketManager", "Connection error: ${e.message}")
        }
    }


    fun addUser(userId: String) {
        mSocket?.emit("addUser", userId)
    }

    fun joinRoom(userId: String,communityId: String) {
        mSocket?.emit("joinRoom", communityId, userId)
    }

    fun sendGroundMessage(message: JSONObject) {
        mSocket?.emit("sendGroundMessage", message)
    }

    fun emitTyping(userId: String) {
        mSocket?.emit("typing", userId)
    }

    fun disconnect() {
        mSocket?.disconnect()
        mSocket = null
    }

    fun isConnected(): Boolean = mSocket?.connected() == true
}
