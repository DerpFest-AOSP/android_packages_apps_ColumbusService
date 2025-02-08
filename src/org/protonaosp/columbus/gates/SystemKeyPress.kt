package org.protonaosp.columbus.gates

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.InputEvent
import android.view.InputEventReceiver
import android.view.KeyEvent
import com.android.systemui.shared.system.InputChannelCompat
import com.android.systemui.shared.system.InputMonitorCompat
import org.protonaosp.columbus.TAG

class SystemKeyPress(context: Context, handler: Handler) : TransientGate(context, handler) {
    private val blockingKeys =
        setOf(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_POWER)

    private var inputEventReceiver: InputChannelCompat.InputEventReceiver? = null
    private var inputMonitor: InputMonitorCompat? = null
    private var isListening: Boolean = false

    private val inputEventListener =
        object : InputChannelCompat.InputEventListener {
            override fun onInputEvent(ev: InputEvent) {
                val keyEvent: KeyEvent = ev as? KeyEvent ?: return

                inputEventReceiver?.let {
                    if (isBlockingKeys(keyEvent)) {
                        blockForMillis(GATE_DURATION)
                    }
                }
            }
        }

    private fun dispose() {
        inputEventReceiver?.dispose()
        inputMonitor?.dispose()
    }

    private fun isBlockingKeys(keyEvent: KeyEvent): Boolean {
        return blockingKeys.contains(keyEvent.keyCode)
    }

    override fun onActivate() {
        if (isListening) return

        inputMonitor = InputMonitorCompat(TAG, 0)
        inputEventReceiver =
            inputMonitor?.getInputReceiver(
                Looper.getMainLooper(),
                Choreographer.getInstance(),
                inputEventListener,
            )

        isListening = inputEventReceiver != null
        if (isListening) {
            setBlocking(false)
        }
    }

    override fun onDeactivate() {
        isListening = false
        setBlocking(false)
        dispose()
        inputEventReceiver = null
        inputMonitor = null
    }
}
