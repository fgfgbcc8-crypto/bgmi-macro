package com.hawk.bgmimacro

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class TouchAccessibilityService : AccessibilityService() {

    companion object {
        var instance: TouchAccessibilityService? = null
    }

    private val handler = Handler(Looper.getMainLooper())
    private var recoilJob: Runnable? = null

    var fireX = 1800f
    var fireY = 900f

    var originX = 540f
    var originY = 1200f

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() { stopRecoil() }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun tap(x: Float, y: Float, ms: Long = 35L) {
        val path = Path().apply { moveTo(x, y) }
        val g = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, ms))
            .build()
        dispatchGesture(g, null, null)
    }

    fun startRecoil(gun: Gun) {
        stopRecoil()
        val curve = RecoilCurves.forGun(gun)

        recoilJob = object : Runnable {
            private var index = 0
            private var curX = originX
            private var curY = originY

            override fun run() {
                if (index >= curve.size) return

                val step = curve[index]
                curX += step.x
                curY += step.y

                val path = Path().apply {
                    moveTo(curX - step.x, curY - step.y)
                    lineTo(curX, curY)
                }
                val g = GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, 0L, 70L))
                    .build()
                dispatchGesture(g, null, null)

                index++
                handler.postDelayed(this, 80L)
            }
        }
        handler.post(recoilJob!!)
    }

    fun stopRecoil() {
        recoilJob?.let { handler.removeCallbacks(it) }
        recoilJob = null
    }
}
