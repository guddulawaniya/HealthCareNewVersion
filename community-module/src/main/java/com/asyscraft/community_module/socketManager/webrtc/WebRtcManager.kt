package com.asyscraft.community_module.socketManager.webrtc

import android.content.Context
import org.json.JSONObject
import org.webrtc.*
import java.util.concurrent.Executors

class WebRtcManager(
    private val context: Context,
    private val onLocalStream: (videoTrack: VideoTrack) -> Unit,
    private val onRemoteStream: (peerId: String, videoTrack: VideoTrack?) -> Unit,
    private val sendSignal: (to: String, type: String, payload: JSONObject) -> Unit
) {
    private val executor = Executors.newSingleThreadExecutor()
    private val eglBase = EglBase.create()
    private val pcFactory: PeerConnectionFactory
    private var localVideoSource: VideoSource
    private var localAudioSource: AudioSource
    private var localVideoTrack: VideoTrack
    private var localAudioTrack: AudioTrack

    // per-peer state
    private val peerConnections = mutableMapOf<String, PeerConnection>()
    private val rtpSenders = mutableMapOf<String, RtpSender>() // peerId -> video sender

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions()
        )
        val options = PeerConnectionFactory.Options()
        pcFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .createPeerConnectionFactory()

        // create local video & audio track (camera)
        val videoCapturer = createCameraCapturer()
            ?: throw RuntimeException("No camera available")
        localVideoSource = pcFactory.createVideoSource(videoCapturer.isScreencast)
        val stHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
        videoCapturer.initialize(stHelper, context, localVideoSource.capturerObserver)
        videoCapturer.startCapture(1280, 720, 30)
        localVideoTrack = pcFactory.createVideoTrack("LOCAL_VIDEO", localVideoSource)

        localAudioSource = pcFactory.createAudioSource(MediaConstraints())
        localAudioTrack = pcFactory.createAudioTrack("LOCAL_AUDIO", localAudioSource)

        onLocalStream(localVideoTrack)
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames
        // prefer front
        deviceNames.firstOrNull { enumerator.isFrontFacing(it) }?.let {
            enumerator.createCapturer(it, null)?.let { return it }
        }
        // fallback any
        for (name in deviceNames) {
            enumerator.createCapturer(name, null)?.let { return it }
        }
        return null
    }

    private fun createPeerConnection(peerId: String): PeerConnection {
        val iceServers = listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        val pc = pcFactory.createPeerConnection(rtcConfig, object : PeerConnectionObserver("PC-$peerId") {
            override fun onIceCandidate(candidate: IceCandidate?) {
                candidate?.let {
                    val json = JSONObject().apply {
                        put("sdpMid", it.sdpMid)
                        put("sdpMLineIndex", it.sdpMLineIndex)
                        put("candidate", it.sdp)
                    }
                    sendSignal(peerId, "ice-candidate", json)
                }
            }

            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
                // try to get remote video track
                receiver?.track()?.let { track ->
                    if (track is VideoTrack) {
                        onRemoteStream(peerId, track)
                    }
                }
                super.onAddTrack(receiver, streams)
            }

            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                super.onIceConnectionChange(newState)
            }
        }) ?: throw RuntimeException("Failed to create PeerConnection")

        // Add local tracks using addTrack so we can replace via RtpSender later
        val localStream = ArrayList<String>()
        val audioSender = pc.addTrack(localAudioTrack)
        val videoSender = pc.addTrack(localVideoTrack)
        rtpSenders[peerId] = videoSender
        return pc
    }

    // Called when joining and server returns existing peers: we should create offer to each
    fun handleExistingPeers(peers: List<String>) {
        peers.forEach { peerId ->
            executor.execute {
                val pc = createPeerConnection(peerId)
                peerConnections[peerId] = pc
                // create offer
                pc.createOffer(object : SdpObserverImpl() {
                    override fun onCreateSuccess(desc: SessionDescription?) {
                        desc?.let {
                            pc.setLocalDescription(SdpObserverImpl(), it)
                            val payload = JSONObject().apply {
                                put("sdp", it.description)
                                put("type", it.type.canonicalForm())
                            }
                            sendSignal(peerId, "offer", payload)
                        }
                    }
                }, MediaConstraints())
            }
        }
    }

    // When server notifies new peer joined; create pc and wait for their offer
    fun handleNewPeer(peerId: String) {
        executor.execute {
            val pc = createPeerConnection(peerId)
            peerConnections[peerId] = pc
        }
    }

    fun handleSignal(from: String, type: String, data: JSONObject) {
        executor.execute {
            val pc = peerConnections[from] ?: createPeerConnection(from).also { peerConnections[from] = it }
            when (type) {
                "offer" -> {
                    val sdp = data.getString("sdp")
                    val offer = SessionDescription(SessionDescription.Type.OFFER, sdp)
                    pc.setRemoteDescription(SdpObserverImpl(), offer)
                    pc.createAnswer(object : SdpObserverImpl() {
                        override fun onCreateSuccess(desc: SessionDescription?) {
                            desc?.let {
                                pc.setLocalDescription(SdpObserverImpl(), it)
                                val payload = JSONObject().apply {
                                    put("sdp", it.description)
                                    put("type", it.type.canonicalForm())
                                }
                                sendSignal(from, "answer", payload)
                            }
                        }
                    }, MediaConstraints())
                }
                "answer" -> {
                    val sdp = data.getString("sdp")
                    val answer = SessionDescription(SessionDescription.Type.ANSWER, sdp)
                    pc.setRemoteDescription(SdpObserverImpl(), answer)
                }
                "ice-candidate" -> {
                    val candidate = IceCandidate(
                        data.getString("sdpMid"),
                        data.getInt("sdpMLineIndex"),
                        data.getString("candidate")
                    )
                    pc.addIceCandidate(candidate)
                }
            }
        }
    }

    fun removePeer(peerId: String) {
        executor.execute {
            peerConnections[peerId]?.close()
            peerConnections.remove(peerId)
            rtpSenders.remove(peerId)
        }
    }

    // Toggle mic / video
    fun toggleMic(enable: Boolean) { localAudioTrack.setEnabled(enable) }
    fun toggleVideo(enable: Boolean) { localVideoTrack.setEnabled(enable) }

    // Replace local video track (useful for screen share)
    fun replaceVideoTrack(newTrack: VideoTrack) {
        localVideoTrack = newTrack
        // update all peer senders
        rtpSenders.forEach { (_, sender) ->
            sender.setTrack(newTrack, false)
        }
        onLocalStream(localVideoTrack)
    }

    fun stop() {
        executor.execute {
            peerConnections.values.forEach { it.close() }
            peerConnections.clear()
            rtpSenders.clear()
            localVideoSource.dispose()
            localAudioSource.dispose()
            pcFactory.dispose()
            eglBase.release()
        }
    }
}
