/*
 * SPDX-FileCopyrightText: The Proton AOSP Project
 * SPDX-FileCopyrightText: TheParasiteProject
 * SPDX-License-Identifier: GPL-3.0
 */

package org.protonaosp.columbus.actions

import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import android.view.WindowManagerGlobal

class PowerMenuAction(context: Context) : Action(context) {
    val wm = WindowManagerGlobal.getWindowManagerService()
    val pm: PowerManager = run {
        val powerService = context.getSystemService(Context.POWER_SERVICE)
        if (powerService !is PowerManager) {
            throw IllegalStateException("Power service not available")
        }
        powerService
    }

    override fun run() {
        if (!pm.isInteractive) {
            pm.wakeUp(
                SystemClock.uptimeMillis(),
                PowerManager.WAKE_REASON_GESTURE,
                "org.protonaosp.columbus:GESTURE",
            )
        }

        wm?.showGlobalActions()
    }
}
