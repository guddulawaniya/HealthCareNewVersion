package com.asyscraft.community_module.socketManager.webrtc


import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import android.util.Log

open class SdpObserverImpl : SdpObserver {
    override fun onCreateSuccess(p0: SessionDescription?) { Log.d("SdpObserver","SdpObserver onCreateSuccess") }
    override fun onSetSuccess() { Log.d("SdpObserver","SdpObserver onSetSuccess") }
    override fun onCreateFailure(p0: String?) { Log.d("SdpObserver","SdpObserver onCreateFailure: $p0") }
    override fun onSetFailure(p0: String?) { Log.d("SdpObserver","SdpObserver onSetFailure: $p0") }
}
