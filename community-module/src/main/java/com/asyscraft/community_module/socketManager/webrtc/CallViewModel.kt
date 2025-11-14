package com.asyscraft.community_module.socketManager.webrtc

import android.content.Context
import androidx.lifecycle.ViewModel
import com.asyscraft.community_module.socketManager.signaling.CallSocketManager
import org.json.JSONObject

class CallViewModel(private val context: Context) : ViewModel(), CallSocketManager.SocketEventsListener {
    private lateinit var socketManager: CallSocketManager
    private lateinit var webRtcManager: WebRtcManager
    var localVideoCallback: ((videoTrack: org.webrtc.VideoTrack) -> Unit)? = null
    var remoteVideoCallback: ((peerId: String, videoTrack: org.webrtc.VideoTrack?) -> Unit)? = null

    fun init(roomId: String) {
        socketManager = CallSocketManager(this)
        socketManager.connect()
        socketManager.joinRoom(roomId, JSONObject().put("displayName", "AndroidUser"))

        webRtcManager = WebRtcManager(context,
            onLocalStream = { vt -> localVideoCallback?.invoke(vt) },
            onRemoteStream = { peerId, vt -> remoteVideoCallback?.invoke(peerId, vt) },
            sendSignal = { to, type, payload -> socketManager.sendSignal(to, type, payload) }
        )
    }

    override fun onExistingPeers(data: JSONObject) {
        val arr = data.getJSONArray("peers")
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) list.add(arr.getString(i))
        webRtcManager.handleExistingPeers(list)
    }

    override fun onNewPeer(data: JSONObject) {
        val peerId = data.getString("peerId")
        webRtcManager.handleNewPeer(peerId)
    }

    override fun onSignal(data: JSONObject) {
        val from = data.getString("from")
        val type = data.getString("type")
        val payload = data.getJSONObject("data")
        webRtcManager.handleSignal(from, type, payload)
    }

    override fun onPeerLeft(data: JSONObject) {
        val peerId = data.getString("peerId")
        webRtcManager.removePeer(peerId)
    }

    fun toggleMic(enable: Boolean) = webRtcManager.toggleMic(enable)
    fun toggleVideo(enable: Boolean) = webRtcManager.toggleVideo(enable)
    fun replaceVideoTrack(track: org.webrtc.VideoTrack) = webRtcManager.replaceVideoTrack(track)
    fun leave() {
        socketManager.leaveRoom()
        webRtcManager.stop()
    }
}
