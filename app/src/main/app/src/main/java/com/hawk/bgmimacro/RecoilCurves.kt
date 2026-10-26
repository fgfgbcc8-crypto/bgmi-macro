package com.hawk.bgmimacro

import android.graphics.PointF

enum class Gun { AKM, M14 }

object RecoilCurves {

    private val akm = listOf(
        PointF(0f,   8f),
        PointF(-1f, 14f),
        PointF(-1f, 20f),
        PointF(0f,  26f),
        PointF(1f,  32f),
        PointF(1f,  38f),
        PointF(0f,  44f),
        PointF(-1f, 50f)
    )

    private val m14 = listOf(
        PointF(0f,   11f),
        PointF(-1f, 19f),
        PointF(-2f, 27f),
        PointF(-1f, 35f),
        PointF(0f,  43f),
        PointF(1f,  51f),
        PointF(1f,  59f),
        PointF(0f,  67f)
    )

    fun forGun(gun: Gun): List<PointF> = when (gun) {
        Gun.AKM -> akm
        Gun.M14 -> m14
    }
}
