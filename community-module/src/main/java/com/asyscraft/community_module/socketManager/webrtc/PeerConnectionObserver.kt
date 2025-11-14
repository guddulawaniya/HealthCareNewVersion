package com.asyscraft.community_module.socketManager.webrtc

import org.webrtc.PeerConnection
import android.util.Log

open class PeerConnectionObserver(private val tag: String = "PC") : PeerConnection.Observer {
    override fun onSignalingChange(newState: PeerConnection.SignalingState?) { Log.d("PeerConnection","$tag signaling: $newState") }
    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) { Log.d("PeerConnection","$tag iceConnection: $newState") }
    override fun onIceConnectionReceivingChange(p0: Boolean) {}
    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) { Log.d("PeerConnection","$tag iceGathering: $newState") }
    override fun onIceCandidate(candidate: org.webrtc.IceCandidate?) {}
    override fun onIceCandidatesRemoved(p0: Array<out org.webrtc.IceCandidate>?) {}
    override fun onAddStream(p0: org.webrtc.MediaStream?) {}
    override fun onRemoveStream(p0: org.webrtc.MediaStream?) {}
    override fun onDataChannel(dc: org.webrtc.DataChannel?) { Log.d("PeerConnection","$tag dataChannel") }
    override fun onRenegotiationNeeded() { Log.d("PeerConnection","$tag renegotiation") }
    override fun onAddTrack(receiver: org.webrtc.RtpReceiver?, streams: Array<out org.webrtc.MediaStream>?) { Log.d("PeerConnection","$tag onAddTrack") }
}
