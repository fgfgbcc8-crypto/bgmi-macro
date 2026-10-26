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
    private var isAutoFiring = false
    private var currentGun = Gun.AKM

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() { stopAuto() }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    // تفعيل أو إيقاف الأتمتة
    fun toggleAuto(gun: Gun) {
        if (isAutoFiring && currentGun == gun) {
            stopAuto()
        } else {
            startAuto(gun)
        }
    }

    private fun startAuto(gun: Gun) {
        stopAuto()
        currentGun = gun
        isAutoFiring = true

        val curve = RecoilCurves.forGun(gun)
        val originX = 540f
        val originY = 1200f

        val runnable = object : Runnable {
            private var index = 0
            private var curX = originX
            private var curY = originY

            override fun run() {
                if (!isAutoFiring) return

                // هنا بنعمل نبضات سحب (سحب سريع ورفع إيد عشان الشاشة متعلقش)
                if (index < curve.size) {
                    val step = curve[index]
                    curX += step.x
                    curY += step.y

                    val path = Path().apply {
                        moveTo(curX - step.x, curY - step.y)
                        lineTo(curX, curY)
                    }
                    val g = GestureDescription.Builder()
                        .addStroke(GestureDescription.StrokeDescription(path, 0L, 50L)) // 50 مللي ثانية
                        .build()
                    dispatchGesture(g, null, null)

                    index++
                    handler.postDelayed(this, 60L)
                } else {
                    // رجوع للأصل
                    index = 0
                    curX = originX
                    curY = originY
                    handler.postDelayed(this, 150L)
                }
            }
        }
        handler.post(runnable)
    }

    fun stopAuto() {
        isAutoFiring = false
    }
}
