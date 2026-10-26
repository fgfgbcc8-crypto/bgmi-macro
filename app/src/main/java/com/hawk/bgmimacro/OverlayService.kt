package com.hawk.bgmimacro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var panel: View
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var floatBtn: TextView
    private lateinit var floatParams: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        startForeground(1, buildNotification())

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // لوحة التحكم الأساسية
        panel = LayoutInflater.from(this).inflate(R.layout.overlay_panel, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 60
            y = 300
        }

        wm.addView(panel, params)
        makeDraggable(panel, params)

        // الزرار الشفاف
        floatBtn = TextView(this).apply {
            text = "F"
            setTextColor(Color.WHITE)
            textSize = 18f
            setBackgroundColor(Color.parseColor("#55000000")) // شفاف
            setPadding(30, 30, 30, 30)
        }
        
        floatParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 800 // مكان مبدئي
            y = 1500 // مكان مبدئي
        }

        wm.addView(floatBtn, floatParams)
        makeDraggable(floatBtn, floatParams)

        // ربط الزرار الشفاف بالأتمتة
        floatBtn.setOnClickListener {
            TouchAccessibilityService.instance?.toggleAuto(Gun.AKM)
            if (floatBtn.text == "F") {
                floatBtn.text = "ON"
                floatBtn.setBackgroundColor(Color.parseColor("#55FF0000"))
            } else {
                floatBtn.text = "F"
                floatBtn.setBackgroundColor(Color.parseColor("#55000000"))
            }
        }

        val btnAkm = panel.findViewById<Button>(R.id.btnAkm)
        val btnM14 = panel.findViewById<Button>(R.id.btnM14)
        val btnStop = panel.findViewById<Button>(R.id.btnRec)
        val btnHide = panel.findViewById<Button>(R.id.btnHide)

        btnAkm.setOnClickListener { TouchAccessibilityService.instance?.toggleAuto(Gun.AKM) }
        btnM14.setOnClickListener { TouchAccessibilityService.instance?.toggleAuto(Gun.M14) }
        
        btnStop.setOnClickListener {
            TouchAccessibilityService.instance?.stopAuto()
            floatBtn.text = "F"
            floatBtn.setBackgroundColor(Color.parseColor("#55000000"))
        }
        
        btnHide.setOnClickListener {
            panel.visibility = View.GONE
        }
    }

    private fun makeDraggable(view: View, layoutParams: WindowManager.LayoutParams) {
        var initialX = 0; var initialY = 0
        var touchX = 0f; var touchY = 0f

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x; initialY = layoutParams.y
                    touchX = event.rawX; touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = initialX + (event.rawX - touchX).toInt()
                    layoutParams.y = initialY + (event.rawY - touchY).toInt()
                    wm.updateViewLayout(view, layoutParams)
                    true
                }
                else -> false
            }
        }
    }

    private fun buildNotification(): Notification {
        val channelId = "macro_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Macro", NotificationManager.IMPORTANCE_LOW)
            )
        }
        return Notification.Builder(this, channelId)
            .setContentTitle("BGMI Macro")
            .setContentText("Overlay running")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
