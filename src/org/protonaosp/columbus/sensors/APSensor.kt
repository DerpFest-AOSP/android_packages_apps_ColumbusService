package org.protonaosp.columbus.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import org.protonaosp.columbus.actions.*

class APSensor(val context: Context, var sensitivity: Float, val handler: Handler) :
    ColumbusSensor() {
    val samplingIntervalNs: Long = 2400000L

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    val heuristicMode = isHeuristicMode(context)
    val tap: TapRT = TapRT(context, 153600000L)
    val callback: APCallback = APCallback()
    private var isListening: Boolean = false

    override fun isListening(): Boolean {
        return isListening
    }

    fun setListening(listening: Boolean) {
        isListening = listening
    }

    inner class APCallback : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) {
                return
            }

            val evType: Int = event.sensor!!.getType()
            val evArr: FloatArray? = event.values
            if (evArr == null) {
                return
            }
            tap.updateData(
                evType,
                evArr[0],
                evArr[1],
                evArr[2],
                event.timestamp,
                samplingIntervalNs,
                heuristicMode,
            )
            val timing: Int = tap.checkDoubleTapTiming(event.timestamp)
            when (timing) {
                1 -> handler.post { reportGestureDetected(2) }
                2 -> handler.post { reportGestureDetected(1) }
            }
        }

        fun setListening(listening: Boolean, samplingPeriod: Int) {
            if (!listening || accelerometer == null || gyroscope == null) {
                sensorManager.unregisterListener(callback)
                setListening(false)
            } else {
                sensorManager.registerListener(callback, accelerometer, samplingPeriod, handler)
                sensorManager.registerListener(callback, gyroscope, samplingPeriod, handler)
                setListening(true)
            }
        }

        fun updateSensitivity() {
            (tap as? TapRT)?.run {
                positivePeakDetector.minNoiseTolerate = sensitivity
                reset(heuristicMode)
            }
        }
    }

    override fun startListening() {
        callback.setListening(true, SensorManager.SENSOR_DELAY_FASTEST)
        (tap as? TapRT)?.run {
            lowpassAcc.para = 1f
            lowpassGyro.para = 1f
            highpassAcc.para = 0.05f
            highpassGyro.para = 0.05f
            positivePeakDetector.minNoiseTolerate = sensitivity
            positivePeakDetector.windowSize = 64
            negativePeakDetector.minNoiseTolerate = 0.015f
            negativePeakDetector.windowSize = 64
            reset(heuristicMode)
        }
    }

    override fun stopListening() {
        callback.setListening(false, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun updateSensitivity(sensitivity: Float) {
        this.sensitivity = sensitivity
        callback.updateSensitivity()
    }
}
