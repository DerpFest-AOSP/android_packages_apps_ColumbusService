/*
 * SPDX-FileCopyrightText: TheParasiteProject
 * SPDX-License-Identifier: GPL-3.0
 */

package org.protonaosp.columbus.actions

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager

fun isDeviceInteractiveAndUnlocked(context: Context): Boolean {
    val pm: PowerManager = run {
        val powerService = context.getSystemService(Context.POWER_SERVICE)
        if (powerService !is PowerManager) {
            throw IllegalStateException("Power service not available")
        }
        powerService
    }
    val km: KeyguardManager = run {
        val keyguardService = context.getSystemService(Context.KEYGUARD_SERVICE)
        if (keyguardService !is KeyguardManager) {
            throw IllegalStateException("Keyguard service not available")
        }
        keyguardService
    }
    return pm.isInteractive && !km.isDeviceLocked
}
